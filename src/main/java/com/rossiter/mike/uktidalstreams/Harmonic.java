package com.rossiter.mike.uktidalstreams;

/**
 * Created by lutusp on 9/15/17.
 */

final public class Harmonic {
    double amplitude = 0;
    double epoch = 0;
    Harmonic()
    {
    }
    Harmonic(double a, double e)
    {
        amplitude = a;
        epoch = e;
    }
    Harmonic(Harmonic x)
    {
        amplitude = x.amplitude;
        epoch = x.epoch;
    }
    public String toString()
    {
        return "{" + amplitude + "," + epoch + "}";
    }
}
