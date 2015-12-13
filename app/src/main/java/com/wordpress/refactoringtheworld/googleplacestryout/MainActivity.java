package com.wordpress.refactoringtheworld.googleplacestryout;

import android.content.DialogInterface;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;

    private Button getPlacesButton, sendAnalyticsButton;
    private ListView listView;
    private AlertDialog dialog;

    private List<PlaceLikelihoodEntity> placeLilelihoodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LOG", "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApiIfAvailable(Places.GEO_DATA_API)
                .addApiIfAvailable(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API).build();

        getPlacesButton = (Button) this.findViewById(R.id.button_get_places);
        sendAnalyticsButton = (Button) this.findViewById(R.id.button_send_results);
        listView = (ListView) this.findViewById(R.id.listView);

        getPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LOG", "getPlacesButtonOnClickListener");
                getCurrentPlace();
            }
        });

        sendAnalyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LOG", "sendAnalyticsButtonOnClickListener");

                if (placeLilelihoodList != null && !placeLilelihoodList.isEmpty()) {
                    showResultDialog();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        builder.setPositiveButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d("LOG", "showResultDialogGoodResult");

                SortedSet<LocationAnalyticsEntity> analytics = getLocationAnalytics();
                analytics.last().setWasGood(true);
                for (LocationAnalyticsEntity analytic : analytics) {
                    new UploadLocationAnalyticsTask(getApplicationContext()).execute(analytic);
                }
            }
        });

        builder.setNegativeButton(R.string.dialog_button_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d("LOG", "showResultDialogBadResult");

                SortedSet<LocationAnalyticsEntity> analytics = getLocationAnalytics();
                for (LocationAnalyticsEntity analytic : analytics) {
                    new UploadLocationAnalyticsTask(getApplicationContext()).execute(analytic);
                }
            }
        });

        this.dialog = builder.create();
    }

    @Override
    protected void onStart() {
        Log.d("LOG", "onStart");
        super.onStart();

        if (this.googleApiClient != null) {
            this.googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        Log.d("LOG", "onStop");
        if (this.googleApiClient != null && this.googleApiClient.isConnected()) {
            this.googleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("LOG", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("LOG", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("LOG", "onConnectionFailed");
    }

    private void getCurrentPlace() {
        Log.d("LOG", "getCurrentPlace");
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(this.googleApiClient, null);

        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                Log.d("LOG", "getCurrentPlaceOnResultCallback");
                placeLilelihoodList = new ArrayList();

                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    if (placeLikelihood != null
                            && placeLikelihood.getPlace() != null
                            && !TextUtils.isEmpty(placeLikelihood.getPlace().getName())) {
                        Log.d("LOG", placeLikelihood.getPlace().getName().toString());
                        placeLilelihoodList.add(new PlaceLikelihoodEntity(placeLikelihood.getPlace().getName().toString(), placeLikelihood.getLikelihood() * 100));
                    }
                }

                likelyPlaces.release();

                showRetrievedPlaces();
            }
        });
    }

    private void showRetrievedPlaces() {
        Log.d("LOG", "showRetrievedPlaces");
        if (placeLilelihoodList != null && !placeLilelihoodList.isEmpty()) {
            ArrayAdapter adapter = new ArrayAdapter(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    placeLilelihoodList.toArray(new PlaceLikelihoodEntity[0]));

            listView.setAdapter(adapter);
        }
        else
        {
            Toast toast = Toast.makeText(getApplication(), "The list is now empty", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private SortedSet<LocationAnalyticsEntity> getLocationAnalytics() {
        Log.d("LOG", "getLocationAnalytics");
        SortedSet<LocationAnalyticsEntity> locationAnalytics = new TreeSet<>(new LocationAnalyticsEntityComparator());

        for (PlaceLikelihoodEntity place : placeLilelihoodList) {
            String user = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            locationAnalytics.add(LocationAnalyticsEntity.FromPlaceLikelihood(user, place));
        }

        return locationAnalytics;
    }

    private void showResultDialog() {
        Log.d("LOG", "showResultDialog");

        dialog.show();
    }
}
