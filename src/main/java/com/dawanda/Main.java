package com.dawanda;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * Created by awolny on 18/12/14.
 */
public class Main {
    public static void main(String[] args) {
        ClassifierOptions options = new ClassifierOptions();
        JCommander jCommander = new JCommander(options);
        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            jCommander.usage();
            System.exit(1);
        }

        // TODO:
    }

    static class ClassifierOptions {
        @Parameter(names = {"-c", "--command"}, required = true, description = "Command for the classifier, can be 'train' for training or 'label' for label assignment")
        public String command = "train";

        @Parameter(names = {"-it", "--inputTrain"}, description = "Input directory containing product files for training")
        public String inputTrain = "~/.cbayes/products";

        @Parameter(names = {"-il", "--inputLabel"}, description = "Input directory containing product files for labeling")
        public String inputLabel = "~/.cbayes/test";

        @Parameter(names = {"-om", "--outputModel"}, description = "Output file for the model")
        public String outputModel = "~/.cbayes/model.json";

        @Parameter(names = {"-ol", "--outputLabel"}, description = "Input directory containing product files for labeling")
        public String labelResult = "~/.cbayes/labelResult.json";
    }
}
