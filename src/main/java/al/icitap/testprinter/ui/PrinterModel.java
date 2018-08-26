/***********************************************
 * CONFIDENTIAL AND PROPRIETARY
 * 
 * The source code and other information contained herein is the confidential and exclusive property of
 * ZIH Corp. and is subject to the terms and conditions in your end user license agreement.
 * This source code, and any other information contained herein, shall not be copied, reproduced, published, 
 * displayed or distributed, in whole or in part, in any medium, by any means, for any purpose except as
 * expressly permitted under such license agreement.
 * 
 * Copyright ZIH Corp. 2016
 * 
 * ALL RIGHTS RESERVED
 ***********************************************/

package al.icitap.testprinter.ui;

import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Notification.Type;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.common.card.containers.JobStatusInfo;
import com.zebra.sdk.common.card.errors.ZebraCardErrors;
import com.zebra.sdk.common.card.exceptions.ZebraCardException;
import com.zebra.sdk.common.card.printer.ZebraCardPrinter;

import al.icitap.testprinter.ui.util.PrinterLog;

public class PrinterModel {

	public Connection connection = null;
	public static final String USB_SELECTION = "USB";
	public static final String NETWORK_SELECTION = "Network";
	private static final Integer CARD_FEED_TIMEOUT = 30000;
	public static final Integer RESUME_OPTION = 0;

	public PrinterModel() {
		super();
	}

	protected void pollJobStatus(ZebraCardPrinter zebraCardPrinter, int jobId, PrinterLog jobStatusArea) throws ConnectionException, ZebraCardException {
		boolean done = false;
		long start = System.currentTimeMillis();
		
		StringBuilder jobStatusAreaModel = new  StringBuilder();

		jobStatusAreaModel.append("Polling status for job id " + jobId + "...\n");

		while (!done) {
			JobStatusInfo jobStatus = zebraCardPrinter.getJobStatus(jobId);

			String alarmDesc = jobStatus.alarmInfo.value > 0 ? " (" + jobStatus.alarmInfo.description + ")" : "";
			String errorDesc = jobStatus.errorInfo.value > 0 ? " (" + jobStatus.errorInfo.description + ")" : "";

			jobStatusAreaModel.append(String.format("Job %d: status:%s, position:%s, contact:%s, contactless:%s, alarm:%d%s, error:%d%s%n", jobId, jobStatus.printStatus, jobStatus.cardPosition,
					jobStatus.contactSmartCard, jobStatus.contactlessSmartCard, jobStatus.alarmInfo.value, alarmDesc, jobStatus.errorInfo.value, errorDesc));

			if (jobStatus.printStatus.contains("done_ok")) {
				done = true;
			} else if (jobStatus.printStatus.contains("error") || jobStatus.printStatus.contains("cancelled")) {
				if (jobStatus.errorInfo.value > 0) {
					showErrorDialog(jobId, jobStatus);
				} else {
					jobStatusAreaModel.append("Job ID " + jobId + " was cancelled.%n");
				}
				done = true;
			} else if (jobStatus.alarmInfo.value > 0) {
				done = waitForUserInput(zebraCardPrinter, jobId, jobStatus.alarmInfo.description);
			} else if (jobStatus.errorInfo.value > 0) {
				zebraCardPrinter.cancel(jobId);
				showErrorDialog(jobId, jobStatus);
				done = true;
			} else if ((jobStatus.printStatus.contains("in_progress") && jobStatus.cardPosition.contains("feeding")) // ZMotif printers
					|| (jobStatus.printStatus.contains("alarm_handling") && jobStatus.alarmInfo.value == ZebraCardErrors.MEDIA_OUT_OF_CARDS)) { // ZXP printers
				if (System.currentTimeMillis() > start + CARD_FEED_TIMEOUT) {
					zebraCardPrinter.cancel(jobId);
					jobStatusAreaModel.append("Job ID " + jobId + " was cancelled.%n");
					done = true;
				}
			}
			
			jobStatusArea.setValue(jobStatusAreaModel.toString());

			if (!done) {
				sleep(500);
			}
		}
	}

	private void showErrorDialog(int jobId, JobStatusInfo jobStatus) {
		Notification.show("\"Printer Error Encountered: \" + jobStatus.errorInfo.description + \"\\nJob \" + jobId + \" was canceled.\"", Type.ERROR_MESSAGE);

	}

	public static void showInformationDialog(String title, String message) {
		Object[] options = { "Okay" };
		showInformationDialog(options, title, message);
	}

	public static int showInformationDialog(Object[] options, String title, String message) {
//		return JOptionPane.showOptionDialog(null, , title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, null);
		return 0;
	}

	public static boolean waitForUserInput(ZebraCardPrinter zebraCardPrinter, int jobId, String alarmDesc) throws ZebraCardException, ConnectionException {
		Object[] options = { "Resume", "Cancel" };
		int option = showInformationDialog(options, "Alarm Encountered",
				"The job encountered an alarm [" + alarmDesc + "].\nEither fix the alarm and Click Resume once the job begins again,\nor select cancel to cancel the job.");

		if (option != RESUME_OPTION) {
			zebraCardPrinter.cancel(jobId);
			return true;
		}

		return false;
	}

	public static void cleanUpQuietly(ZebraCardPrinter zebraCardPrinter, Connection connection) {
		try {
			if (zebraCardPrinter != null) {
				zebraCardPrinter.destroy();
			}
		} catch (ZebraCardException e) {
		}

		try {
			if (connection != null) {
				connection.close();
			}
		} catch (ConnectionException e) {
		}
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
}