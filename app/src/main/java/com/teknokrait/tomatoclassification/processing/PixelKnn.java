package com.teknokrait.tomatoclassification.processing;

/**
 * Created by Aprilian Nur Wakhid Daini on 12/17/2017.
 *
 * Sample KNN
 * https://github.com/badlogic/knn/blob/master/src/Knn.java
 *
 */

import android.app.Activity;

import com.teknokrait.tomatoclassification.model.Tomato;
import com.teknokrait.tomatoclassification.realm.RealmController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

class PixelKnn {

    private Activity activity;
    private Realm realm;

    static class Tomato {
        String grade;
        int classifict;
        //int [] pixels;
        RGA rga;
    }

    class RGA{
        int r;
        int g;
        int a;

        void setValue(int r, int g, int a){
            this.r = r;
            this.g = g;
            this.a = a;
        }

        int [] getValue(){
            int[] temp = new int[2];
            temp[0] = r;
            temp[1] = g;
            temp[2] = a;
            return temp;
        }

    }

    private List<Tomato> readFile(boolean isTraining){
        List<Tomato> tomatoes = new ArrayList<Tomato>();

        if (isTraining){
            // refresh the realm instance
            RealmController.with(activity).refresh();
            List<com.teknokrait.tomatoclassification.model.Tomato> tomatosTraining = RealmController.with(activity).getTomatoes();

            for (com.teknokrait.tomatoclassification.model.Tomato t:tomatosTraining){
                Tomato tomato = new Tomato();
                tomato.grade = t.getStatus();
                tomato.classifict = t.getClassification();
                tomato.rga.setValue((int) t.getRed(), (int) t.getGreen(),(int) t.getBlue());
                tomatoes.add(tomato);
            }

        } else {

        }


        return tomatoes;
    }

    private static int distance(int[] a, int[] b) {
        int sum = 0;
        for(int i = 0; i < a.length; i++) {
            sum += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return (int)Math.sqrt(sum); // euclidian distance would be sqrt(sum)...
    }


    public PixelKnn(Activity activity){
        this.activity = activity;
        this.realm = RealmController.with(activity).getRealm();
    }

    private static int classify(List<Tomato> trainingSet, int[] pixels) {
        int label = 0, bestDistance = Integer.MAX_VALUE;
        for(Tomato sample: trainingSet) {
            //int dist = distance(sample.classifict, pixels);
            /*if(dist < bestDistance) {
                bestDistance = dist;
                label = sample.classifict;
            }*/
        }
        return label;
    }

    public void main(){
        List<Tomato> trainingSet = readFile(true);
        List<Tomato> validationSet = readFile(false);
        int numCorrect = 0;
        for(Tomato tomato:validationSet) {
            //if(classify(trainingSet, tomato.rga) == tomato.grade) numCorrect++;
        }
        System.out.println("Accuracy: " + (double)numCorrect / validationSet.size() * 100 + "%");
    }
}
