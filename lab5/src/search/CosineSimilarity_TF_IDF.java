package search;

import engine.Document;

import java.util.ArrayList;
import java.util.Collections;

public class CosineSimilarity_TF_IDF implements ISearch
{
    private ArrayList <Document> _documents;

    public CosineSimilarity_TF_IDF(ArrayList <Document> documents)
    {
        this._documents = documents;
    }

    @Override
    public ArrayList <Score> getSortedDocuments(Document query)
    {
        return getSortedDocuments(query._tf_idf_representation);
    }

    @Override
    public ArrayList <Score> getSortedDocuments(double[] queryVector)
    {
        ArrayList <Score> scores = new ArrayList <>(_documents.size());
        // TODO update scores: compute a similarity of each document (TF-IDF) to the query vector
        // -----------------------------------------------

        // -----------------------------------------------

        Collections.sort(scores);
        return scores;
    }

    @Override
    public String getName()
    {
        return "Cosine similarity";
    }
}
