package task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reads the input file, groups together similar sentences
 * (sentences where only a single word has changed)
 * and extracts the changes, then outputs them to a file.
 */
@Service
public class InvestigationExtractor {

    private static final Logger log = LoggerFactory.getLogger(InvestigationExtractor.class);

    @Autowired
    private Environment env;

    private String inputFileName;
    private String outputFileName;

    // A linked hash map is used in order to guarantee that the natural ordering of the log entries will be preserved.
    private Map<String, List<String>> patternSentencesMap;

    public InvestigationExtractor() {
        patternSentencesMap = new LinkedHashMap<>();
    }

    @PostConstruct
    private void postConstruct() {
        inputFileName = env.getProperty("input.text.file.path");
        outputFileName = env.getProperty("output.text.file.path");
    }

    /**
     * Reads the input file, groups together similar sentences
     * (sentences where only a single word has changed)
     * and extracts the changes, then outputs them to a file.
     */
    public void execute(){
        // Read the input file into stream using try with resources.
        //
        // For each sentence:
        // 1. Extract the pattern.
        // 2. Add it to the pattern sentences map according to its pattern.
        try (Stream<String> stream = Files.lines(Paths.get(inputFileName))) {
            stream.forEach(sentence -> {
                // Extract the pattern.
                String[] substrings = sentence.split(" ", 4);
                String pattern = substrings[substrings.length - 1];

                // Look in the map and try to find the pattern.
                // If the patten is not found, then create a new entry with the pattern as the key and the sentence as the first sentence.
                // If the pattern is found then append the sentence to the existing sentences.
                List<String> sentences = patternSentencesMap.computeIfAbsent(pattern, value -> new ArrayList<>());
                sentences.add(sentence);
            });
        } catch (IOException e) {
            log.error("Failed reading from input file " + inputFileName, e);
        }

        // Write to the output file using try with resources.
        // A pattern will be extracted only if detected in more than 1 sentence.
        //
        // For each group of similar sentences:
        // 1. Print the similar sentences.
        // 2. Print the changing words.
        try(FileWriter fileWriter = new FileWriter(outputFileName);
            PrintWriter printWriter = new PrintWriter(fileWriter)) {
            patternSentencesMap.values()
                    .stream()
                    .filter(sentences -> sentences.size() > 1)
                    .forEach(sentences -> {
                        // Print the similar sentences.
                        sentences
                                .stream()
                                .forEach(printWriter::println);

                        // Extract the changing words.
                        String changingWords = sentences
                                .stream()
                                .map(sentence -> {
                                    String[] substrings = sentence.split(" ", 4);
                                    String changingWord = substrings[substrings.length - 2];
                                    return changingWord;
                                })
                                .collect(Collectors.joining(", "));

                        // Print the changing words.
                        printWriter.println("The changing word was: " + changingWords);

                        // Newline.
                        printWriter.println();
                    });
        } catch (IOException e) {
            log.error("Failed writing to output file " + outputFileName, e);
        }
    }
}
