package dev.caua.botxapipdf;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotxApiPdfApplication {

	private static Dotenv dotenv;

	public static void main(String[] args) {
		// load .env variables
		dotenv = Dotenv.load();
		SpringApplication.run(BotxApiPdfApplication.class, args);
	}

	public static Dotenv getDotenv() {
		return dotenv;
	}

}
