package heeyoung.soldier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SoldierApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoldierApplication.class, args);
	}

}
