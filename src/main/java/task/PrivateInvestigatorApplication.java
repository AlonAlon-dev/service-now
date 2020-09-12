package task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;

/**
 * Main private investigator class.
 */
@PropertySource("classpath:paths.properties")
@SpringBootApplication
public class PrivateInvestigatorApplication {

    @Autowired
    private InvestigationExtractor investigationExtractor;

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(PrivateInvestigatorApplication.class, args);
        ctx.getBean(PrivateInvestigatorApplication.class).start();
    }

    public void start(){
        investigationExtractor.execute();
    }
}
