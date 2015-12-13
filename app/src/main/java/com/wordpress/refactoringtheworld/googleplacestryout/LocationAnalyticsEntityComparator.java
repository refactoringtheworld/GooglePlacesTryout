package com.wordpress.refactoringtheworld.googleplacestryout;

import java.util.Comparator;

/**
 * Created by Alb_Erc on 12/12/2015.
 */
public class LocationAnalyticsEntityComparator implements Comparator<LocationAnalyticsEntity> {
    @Override
    public int compare(LocationAnalyticsEntity lhs, LocationAnalyticsEntity rhs) {
        int compare = Double.compare(lhs.getLikelihood(), rhs.getLikelihood());

        if (compare == 0) {
            compare = lhs.getRowKey().compareTo(rhs.getRowKey());
        }

        return compare;
    }
}
