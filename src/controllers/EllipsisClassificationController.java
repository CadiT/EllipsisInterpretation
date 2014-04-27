package controllers;

import edu.stanford.nlp.trees.*;
import typeClassification.BinaryEllipsisClassifier;
import typeClassification.EllipsisType;
import typeClassification.FeatureGenerator;
import weka.core.Attribute;
import weka.core.FastVector;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Nicole Williams
 *         28/10/13
 *
 * Handles the classification of input by kind of ellipsis, making use of a number of binary classifiers.
 */
public class EllipsisClassificationController {

    Charset charset = Charset.forName("UTF-8");

    protected List<BinaryEllipsisClassifier> binaryClassifiers;
    protected FeatureGenerator featureGenerator;

    FastVector attributes;

    public EllipsisClassificationController(FeatureGenerator featureGenerator){
        binaryClassifiers = new ArrayList<BinaryEllipsisClassifier>();
        this.featureGenerator = featureGenerator;
        attributes = new FastVector();
    }


    /**
     * Initialise some number of binary classifiers, given paths to the .csv files containing training data
     */
    public void initialiseClassifiers(List<String> datasetPaths, List<String> datasetNames, Set<String> featureNames){

        generateAttributes(featureNames);

        for (int i = 0; i < datasetPaths.size(); i++){
            String name = datasetNames.get(i);
            System.out.printf("Initialising classifier %s...%n", name);
            makeNewClassifier(datasetPaths.get(i), name);
            System.out.printf("Initialised %s.", name);
        }

    }

    public EllipsisType findEllipsisType(Tree parse, Collection typedDependencies){
        Map<String, Integer> featureValues = featureGenerator.genFeatures(parse, typedDependencies);

        FastVector convertedFeatures = convert(featureValues);

        List<Boolean> results = new ArrayList<Boolean>();
        for (BinaryEllipsisClassifier classifier : binaryClassifiers){
            boolean result = classifier.classify(convertedFeatures);
            results.add(result);
        }

        //TODO: interpret results

        return EllipsisType.NONE;
    }

    protected void makeNewClassifier(String datasetPath, String datasetName){

        BinaryEllipsisClassifier classifier = new BinaryEllipsisClassifier(attributes, datasetName);

        try{
            BufferedReader reader = Files.newBufferedReader(Paths.get(datasetPath), charset);

            //Read dataset file and add training data to classifier
            String line = reader.readLine();    //read first line i.e. feature names. TODO: does file even need this line?
            while ((line = reader.readLine()) != null){
                FastVector dataItem = convert(line);
                classifier.updateTrainingData(dataItem);
            }

            reader.close();
        } catch (Exception e){
            System.err.format("Exception: %s%n", e);
        }

        binaryClassifiers.add(classifier);
    }

    protected void generateAttributes(Set<String> featureNames){

        for (String s : featureNames){
            if(!s.equals("class")) {
                attributes.addElement(new Attribute(s));
            }
        }
    }

    /**
     * Convert one line of a .csv file into a FastVector of feature/attribute values
     */
    protected FastVector convert(String line){
        FastVector data = new FastVector();
        String[] dataStrings = line.split(",");

        for (String s : dataStrings){
            data.addElement(s.trim());
        }

        return data;
    }

    /**
     * Convert a map of feature name / feature value to a Fastvector of feature values
     */
    protected FastVector convert(Map<String,Integer> features){
        FastVector data = new FastVector();

        for(String k : features.keySet()){
            data.addElement(features.get(k));
        }
        return data;
    }

    /**
     * Wipe current classifier list.
     */
    public void reset(){
        binaryClassifiers = new ArrayList<BinaryEllipsisClassifier>();
        attributes = new FastVector();
    }
}
