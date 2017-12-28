package com.teknokrait.tomatoclassification.processing;

/**
 * Created by Aprilian Nur Wakhid Daini on 12/26/2017.
 */

public class Neighbor implements Comparable<Neighbor> {
    boolean positivo = false;
    double distancia;
    public PixelKnn p;

    public Neighbor( double d, boolean p ) {
        positivo = p;
        distancia = d;
    }

    public int compareTo(Neighbor o) { // retorna Neg se este eh MENOR... 0 se IGUAL... Posit. se MAIOR
        double result = this.distancia - o.distancia;
        if( result < 0 )
            return -1;
        else if( result > 0 )
            return 1;
        else
            return 0;
    }

}
