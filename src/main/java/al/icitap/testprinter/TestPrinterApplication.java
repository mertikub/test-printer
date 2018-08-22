package al.icitap.testprinter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import al.icitap.testprinter.ui.ZebraUI;

@SpringBootApplication(scanBasePackageClasses= {ZebraUI.class})
public class TestPrinterApplication  extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TestPrinterApplication.class, args);
	}
}
