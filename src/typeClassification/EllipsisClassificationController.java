package typeClassification;

import edu.stanford.nlp.trees.*;
import weka.core.Attribute;
import weka.core.FastVector;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Nicole Williams
 *         28/10/13
 *
 * Handles the classification of input by kind of ellipsis, making use of a number of binary classifiers.
 */
public class EllipsisClassificationController {

    Charset charset = Charset.forName("UTF-8");

    List<BinaryEllipsisClassifier> binaryClassifiers = new ArrayList<BinaryEllipsisClassifier>();

    FastVector attributes;
    FastVector classValues;

    /**
     * Initialise some number of binary classifiers, given paths to the .csv files containing training data
     */
    public void initialiseClassifiers(List<String> datasetPaths, List<String> datasetNames, Set<String> featureNames){

        generateAttributes(featureNames);

        for (int i = 0; i < datasetPaths.size(); i++){
            makeNewClassifier(datasetPaths.get(i), datasetNames.get(i));
        }

    }

    public EllipsisType findEllipsisType(Tree parse, Collection typedDependencies){
        //TODO: Given a parse and dependency parse, classify the input. Use confidence values?
        return EllipsisType.NONE;
    }

    private void makeNewClassifier(String datasetPath, String datasetName){

        BinaryEllipsisClassifier classifier = new BinaryEllipsisClassifier(attributes, datasetName);

        try{
            BufferedReader reader = Files.newBufferedReader(Paths.get(datasetPath), charset);

            //Read dataset file and add training data to classifier
            String line = reader.readLine();    //read first line i.e. feature names. TODO: does file even need this line?
            int dataItemsRead = 0;
            while ((line = reader.readLine()) != null){
                FastVector dataItem = convert(line);
                classifier.makeInstance(dataItem);
            }
        } catch (Exception e){
            System.err.format("IOException: %s%n", e);
        }

        binaryClassifiers.add(classifier);
    }

    private void generateAttributes(Set<String> featureNames){

        for (String s : featureNames){
            if(s.equals("class")){
                attributes.addElement(new Attribute("class",classValues));
            } else {
                attributes.addElement(new Attribute(s));
            }
        }
    }

    /**
     * Convert one line of a .csv file into a FastVector of feature/attribute values
     */
    private FastVector convert(String line){
        FastVector data = new FastVector();
        String[] dataStrings = line.split(", ");

        for (String s : dataStrings){
            data.addElement(s);
        }


        return data;
    }
}
