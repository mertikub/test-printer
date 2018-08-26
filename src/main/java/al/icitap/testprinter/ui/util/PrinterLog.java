package al.icitap.testprinter.ui.util;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;

public class PrinterLog extends Label {

	public PrinterLog() {
		super("", ContentMode.HTML);
	}
	
	public PrinterLog append(String line) {
		setValue(getValue()+"</br>"+line);
		return this;
	}
}
