package al.icitap.testprinter.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.zebra.sdk.common.card.printer.discovery.NetworkCardDiscoverer;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveredUsbPrinter;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;
import com.zebra.sdk.printer.discovery.UsbDiscoverer;

import al.icitap.testprinter.DiscoveredPrinterForDevDemo;

public class PrinterDemoBase {
	
	public PrinterDemoBase() {
		super();
	}
	
	protected ComboBox<String> connectionTypeCombobox;
	protected static ComboBox<DiscoveredPrinterForDevDemo> addressDropdown;
	protected Button discoveryButton;
	
	protected Label createHeader(String demoTitle) {
		Label label = new Label(demoTitle, ContentMode.HTML);
		label.setStyleName(ValoTheme.LABEL_H3);
		return label;
	}
	
	protected Panel createSelectPrinterPanel() {
		Panel selectedPrinterPanel = new Panel();
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setMargin(true);
		selectedPrinterPanel.setContent(panelLayout);
		
		panelLayout.addComponent(createConnectionArea());
		panelLayout.addComponent(createPrinterAddressArea());		
		
		return selectedPrinterPanel;
	}
	
	private CssLayout createConnectionArea() {
		CssLayout connectionArea = new CssLayout();
		connectionArea.setWidth("30%");
		
		connectionTypeCombobox = new  ComboBox<String>("Connection");
		connectionTypeCombobox.setItems(PrinterModel.NETWORK_SELECTION, PrinterModel.USB_SELECTION);
		connectionTypeCombobox.addSelectionListener(e -> {
			
		});
		
		FormLayout connectionTypeLayout = new FormLayout(connectionTypeCombobox);
		
		connectionArea.addComponent(connectionTypeLayout);
		return connectionArea;
	}
	
	private CssLayout createPrinterAddressArea() {
		CssLayout addressArea = new CssLayout();		
		addressArea.setWidth("70%");
		
		addressDropdown = new ComboBox<DiscoveredPrinterForDevDemo>("Printer");
		addressDropdown.setWidth("300px");
		addressDropdown.setItems(new DiscoveredPrinterForDevDemo(createPrototypeForCombobox()));
		addressArea.addComponent(addressDropdown);
		
		discoveryButton = new Button();
		discoveryButton.setIcon(VaadinIcons.REFRESH);
		addressArea.addComponent(discoveryButton);
		
		
		
		discoveryButton.addClickListener(e -> {
			discoveryButton.setEnabled(false);
			UI.getCurrent().access(() -> {
				discoverPrinters();
			});
		});
		
		
		return addressArea;
	}
	
	private void discoverPrinters() {
		List<DiscoveredPrinterForDevDemo> discoveredPrinters = new ArrayList<>();
		
		DiscoveryHandler discoveryHandler = new DiscoveryHandler() {
			
			@Override
			public void foundPrinter(DiscoveredPrinter printer) {

				String model = printer.getDiscoveryDataMap().get("MODEL");
				
				if(model.contains("ZXP")) {
					discoveredPrinters.add(new DiscoveredPrinterForDevDemo(printer));
				}	
				
			}
			
			@Override
			public void discoveryFinished() {
				addressDropdown.setItems(discoveredPrinters);
				
			}
			
			@Override
			public void discoveryError(String message) {
				Notification.show("Unable to discover printers : "+message);
			}
		};
		
		try {
			if(connectionTypeCombobox.getValue().equals(PrinterModel.NETWORK_SELECTION)) {
				NetworkCardDiscoverer.findPrinters(discoveryHandler);
			} else {
				for(DiscoveredUsbPrinter discoPrinter : UsbDiscoverer.getZebraUsbPrinters()) {
					discoveryHandler.foundPrinter(discoPrinter);
				}
				discoveryHandler.discoveryFinished();
			}
		} catch (Exception e) {
			Notification.show("Unable to discover printers : " + e.getLocalizedMessage(), Type.ERROR_MESSAGE);
		} finally {
			discoveryButton.setEnabled(true);
		}
		
	}

	private DiscoveredUsbPrinter createPrototypeForCombobox() {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("MODEL", "");

		StringBuffer eightyChars = new StringBuffer(); // Wide enough to hold Card Printer usb address
		for (int ix = 1; ix <= 8; ix++) {
			eightyChars.append("HHHHHHHHHH");
		}

		return new DiscoveredUsbPrinter(eightyChars.toString(), attributes);
	}

}
