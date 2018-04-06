package search;

import engine.Document;

import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.sqrt;

public class CosineSimilarity_TF_IDF implements ISearch
{
    private ArrayList <Document> _documents;

    public CosineSimilarity_TF_IDF(ArrayList <Document> documents)
    {
        this._documents = documents;
    }

    public void printTab(double tab[]){
        for(double d : tab){
            if(Double.isNaN(d))
            System.out.print(d+" ");
        }
        System.out.println();
    }
    public double getRootOFSumOfSquared(double tab[]){
        double sumVal=0.0d;
        for(double d : tab){
            if(Double.isNaN(d)==false)
                sumVal+=d*d;
        }
        return sqrt(sumVal);
    }
    public double getSum(double tab[]){
        double sumVal=0.0d;
        for(double d : tab){
            if(Double.isNaN(d)==false)
                sumVal+=d;
        }
        return sumVal;
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
        for(int i=0;i<_documents.size();i++){
            double mul_TFIDF_query[] = new double[_documents.get(i)._tf_idf_representation.length];
            for(int j=0;j<mul_TFIDF_query.length;j++){
                mul_TFIDF_query[j] = _documents.get(i)._tf_idf_representation[j]*queryVector[j];
            }
            double score = getSum(mul_TFIDF_query) /(getRootOFSumOfSquared(queryVector)*getRootOFSumOfSquared(_documents.get(i)._tf_idf_representation));
            if(Double.isNaN(score))
                score=0.0d;
            Score newScore = new Score(_documents.get(i),score);
            scores.add(newScore);
        }
        Collections.sort(scores);
        return scores;
    }

    @Override
    public String getName()
    {
        return "Cosine similarity";
    }
}
