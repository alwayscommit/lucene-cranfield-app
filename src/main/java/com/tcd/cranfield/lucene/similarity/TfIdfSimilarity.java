package com.tcd.cranfield.lucene.similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class TfIdfSimilarity extends ClassicSimilarity {
    @Override
    public float idf(long docFrequency, long totalDocs){
        return (float) Math.log10((double)totalDocs/(double)docFrequency);
    }

    @Override
    public float tf(float frequency){
        return (float) (1 + Math.log10(frequency));
    }
}
