package com.dawanda;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.dawanda.classifier.naivebayes.ClassifierTrainer;
import com.dawanda.classifier.naivebayes.ClassifierValidator;

import java.io.IOException;

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
                new ClassifierTrainer(options.trainDir, options.outputModel).train(options.concurrent);
                break;
            case "validate":
                new ClassifierValidator(options.testDir, options.outputModel).validate();
                break;
            case "label":
                new ClassifierValidator(options.testDir, options.outputModel).label(2);
                break;
            default:
                throw new IllegalArgumentException("Invalid command: " + options.command);
        }
    }

    static class ClassifierOptions {
        private static final String HOME_DIR = System.getProperty("user.home");
        @Parameter(names = {"-c", "--command"}, required = true, description = "Command for the classifier, can be 'train' for training, 'label' for label assignment, or 'validate' for validating the classifier accuracy")
        public String command;

        @Parameter(names = {"-tr", "--trainDir"}, description = "Input directory containing product files for training")
        public String trainDir = HOME_DIR + "/.cbayes/train";

        @Parameter(names = {"-te", "--testDir"}, description = "Input directory containing product files for labeling")
        public String testDir = HOME_DIR + "/.cbayes/test";

        @Parameter(names = {"-o", "--outputModel"}, description = "Output file for the model")
        public String outputModel = HOME_DIR + "/.cbayes/model.json";

        @Parameter(names = {"-m", "--multithreaded"}, description = "Use multi-threaded model (true/false)")
        public boolean concurrent = false;
    }
}
