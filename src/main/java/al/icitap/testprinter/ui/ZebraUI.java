package al.icitap.testprinter.ui;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SpringUI
@Push(transport=Transport.WEBSOCKET_XHR)
public class ZebraUI extends UI {

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		
		PrinterSettingsDemo settingsDemo = new PrinterSettingsDemo();
		settingsDemo.createContent(layout);
		
	}
	

}
