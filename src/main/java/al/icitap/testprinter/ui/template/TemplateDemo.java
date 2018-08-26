package al.icitap.testprinter.ui.template;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.zebra.sdk.common.card.containers.GraphicsInfo;
import com.zebra.sdk.common.card.graphics.ZebraGraphicsI;

import al.icitap.testprinter.data.Employee;
import al.icitap.testprinter.data.EmployeeRepository;
import al.icitap.testprinter.ui.PrinterDemoBase;

@SpringComponent
public class TemplateDemo extends PrinterDemoBase {

	
	private VerticalLayout middlePanelContent;
	
	@Autowired
	private EmployeeRepository employeeRepository;

	public void createContent(VerticalLayout contentContainer) {
		
		VerticalLayout mainPane = new VerticalLayout();
		mainPane.addComponent(createHeader("Template"));
		mainPane.addComponent(createTopPanel());
		
		contentContainer.removeAllComponents();
		contentContainer.addComponent(mainPane);
	}
	
	private Panel createTopPanel() {
		Panel topPanel = new Panel();
		VerticalLayout panelContent = new VerticalLayout();
		topPanel.setContent(panelContent);
		panelContent.addComponent(createSelectPrinterPanel());
		panelContent.addComponent(createSelectEmployeePanel());
		panelContent.addComponent(createMiddlePanel());
		panelContent.addComponent(createBottomPanel());
		return topPanel;
	}
	
	private Panel createMiddlePanel() {
		Panel middlePanel = new Panel();
		middlePanelContent = new VerticalLayout();
		middlePanel.setContent(middlePanelContent);		
		
		return middlePanel;
	}
	
	private Panel createBottomPanel() {
		Panel bottomPanel = new Panel();
		VerticalLayout panelContent = new VerticalLayout();
		bottomPanel.setContent(panelContent);
		panelContent.addComponent(createPrinterLog(600));
		return bottomPanel;
	}

	private Component createSelectEmployeePanel() {
		FormLayout employeesContainer = new FormLayout();
		ComboBox<Employee> empCombobox = new ComboBox<Employee>("Select Employee");
		empCombobox.setItems(employeeRepository.findAll());
		empCombobox.setItemCaptionGenerator(e -> String.format("%s %s, %s", e.getName(), e.getSurname(), e.getEmployeeId()));
		employeesContainer.addComponent(empCombobox);
		
		empCombobox.addValueChangeListener(event -> {
			empCombobox.getOptionalValue().ifPresent(emp -> generatePreview(emp));
		});
		
		return employeesContainer;
	}

	private void generatePreview(Employee emp) {
		ClassLoader classLoader = getClass().getClassLoader();
		File templateFile = new File(classLoader.getResource("templates/Template2.xml").getFile());
		if(!templateFile.exists()) {
			Notification.show("No template found");
		} else {
			try {
				TemplateModel templateModel = new TemplateModel(templateFile.getPath(), templateFile.getParent(), printerLog);
			
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.registerModule(new JavaTimeModule());
				objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
				
				Map<String, String> variableData = objectMapper.convertValue(emp, Map.class);
			
				List<GraphicsInfo> graphicsData = templateModel.generatePreview(variableData);
				VerticalLayout imageHolder = new  VerticalLayout();
				
				graphicsData.forEach(info -> {
					Label imageLabel = new Label(info.side + " " + info.printType);
					imageHolder.addComponent(imageLabel);
					
					byte[] loopImageData = info.graphicData.getImageData();
					Image loopImage = labelFromImageData(loopImageData);
					imageHolder.addComponent(loopImage);
				});
				
				middlePanelContent.removeAllComponents();
				middlePanelContent.addComponent(imageHolder);
				
			} catch (Exception e) {
				e.printStackTrace();
				Notification.show("Could not generate preview : "+e.getMessage());
			}
		}
		
	}
	
	private Image labelFromImageData(byte[] imageData) {
		StreamSource source = new StreamSource() {			
			@Override
			public InputStream getStream() {
				return new ByteArrayInputStream(imageData);
			}
		};
		StreamResource resource = new StreamResource(source, "");
		Image image = new Image("", resource);		
		return image;
	}
}
