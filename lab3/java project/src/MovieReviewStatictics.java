
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MovieReviewStatictics
{
    public static String TOKENIZER_MODEL = "models/en-token.bin";
    public static String SENTENCE_MODEL = "models/en-sent.bin";
    public static String POS_MODEL = "models/en-pos-maxent.bin";
    public static String LEMMATIZER_DICT = "models/en-lemmatizer.dict";
    public static String NAME_MODEL = "models/en-ner-person.bin";
    public static String ORGANIZATION_MODEL = "models/en-ner-organization.bin";
    public static String LOCATION_MODEL = "models/en-ner-location.bin";

    private static final String DOCUMENTS_PATH = "movies/";
    private int _verbCount = 0;
    private int _nounCount = 0;
    private int _adjectiveCount = 0;
    private int _adverbCount = 0;
    private int _totalTokensCount = 0;

    private PrintStream _statisticsWriter;

    private SentenceModel _sentenceModel; //Done
    private TokenizerModel _tokenizerModel;//Done
    private DictionaryLemmatizer _lemmatizer;//Done
    private PorterStemmer _stemmer;//Done
    private POSModel _posModel;
    private TokenNameFinderModel _peopleModel;
    private TokenNameFinderModel _placesModel;
    private TokenNameFinderModel _organizationsModel;

    public static void main(String[] args)
    {
        MovieReviewStatictics statictics = new MovieReviewStatictics();
        statictics.run();
    }

    private void run()
    {
        try
        {
            initModelsStemmerLemmatizer();

            File dir = new File(DOCUMENTS_PATH);
            File[] reviews = dir.listFiles((d, name) -> name.endsWith(".txt"));

            _statisticsWriter = new PrintStream("statistics.txt", "UTF-8");

            Arrays.sort(reviews, Comparator.comparing(File::getName));
            for (File file : reviews)
            {
                System.out.println("Movie: " + file.getName().replace(".txt", ""));
                _statisticsWriter.println("Movie: " + file.getName().replace(".txt", ""));

                String text = new String(Files.readAllBytes(file.toPath()));
                processFile(text);

                _statisticsWriter.println();
            }

            overallStatistics();
            _statisticsWriter.close();

        } catch (IOException ex)
        {
            Logger.getLogger(MovieReviewStatictics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initModelsStemmerLemmatizer()
    {
        try
        {
            //Stemmer and lematizer
            File file = new File(LEMMATIZER_DICT);
            InputStream targetStream = new FileInputStream(file);
            this._lemmatizer = new DictionaryLemmatizer(targetStream);
            this._stemmer =  new PorterStemmer();
            //Sentece model
            file = new File(SENTENCE_MODEL);
            targetStream = new FileInputStream(file);
            this._sentenceModel =  new SentenceModel(targetStream);
            //tokenization
            file = new File(TOKENIZER_MODEL);
            targetStream = new FileInputStream(file);
            this._tokenizerModel = new TokenizerModel(targetStream);
            //Pos model
            file = new File(POS_MODEL);
            targetStream = new FileInputStream(file);
            this._posModel = new POSModel(targetStream);
            //Token name finder
            file = new File(NAME_MODEL);
            targetStream = new FileInputStream(file);
            this._peopleModel =  new TokenNameFinderModel(targetStream);
            //organization
            file = new File(ORGANIZATION_MODEL);
            targetStream = new FileInputStream(file);
            this._organizationsModel=  new TokenNameFinderModel(targetStream);
            //LOCATION
            file = new File(LOCATION_MODEL);
            targetStream = new FileInputStream(file);
            this._placesModel=  new TokenNameFinderModel(targetStream);


        } catch (IOException ex)
        {
            Logger.getLogger(MovieReviewStatictics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFile(String text)
    {
        // TODO: process the text to find the following statistics:
        // For each movie derive:
        //    - number of sentences
        int noSentences =0;
        //    - number of tokens
        int noTokens = 0;
        //    - number of (unique) stemmed forms
        int noStemmed = 0;
        //    - number of (unique) words from a dictionary (lemmatization)
        int noWords = 0;
        //    -  people
        Span people[] = new Span[] { };
        //    - locations
        Span locations[] = new Span[] { };
        //    - organisations
        Span organisations[] = new Span[] { };

        // TODO + compute the following overall (for all movies) POS tagging statistics:
        //    - percentage number of adverbs (class variable, private int _verbCount = 0)
        //    - percentage number of adjectives (class variable, private int _nounCount = 0)
        //    - percentage number of verbs (class variable, private int _adjectiveCount = 0)
        //    - percentage number of nouns (class variable, private int _adverbCount = 0)
        //    + update _totalTokensCount

        // ------------------------------------------------------------------

        // TODO derive sentences (update noSentences variable)
        SentenceDetectorME sentenceME =  new SentenceDetectorME(this._sentenceModel);
        noSentences =sentenceME.sentDetect(text).length;
        // TODO derive tokens and POS tags from text
        TokenizerME tokenizer = new TokenizerME(this._tokenizerModel);
        String tokens[] = tokenizer.tokenize(text);
        noTokens =tokens.length;
        POSTaggerME taggerME = new POSTaggerME(this._posModel);
        String posTags[] =taggerME.tag(tokens);
        for(String tag : posTags){
            if(tag.startsWith("V"))
                this._verbCount ++;
            if(tag.startsWith("N"))
                this._nounCount ++;
            if(tag.startsWith("JJ"))
                this._adjectiveCount ++;
            if(tag.startsWith("RB"))
                this._adverbCount ++;
        }
        this._totalTokensCount+=posTags.length;
        // TODO perform stemming (use derived tokens)
        // (update noStemmed)
        Set <String> stems = new HashSet <>();
        List <String> derivedTokensList  = new LinkedList<String>();;
        for (String token : tokens)
        {
             String editedToken =  token.toLowerCase().replaceAll("[^a-z0-9]", "");
             if(editedToken.equals("")){
                 derivedTokensList.add(editedToken);
                 stems.add(this._stemmer.stem(editedToken));
             }
        }
        noStemmed =stems.size();
        // TODO perform lemmatization (use derived tokens)
        String [] derivedTokens =  new String[derivedTokensList.size()];
        derivedTokens = derivedTokensList.toArray(derivedTokens);
        String [] lems =this._lemmatizer.lemmatize(derivedTokens,posTags);
        noWords = lems.length;
        // TODO derive people, locations, organisations (use tokens),
        people =  new NameFinderME(this._peopleModel).find(tokens);
        locations =  new NameFinderME(this._placesModel).find(tokens);
        organisations =  new NameFinderME(this._organizationsModel).find(tokens);
        // TODO update overall statistics - use tags and check first letters
        // (see https://www.clips.uantwerpen.be/pages/mbsp-tags; first letter = "V" = verb?)


        // ------------------------------------------------------------------

        saveResults("Sentences", noSentences);
        saveResults("Tokens", noTokens);
        saveResults("Stemmed forms (unique)", noStemmed);
        saveResults("Words from a dictionary (unique)", noWords);

        try {
            saveNamedEntities("People", people, tokens);
            saveNamedEntities("Locations", locations, tokens);
            saveNamedEntities("Organizations", organisations, tokens);
        }
        catch (Exception ex){
        }
    }


    private void saveResults(String feature, int count)
    {
        String s = feature + ": " + count;
        System.out.println("   " + s);
        _statisticsWriter.println(s);
    }

    private void saveNamedEntities(String entityType, Span spans[], String tokens[])
    {
        StringBuilder s = new StringBuilder(entityType + ": ");
        for (int sp = 0; sp < spans.length; sp++)
        {
            for (int i = spans[sp].getStart(); i < spans[sp].getEnd(); i++)
            {
                s.append(tokens[i]);
                if (i < spans[sp].getEnd() - 1) s.append(" ");
            }
            if (sp < spans.length - 1) s.append(", ");
        }

        System.out.println("   " + s);
        _statisticsWriter.println(s);
    }

    private void overallStatistics()
    {
        _statisticsWriter.println("---------OVERALL STATISTICS----------");
        DecimalFormat f = new DecimalFormat("#0.00");

        if (_totalTokensCount == 0) _totalTokensCount = 1;
        String verbs = f.format(((double) _verbCount * 100) / _totalTokensCount);
        String nouns = f.format(((double) _nounCount * 100) / _totalTokensCount);
        String adjectives = f.format(((double) _adjectiveCount * 100) / _totalTokensCount);
        String adverbs = f.format(((double) _adverbCount * 100) / _totalTokensCount);

        _statisticsWriter.println("Verbs: " + verbs + "%");
        _statisticsWriter.println("Nouns: " + nouns + "%");
        _statisticsWriter.println("Adjectives: " + adjectives + "%");
        _statisticsWriter.println("Adverbs: " + adverbs + "%");

        System.out.println("---------OVERALL STATISTICS----------");
        System.out.println("Adverbs: " + adverbs + "%");
        System.out.println("Adjectives: " + adjectives + "%");
        System.out.println("Verbs: " + verbs + "%");
        System.out.println("Nouns: " + nouns + "%");
    }

}
