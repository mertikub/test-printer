package al.icitap.testprinter.ui;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Button;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import al.icitap.testprinter.DiscoveredPrinterForDevDemo;
import al.icitap.testprinter.ui.PrinterSettingsModel.SettingsGroup;

public class PrinterSettingsDemo extends PrinterDemoBase {
	
	private final Map<SettingsGroup, TextArea> settingsComponents = new HashMap<>();

	private VerticalLayout contentContainer;

	private Button actionButton;

	public PrinterSettingsDemo() {
	
	}
	
	public void createContent(VerticalLayout contentContainer) {
		this.contentContainer = contentContainer;
		
		VerticalLayout mainPane = new  VerticalLayout();
		mainPane.addComponent(createHeader("Printer Settings"));
		mainPane.addComponent(createSelectPrinterPanel());
		mainPane.addComponent(createLowerPanel());
		
		contentContainer.removeAllComponents();
		contentContainer.addComponent(mainPane);
	}
	
	private TabSheet createLowerPanel() {
		TabSheet tabbed = new TabSheet();
		
		VerticalLayout deviceSettingsTab = createSettingPanel(SettingsGroup.device);
		tabbed.addTab(deviceSettingsTab, "Device");
		
		VerticalLayout printSettingsTab = createSettingPanel(SettingsGroup.print);
		tabbed.addTab(printSettingsTab, "Print");
		
		actionButton = new Button("Refresh");
		actionButton.addClickListener(e -> {
			actionButton.setEnabled(false);
			
			UI.getCurrent().access(() -> {
				DiscoveredPrinterForDevDemo printer = addressDropdown.getValue();
				Map<SettingsGroup, Object[][]> printerSettingsDataMap = new PrinterSettingsModel().getPrinterSettings(printer);
				updatePrinterSettings(printerSettingsDataMap);
				actionButton.setEnabled(true);
			});
		});
		
		printSettingsTab.addComponent(actionButton);
		
		return tabbed;		
	}
	
	protected void updatePrinterSettings(Map<SettingsGroup, Object[][]> printerSettingsDataMap) {
		for(SettingsGroup group : SettingsGroup.values()) {
			updateSettingsGroup(group, printerSettingsDataMap.get(group));
		}		
	}

	private void updateSettingsGroup(SettingsGroup group, Object[][] objects) {
		final TextArea textArea = settingsComponents.get(group);
		
		StringBuilder textAreaModel = new StringBuilder();
		for(int ix = 0; ix < objects.length; ix++ ) {
			textAreaModel.append(objects[ix]);
		}
		
		textArea.setValue(textAreaModel.toString());
	}

	private VerticalLayout createSettingPanel(SettingsGroup group) {
		TextArea area = new TextArea();
		area.setRows(30);
		area.setWidth("300px");
		
		VerticalLayout areaContainer = new VerticalLayout(area);
		settingsComponents.put(group, area);
		
		return areaContainer;
	}
	
	
	private String[] getHeaderLabels(SettingsGroup group) {
		boolean settable = group == SettingsGroup.device;
		String[] headerLabels;

		if (settable) {
			headerLabels = new String[] { "Setting", "Value", "Action" };
		} else {
			headerLabels = new String[] { "Setting", "Value" };
		}

		return headerLabels;
	}

}
