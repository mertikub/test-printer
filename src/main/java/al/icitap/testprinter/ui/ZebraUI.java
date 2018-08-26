package al.icitap.testprinter.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import al.icitap.testprinter.ui.template.TemplateDemo;

@SpringUI
@Push(transport=Transport.WEBSOCKET_XHR)
public class ZebraUI extends UI {
	
	@Autowired
	private TemplateDemo templateDemo;

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		
/*		PrinterSettingsDemo settingsDemo = new PrinterSettingsDemo();
		settingsDemo.createContent(layout);*/
		
		templateDemo.createContent(layout);
		
	}
	

}
