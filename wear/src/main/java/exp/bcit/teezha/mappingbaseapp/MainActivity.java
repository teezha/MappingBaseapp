package exp.bcit.teezha.mappingbaseapp;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity implements MainActivityWear {



    MapView mapView;
    Spinner mSpnParcels;

    int mLotLayerID = 0;
    String mLotLayerURL;
    String mLotNumColName = "PARCELS_ID";


    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        addBCLayer();
        getMapView().setOnStatusChangedListener((OnStatusChangedListener) this);

        this.mSpnParcels = (Spinner) findViewById(R.id.spnParcels);

        QueryParameters qryLotNums = new QueryParameters();
        qryLotNums.setReturnGeometry(false);
        qryLotNums.setOutFields(new String[]{mLotNumColName});
        qryLotNums.setWhere(mLotNumColName + " > 0");

        mLotLayerURL = getString(R.string.basemap_url) + "/" + mLotLayerID;
        QueryTask qtask = new QueryTask(mLotLayerURL);

        mSpnParcels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    zoomToFeature(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        try {
            /** ================================
             * add your code here
             * ==================================*/
            FeatureResult fSet = qtask.execute(qryLotNums);

            ArrayList<Integer> listOfLotNums = new ArrayList<Integer>();
            Feature tmpFeat;

            for (Object featAsObj : fSet) {
                tmpFeat = (Feature) featAsObj;
                listOfLotNums.add(
                        Integer.valueOf(tmpFeat.getAttributeValue(
                                mLotNumColName).toString()));
            }
            Collections.sort(listOfLotNums);

            ArrayAdapter<Integer> adtTmp = new ArrayAdapter<Integer>(
                    this, android.R.layout.simple_spinner_dropdown_item,
                    listOfLotNums);

            mSpnParcels.setAdapter(adtTmp);


        } catch (Exception e) {
            Log.d("GIST-8010", e.getMessage());
        }
//==========================================
// import com.esri.core.tasks.query
//==========================================

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }
    }


    /**
     * =============================================================================
     * getter for the main map
     * =============================================================================
     */
    private MapView getMapView() {

        /** =============================================================================
         * if mMapView is underlined in red, change the name to match your MapView
         * =============================================================================*/
        if (mapView == null) {
            mapView = (MapView) findViewById(R.id.map);
        }
        return mapView;
    }

    /**
     * =============================================================================
     * setter for the main map
     * =============================================================================
     */
    private void setMapView(MapView newMapView) {
        /** =============================================================================
         * if mMapView is underlined in red, change the name to match your MapView
         * =============================================================================*/
        mapView = newMapView;
    }


    public void addBCLayer() {
        /** ==============================================================
         * Make a tiled service based on the BC cache
         * then add the layer to the map
         * import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
         * create a string called basemap_url (res/values/strings.xml)
         * http:*maps.gov.bc.ca/arcserver/rest/services/Province/albers_cache/MapServer
         * ==============================================================*/
        ArcGISTiledMapServiceLayer basemap = new ArcGISTiledMapServiceLayer(
                this.getResources().getString(R.string.basemap_url));
        getMapView().addLayer(basemap);
    }


    /**
     * =============================================================================
     * Zooms the map the the MBR of the feature selected in the spinner
     * import android.view.View;
     * =============================================================================
     */
    public void zoomToFeature(View v) throws Exception {

        /** =============================================================================
         * Your code goes here
         * ============================================================================= */

        //quickToast("Inside ZoomToFeature");

        QueryParameters zoomParams = new QueryParameters();
        zoomParams.setReturnGeometry(true);
        zoomParams.setOutFields(new String[]{mLotNumColName});
        zoomParams.setWhere(mLotNumColName+" = "+mSpnParcels.getSelectedItem().toString());
        quickToast(mLotNumColName+"="+mSpnParcels.getSelectedItem().toString());

        QueryTask qtask = new QueryTask(mLotLayerURL);

        try {
            /** ================================
             * add your code here
             * ==================================*/
            FeatureResult fSet = qtask.execute(zoomParams);
            Feature tmpFeat = (Feature) fSet.iterator().next();
            getMapView().setExtent(tmpFeat.getGeometry());
        } catch (Exception e) {
            Log.d("GIST-8010", e.getMessage());
        }



    }


    /**
     * =============================================================================
     * This method simplifies making a toast
     * =============================================================================
     */
    public void quickToast(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }


    @Override
    public void onStatusChanged(Object o, OnStatusChangedListener.STATUS status) {
        if (status == OnStatusChangedListener.STATUS.LAYER_LOADED) {


        }


    }


}
