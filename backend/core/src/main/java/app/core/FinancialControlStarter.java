package app.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinancialControlStarter {

    /**
     * Запуск приложения.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(FinancialControlStarter.class, args);
    }
}