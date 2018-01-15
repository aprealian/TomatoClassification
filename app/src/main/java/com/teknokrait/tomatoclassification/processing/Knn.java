package com.teknokrait.tomatoclassification.processing;

/**
 * Created by Aprilian Nur Wakhid Daini on 12/17/2017.
 *
 * Sample KNN
 * https://github.com/badlogic/knn/blob/master/src/Knn.java
 *
 */

import android.app.Activity;

import com.teknokrait.tomatoclassification.adapters.RealmTomatoesAdapter;
import com.teknokrait.tomatoclassification.model.Tomato;
import com.teknokrait.tomatoclassification.realm.RealmController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Knn {

    //private static int distance(int[] a, int[] b) {
    private static int distance(Tomato train, Tomato test) {
        int sum = 0;

        sum += (train.getRed() - test.getRed()) * (train.getRed() - test.getRed());
        sum += (train.getGreen() - test.getGreen()) * (train.getGreen() - test.getGreen());
        sum += (train.getBlue() - test.getBlue()) * (train.getBlue() - test.getBlue());

        return (int)Math.sqrt(sum); // euclidian distance would be sqrt(sum)...
    }

    private static int classify(List<Tomato> trainingSet, Tomato tomato) {
        int label = 0, bestDistance = Integer.MAX_VALUE;
        for(Tomato sample: trainingSet) {
            int dist = distance(sample, tomato);
            if(dist < bestDistance) {
                bestDistance = dist;
                label = sample.getClassification();
            }
        }
        return label;
    }


    public int getClassification(List<Tomato> trainingSet, Tomato tomato ){
        return classify(trainingSet, tomato);
    }

    public static void main(String[] argv) throws IOException {
        List<Tomato> trainingSet = new ArrayList<>();
        List<Tomato> validationSet = new ArrayList<>();
        int numCorrect = 0;
        for(Tomato tomato:validationSet) {
            if(classify(trainingSet, tomato) == tomato.getClassification()) numCorrect++;
        }
        System.out.println("Accuracy: " + (double)numCorrect / validationSet.size() * 100 + "%");
    }

}
