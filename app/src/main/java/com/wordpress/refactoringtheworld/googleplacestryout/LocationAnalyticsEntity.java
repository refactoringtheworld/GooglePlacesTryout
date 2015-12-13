package com.wordpress.refactoringtheworld.googleplacestryout;

import com.google.android.gms.location.places.PlaceLikelihood;
import com.microsoft.azure.storage.table.TableServiceEntity;

import java.util.Date;

/**
 * Created by Alb_Erc on 10/12/2015.
 */
public class LocationAnalyticsEntity extends TableServiceEntity {
    public LocationAnalyticsEntity(String partitionKey, String rowKey) {
        this.partitionKey = partitionKey;
        this.rowKey = rowKey;
    }

    public boolean WasGood;
    public double Likelihood;
    public String Place;

    public boolean getWasGood() {
        return WasGood;
    }

    public void setWasGood(boolean wasGood) {
        WasGood = wasGood;
    }

    public double getLikelihood() {
        return Likelihood;
    }

    public void setLikelihood(double likelihood) {
        Likelihood = likelihood;
    }

    public String getPlace() {
        return Place;
    }

    public void setPlace(String place) {
        Place = place;
    }

    public static LocationAnalyticsEntity FromPlaceLikelihood(String user, PlaceLikelihoodEntity placeLikelihood) {
        LocationAnalyticsEntity entity = new LocationAnalyticsEntity(new Date().toString(), user + "-" + placeLikelihood.getPlace());

        entity.setLikelihood(placeLikelihood.getLikelihood());
        entity.setWasGood(false);
        entity.setPlace(placeLikelihood.getPlace());

        return entity;
    }
}
