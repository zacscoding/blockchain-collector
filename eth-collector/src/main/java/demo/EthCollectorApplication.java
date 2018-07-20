package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class EthCollectorApplication {

    private static ApplicationContext CTX;

    public static void main(String[] args) {
        CTX = SpringApplication.run(EthCollectorApplication.class, args);
    }

    public static void autowiredBean(Object inst) {
        if (inst == null) {
            return;
        }

        if (CTX == null) {
            throw new RuntimeException("Not loaded spring app context");
        }

        CTX.getAutowireCapableBeanFactory().autowireBean(inst);
    }
}