package dev.caua.botxapipdf;

import dev.caua.botxapipdf.globaltec.Globaltec;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotxApiPdfApplication {

	private static Dotenv dotenv;
	private static Globaltec globaltec;

	public static void main(String[] args) {
		// load .env variables
		dotenv = Dotenv.load();
		globaltec = new Globaltec();
		SpringApplication.run(BotxApiPdfApplication.class, args);
	}

	public static Dotenv getDotenv() {
		return dotenv;
	}

	public static Globaltec getGlobaltec() {
		return globaltec;
	}
}
