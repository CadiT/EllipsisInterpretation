package testing;

import controllers.EllipsisClassificationController;
import controllers.ParsingController;
import typeClassification.FeatureGenerator;
import weka.core.FastVector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         27/04/14
 */
public class CrossValidator {

    Charset charset = Charset.forName("UTF-8");

    int n;          //n-fold crossval
    SingleClassifierController classificationController;

    final String TRAIN_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\crossval\\training.csv";
    final String TEST_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\crossval\\testing.txt";

    List<String> datasetPaths;
    List<String> datasetNames;
    String baseName;

    Set<String> featureNames;

    List<Float> precision;
    List<Float> recall;

    /**
     * @param n                n-fold cross-validation e.g. n=10 for 10-fold crossval
     * @param name             String to identify classifier being crossval'ed
     * @param featureGenerator feature generator
     */
    public CrossValidator(int n, String name, FeatureGenerator featureGenerator, ParsingController parser) {
        this.n = n;
        classificationController = new SingleClassifierController(featureGenerator);
        featureNames = featureGenerator.getFeatureNames();

        datasetPaths = new ArrayList<String>();
        datasetPaths.add(TRAIN_PATH);

        datasetNames = new ArrayList<String>();
        baseName = "crossval-%d"+name;
    }

    /**
     *
     * @param dataPath      Path to file containing pre-processed data
     */
    public void validateClassifier(String dataPath) {

        //Perform n rounds of cross-validation
        for (int round = 0; round < n; round++) {
            buildDataSets(round, dataPath);

            //Customise dataset name for this round
            datasetNames.set(0,String.format(baseName,round));

            classificationController.initialiseClassifiers(datasetPaths, datasetNames, featureNames);

            float accuracy = classifyTestData();

            //Reset classification controller for re-use in next round
            classificationController.reset();
        }

    }

    /**
     * Split data into two datasets with ratio n-1:1 training:test
     * @param round         which round of cross-val are we in? Determines how file is split
     * @param dataPath      path to dataset file to be split
     */
    private void buildDataSets(int round, String dataPath) {
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(dataPath), charset);
            BufferedWriter trainWriter = Files.newBufferedWriter(Paths.get(TRAIN_PATH), charset);
            BufferedWriter testWriter = Files.newBufferedWriter(Paths.get(TEST_PATH), charset);

            //Clear dataset files before beginning
            trainWriter.write("");
            testWriter.write("");

            String line = reader.readLine();        //read header line
            int linesRead = 0;
            //Send 1/nth of data to test file, rest to training file. Different 1/nth sent to test each round.
            while ((line=reader.readLine()) != null){

                System.out.println("Reading line "+linesRead);

                if ((linesRead % n) == round){
                    testWriter.append(line);
                    System.out.println("test data");
                } else {
                    trainWriter.append(line);
                    System.out.println("training data");
                }

                linesRead++;
            }

            reader.close();
            trainWriter.close();
            testWriter.close();

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }


    private void classifyTestData(){
        try{
            BufferedReader reader = Files.newBufferedReader(Paths.get(TEST_PATH), charset);

            int total = 0;          //total number of data items
            int conditionPos = 0;   //number of data items with true class "true"
            int testPos = 0;        //number of data items classified as "true"
            int truePos = 0;        //number of data items *correctly* classified as "true"
            int correct = 0;        //number of data items correctly classified

            String line;
            while ((line = reader.readLine()) != null){

                //Detach "true" or "false" class from beginning of line
                String classification;
                String classlessLine;
                if(line.trim().startsWith("t")){
                    classification = line.substring(0,6).trim();
                    classlessLine = line.substring(5).trim();
                } else {
                    classification = line.substring(0,7).trim();
                    classlessLine = line.substring(6).trim();
                }

                //Classify item
                boolean assignedClass = classificationController.classifyDataItem(classlessLine);

                boolean trueClass;
                if(classification.equals("true")){
                    trueClass = true;
                    conditionPos++;
                } else {
                    trueClass = false;
                }

                if (assignedClass == true){
                    testPos++;
                }

                if (assignedClass == trueClass){
                    correct++;
                    if(assignedClass == true){
                        truePos++;
                    }
                }


                total++;
            }

            //record precision and recall
            precision.add(((float) truePos)/testPos);
            recall.add(((float) truePos)/conditionPos);


        } catch (IOException e){
            System.err.format("IOException: %s%n", e);
        }

    }

}
