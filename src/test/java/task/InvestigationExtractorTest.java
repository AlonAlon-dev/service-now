package task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { InvestigationExtractor.class })
@TestPropertySource(properties = { "input.text.file.path=input.txt", "output.text.file.path=output.txt"})
public class InvestigationExtractorTest {

    @Autowired
    private InvestigationExtractor investigationExtractor;

    @Test
    public void executeTest(){
        investigationExtractor.execute();
    }
}