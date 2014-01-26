package typeClassification;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         02/11/13
 */
public class FeatureGenerator {

    public static final String START_MARKER = "START";
    Map<String, Integer> featureList = new LinkedHashMap<String, Integer>();
    String[] punctuationArray = {".", ",", "'", "\"", "-", "/", "\\", "(", ")", "!", "?", ":", ";"};
    List<String> punctuation = Arrays.asList(punctuationArray);

    public Map<String, Integer> initialiseFeatures() {
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get("C:\\DataFiles\\Programming\\4th Year Project - Ellipsis Interpretation\\PTBtags.txt"),
                    Charset.forName("UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                featureList.put(line.trim(), 0);
            }

            reader.close();
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        return featureList;
    }

    //returns list of features, populated with correct values
    public Map<String, Integer> genFeatures(Tree parse, Collection typedDependencies) {

        List<Word> words = parse.yieldWords();
        List<TaggedWord> tagWords = parse.taggedYield();
        tagWords.add(0, new TaggedWord("", START_MARKER));     //special start-of-sentence marker

        //Sentence length
        getSentenceLength(words);

        //POS counts
        getPOSCounts(tagWords);

        //POS pair counts
        getPOSPairCounts(tagWords);

        //Exists conjunction?
        existsConjunction(tagWords);

        //WP-final?
        isWPFinal(tagWords);

        //TODO: Implementation, more features
        return featureList;
    }

    /**
     * Returns the length of the sentence represented by the given parse tree.
     *
     * @param words The words making up the given sentence.
     * @return The sentence length.
     */
    private void getSentenceLength(List<Word> words) {
        int size = words.size();
        for (Word w : words) {
            boolean isPunctuation = punctuation.contains(w.value().trim());
            if (isPunctuation) {
                size -= 1;
            }
        }
        featureList.put("sentence length", size);
    }

    /**
     * Generates features for number of each kind of POS tag.
     * (Note POS tags include punctuation tags.)
     *
     * @param tagWords Words & associated POS tags for given sentence.
     */
    private void getPOSCounts(List<TaggedWord> tagWords) {
        Map<String, Integer> tagCounts = new HashMap<String, Integer>();
        for (TaggedWord tw : tagWords) {
            String key = tw.tag();
            if (!key.equals(START_MARKER)) {
                if (tagCounts.containsKey(key)) {
                    tagCounts.put(key, tagCounts.get(key) + 1);
                } else {
                    tagCounts.put(key, 1);
                }
            }
        }
        for (String tag : tagCounts.keySet()) {
            if (featureList.containsKey(tag)) {
                featureList.put(tag, tagCounts.get(tag));
            } else {
                System.out.println("tried to add different key " + tag);
            }
        }
    }

    /**
     * Generates features for pairs of neighbouring POS tags.
     *
     * @param tagWords Words & associated POS tags for up given sentence.
     */
    private void getPOSPairCounts(List<TaggedWord> tagWords) {
        Map<String, Integer> tagCounts = new HashMap<String, Integer>();

        for (int i = 0; i < tagWords.size() - 1; i++) {
            String pair = tagWords.get(i).tag() + "/" + tagWords.get(i + 1).tag();
            if (tagCounts.containsKey(pair)) {
                tagCounts.put(pair, tagCounts.get(pair) + 1);
            } else {
                tagCounts.put(pair, 1);
            }
        }
        for (String key : tagCounts.keySet()) {
            if (featureList.containsKey(key)) {
                featureList.put(key, tagCounts.get(key));
            } else {
                System.out.println("tried to add different key " + key);
            }
        }

    }

    /**
     * Determines whether the given sentence contains a conjunction.
     *
     * @param tagWords Words & associated POS tags for up given sentence.
     * @return 1 if there is a conjunction, 0 otherwise
     */
    //TODO: Differentiate between prep & sub-conj ("IN")
    private void existsConjunction(List<TaggedWord> tagWords) {
        for (TaggedWord tw : tagWords) {
            if (tw.tag().equals("CC") || tw.tag().equals("IN")) {
                featureList.put("exists conjunction", 1);
            }
        }
    }

    private void existsNegFinalVP(Tree parse) {
        //TODO: Implementation
    }

    /**
     * Is the last word of the sentence a Wh-pronoun?
     *
     * @param tagWords Words & associated POS tags for up given sentence.
     * @return 1 if true, 0 otherwise
     */
    private void isWPFinal(List<TaggedWord> tagWords) {
        int finalIndex = tagWords.size() - 1;
        TaggedWord finalWord = tagWords.get(finalIndex);
        while (punctuation.contains(finalWord.tag())) {
            tagWords.remove(finalIndex);
            finalIndex = tagWords.size() - 1;
            finalWord = tagWords.get(finalIndex);
        }
        if (finalWord.tag().equals("WP")) {
            featureList.put("WP-final", 1);
        } else {
        }
    }

    /**
     * Do phrases like "does so", "does too", "doesn't" occur in the given sentence?
     *
     * @param words
     * @param tagWords
     * @return
     */
    private void existsDoesPhrase(List<Word> words, List<TaggedWord> tagWords) {
        //TODO: Implementation
    }

    /**
     * Return a list of elements which encode POS in the given sentence augmented with
     * information from their ancestors in the parse tree.
     *
     * @param parse parse tree of given sentence
     * @return list of augmented POS tags
     */
    private void getAugmentedPOS(Tree parse) {

        //TODO: Implementation
    }

    public void printFeatures() {
        for (String feature : featureList.keySet()) {
            System.out.println(feature + " : " + featureList.get(feature));
        }
    }

    public void reset() {
        for (String key : featureList.keySet()) {
            featureList.put(key, 0);
        }
    }


}