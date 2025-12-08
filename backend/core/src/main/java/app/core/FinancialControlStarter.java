package app.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class FinancialControlStarter {
    //TODO: java doc + swagger (ru+eng)
    /**
     * Запуск приложения.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        //TODO: исправить когда будем делать локализацию (создать бин)
        Locale.setDefault(Locale.ENGLISH);
        SpringApplication.run(FinancialControlStarter.class, args);
    }
}