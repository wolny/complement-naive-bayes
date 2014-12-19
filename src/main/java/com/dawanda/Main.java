package com.dawanda;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.dawanda.classifier.naivebayes.ClassifierTrainer;
import com.dawanda.classifier.naivebayes.ClassifierValidator;

import java.io.IOException;

/**
 * Created by awolny on 18/12/14.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        ClassifierOptions options = new ClassifierOptions();
        JCommander jCommander = new JCommander(options);
        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            jCommander.usage();
            System.exit(1);
        }

        switch (options.command) {
            case "train":
                new ClassifierTrainer(options.inputDir, options.outputModel).train();
                break;
            case "validate":
                new ClassifierValidator(options.inputDir, options.outputModel).validate(0.05);
                break;
            case "label":
                new ClassifierValidator(options.inputLabel, options.outputModel).label(2);
                break;
            default:
                throw new IllegalArgumentException("Invalid command: " + options.command);
        }
    }

    static class ClassifierOptions {
        private static final String HOME_DIR = System.getProperty("user.home");
        @Parameter(names = {"-c", "--command"}, required = true, description = "Command for the classifier, can be 'train' for training, 'label' for label assignment, or 'validate' for validating the classifier accuracy")
        public String command;

        @Parameter(names = {"-id", "--inputDir"}, description = "Input directory containing product files for training or testing/validation")
        public String inputDir = HOME_DIR + "/.cbayes/products";

        @Parameter(names = {"-il", "--inputLabel"}, description = "Input directory containing product files for labeling")
        public String inputLabel = HOME_DIR + "/.cbayes/test";

        @Parameter(names = {"-om", "--outputModel"}, description = "Output file for the model")
        public String outputModel = HOME_DIR + "/.cbayes/model.json";
    }
}
