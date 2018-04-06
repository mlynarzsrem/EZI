package search;

import engine.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;
import net.sf.extjwnl.dictionary.MorphologicalProcessor;

public class WordNet implements ISearch
{
    private ArrayList <Document> _documents;
    private engine.Dictionary _dictionary;
    private Dictionary _wordnetDictionary;
    private static final String DICT_PATH = "wn3.1.dict/dict";

    public WordNet(engine.Dictionary dictionary, ArrayList <Document> documents)
    {
        this._dictionary = dictionary;
        this._documents = documents;

        try
        {
            _wordnetDictionary = Dictionary.getFileBackedInstance(DICT_PATH);
        } catch (JWNLException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public ArrayList <Score> getSortedDocuments(Document query)
    {
        // TODO 1) Build a set of unique indexes of terms of a query
        // You can iterate over dictionary._terms and use bow representation
        // of a query to verify if a term occurs in the query or not
        // if occurs, add the index of the term to uniqueTerms_Query
        Set <Integer> uniqueTerms_Query = new HashSet <>(_dictionary._terms.size());
        for(String term: _dictionary._terms){
            int id =_dictionary._termID.get(term);
            if(query._bow_representation[id]>0.0) {
                uniqueTerms_Query.add(id);
            }
        }

        System.out.println("Original terms and keywords: ");
        for (Integer i : uniqueTerms_Query)
            System.out.println("   term=" + _dictionary._terms.get(i) + " keyword=" +
                               _dictionary._termsToKeywords.get(_dictionary._terms.get(i)));
        System.out.println("");
        System.out.println("Suggesting other keywords: ");

        for (Integer i : uniqueTerms_Query)
        {
            String keyword = _dictionary._termsToKeywords.get(_dictionary._terms.get(i));
            System.out.println("   for the keyword = " + keyword);

            try
            {
            // TODO obtain senses of a term (keyword).
            // These senses are grouped: different groups have different meaning.
            // 1) use the IndexWord to store base form of a keyword. To derive base form, use
            // getMorphologicalProcessor(), lookupBaseForm() methods and _wordnetDictionary object.
            // For the latter method, use POS.NOUN or POS.VERB
            //-----------------------------------------------------------
            MorphologicalProcessor mp =  _wordnetDictionary.getMorphologicalProcessor();
            IndexWord baseForm = mp.lookupBaseForm(POS.NOUN,keyword);
            //-----------------------------------------------------------
            if (baseForm != null)
            {
                // 2) Iterate over groups of senses (getSenses()) - Synsets;
                // Print each synset .toString(). This info contains a meaning of a group and some examples.
                List<Synset> synList = baseForm.getSenses();
               /* for(Synset s : baseForm.getSenses()){
                    System.out.print(s.toString()+ " ");
                }*/
               for(int j=0;j<synList.size();j++){
                   try {
                       System.out.println(synList.get(j).toString());
                       for(Word word : synList.get(j).getWords()){
                           System.out.print(word.getLemma());
                       }
                   }
                   catch (Exception e){
                       e.printStackTrace();

                   }
               }
                System.out.println();

                // 3) Iterate over words of a synset (getWords()). Print a lemma of a word.
                for(int j=0;j<synList.size();j++){
                    try {
                        System.out.print(synList.get(j).toString());
                    }
                    catch (Exception e){

                    }
                }
            }

            } catch (JWNLException e)
            {
                e.printStackTrace();
            }
        }

        return getSortedDocuments(query._tf_idf_representation);
    }

    @Override
    public ArrayList <Score> getSortedDocuments(double[] queryVector)
    {
        ISearch search = new CosineSimilarity_TF_IDF(_documents);
        return search.getSortedDocuments(queryVector);
    }

    @Override
    public String getName()
    {
        return "Cosine similarity + word net";
    }

}
