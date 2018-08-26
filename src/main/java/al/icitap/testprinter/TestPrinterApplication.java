package al.icitap.testprinter;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import al.icitap.testprinter.data.Employee;
import al.icitap.testprinter.data.EmployeeRepository;
import al.icitap.testprinter.ui.ZebraUI;

@SpringBootApplication(scanBasePackageClasses= {ZebraUI.class}, scanBasePackages= {"al.icitap.testprinter"})
public class TestPrinterApplication  extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TestPrinterApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner createEmployeeList(EmployeeRepository repo) {
		return args -> {
			repo.save(new Employee("JOHN", "SINA", LocalDate.now().minusYears(40), "M000123"));
			repo.save(new Employee("JONNY", "BRAVO", LocalDate.now().minusYears(30).minusMonths(2), "M000124"));
			repo.save(new Employee("JOHN", "SINA", LocalDate.now().minusYears(40), "M000125"));
			repo.save(new Employee("JOHN", "SINA", LocalDate.now().minusYears(40), "M000126"));
			repo.save(new Employee("JOHN", "SINA", LocalDate.now().minusYears(40), "M000127"));
		};
	}
}
