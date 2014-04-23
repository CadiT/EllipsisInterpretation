package controllers;

import dataExtraction.DatasetBuilder;
import typeClassification.FeatureGenerator;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         Date: 17/10/13
 */
public class MainController {


    public static final String FEATURE_NAMES_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\featureNames.txt";

    public static final String NPE_RAW_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\NPE-all.txt";
    public static final String NPE_PROCESSED_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\vectors-NPE.csv";
    public static final String VPE_RAW_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\VPE.txt";
    public static final String VPE_PROCESSED_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\vectors-VPE.csv";
    public static final String NSU_RAW_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\NSUs.txt";
    public static final String NSU_PROCESSED_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\vectors-NSU.csv";

    public static boolean datasetsToBeBuilt = true;

    public static void main(String[] args) {

        ParsingController parser = new ParsingController();

        //Build datasets for each kind of ellipsis
        if (datasetsToBeBuilt) {
            DatasetBuilder npeDatasetBuilder = new DatasetBuilder(new FeatureGenerator(FEATURE_NAMES_PATH) {
            }, NPE_RAW_PATH, NPE_PROCESSED_PATH, parser);
            DatasetBuilder vpeDatasetBuilder = new DatasetBuilder(new FeatureGenerator(FEATURE_NAMES_PATH) {
            }, VPE_RAW_PATH, VPE_PROCESSED_PATH, parser);
            DatasetBuilder nsuDatasetBuilder = new DatasetBuilder(new FeatureGenerator(FEATURE_NAMES_PATH) {
            }, NSU_RAW_PATH, NSU_PROCESSED_PATH, parser);

            npeDatasetBuilder.buildDataset();
            System.out.println("Build NPE dataset.");
            vpeDatasetBuilder.buildDataset();
            System.out.println("Build VPE dataset.");
            nsuDatasetBuilder.buildDataset();
            System.out.println("Build NSU dataset.");
        }

        /*
        if (args.length > 0){
            String filename = args[0];

            ParsingController parser = new ParsingController();
            FeatureGenerator fGen = new FeatureGenerator();

            for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
                Tree parse = parser.getParse(sentence);
                Collection typedDependencies = parser.getDependencies(parse);

                System.out.println(sentence + " " + sentence.size());

                fGen.genFeatures(parse,typedDependencies);

                System.out.println("## Features: ##");
                fGen.printFeatures();
                fGen.reset();
            }
        }
        */
    }

}
