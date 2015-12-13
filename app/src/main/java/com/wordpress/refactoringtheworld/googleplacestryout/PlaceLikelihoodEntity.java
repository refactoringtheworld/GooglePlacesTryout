package com.wordpress.refactoringtheworld.googleplacestryout;

import com.google.android.gms.location.places.PlaceLikelihood;

/**
 * Created by Alb_Erc on 12/12/2015.
 */
public class PlaceLikelihoodEntity {
    private String place;
    private double likelihood;

    public PlaceLikelihoodEntity(String place, double likelihood) {
        this.place = place;
        this.likelihood = likelihood;
    }

    public String getPlace() {
        return place;
    }

    public double getLikelihood() {
        return likelihood;
    }

    @Override
    public String toString() {
        return "Place: " + this.place + " Likelihood: " + this.likelihood;
    }
}
