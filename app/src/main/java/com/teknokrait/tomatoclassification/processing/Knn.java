package com.teknokrait.tomatoclassification.processing;

/**
 * Created by Aprilian Nur Wakhid Daini on 12/17/2017.
 *
 * Sample KNN
 * https://github.com/badlogic/knn/blob/master/src/Knn.java
 *
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Knn {
    static class Tomato {
        String grade;
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

    private static List<Tomato> readFile(String file) throws IOException {
        List<Tomato> tomatoes = new ArrayList<Tomato>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            String line = reader.readLine(); // ignore header
            while((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                Tomato tomato = new Tomato();
                tomato.grade = tokens[0];
                /*tomato.rga[0] = Integer.parseInt(tokens[1]);
                tomato.rga[1] = Integer.parseInt(tokens[2]);
                tomato.rga[2] = Integer.parseInt(tokens[3]);*/
                tomato.rga.setValue(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
                tomatoes.add(tomato);

                //sample.label = Integer.parseInt(tokens[0]);
                //sample.pixels = new int[tokens.length - 1];
                /*for(int i = 1; i < tokens.length; i++) {
                    sample.pixels[i-1] = Integer.parseInt(tokens[i]);
                }*/

            }
        } finally { reader.close(); }
        return tomatoes;
    }

    private static int distance(int[] a, int[] b) {
        int sum = 0;
        for(int i = 0; i < a.length; i++) {
            sum += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return (int)Math.sqrt(sum); // euclidian distance would be sqrt(sum)...
    }

    /*private static int classify(List<Tomato> trainingSet, int[] pixels) {
        int label = 0, bestDistance = Integer.MAX_VALUE;
        for(Tomato sample: trainingSet) {
            int dist = distance(sample.grade, pixels);
            if(dist < bestDistance) {
                bestDistance = dist;
                label = sample.grade;
            }
        }
        return label;
    }*/

    /*public static void main(String[] argv) throws IOException {
        List<Tomato> trainingSet = readFile("trainingsample.csv");
        List<Tomato> validationSet = readFile("validationsample.csv");
        int numCorrect = 0;
        for(Tomato tomato:validationSet) {
            if(classify(trainingSet, tomato.rga) == tomato.grade) numCorrect++;
        }
        System.out.println("Accuracy: " + (double)numCorrect / validationSet.size() * 100 + "%");
    }*/
}
