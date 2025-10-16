package mail.service.oob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {JdbcRepositoriesAutoConfiguration.class})
@EnableScheduling
public class OobApplication {

	public static void main(String[] args) {
		SpringApplication.run(OobApplication.class, args);
	}

}
