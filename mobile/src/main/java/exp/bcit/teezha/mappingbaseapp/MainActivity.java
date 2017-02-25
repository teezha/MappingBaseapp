package exp.bcit.teezha.mappingbaseapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements OnStatusChangedListener {

    MapView mapView;
    Button mBtnZoom;
    Spinner mSpnParcels;

    int mLotLayerID = 0;
    String mLotLayerURL;
    String mLotNumColName = "PARCELS_ID";


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        addBCLayer();
        getMapView().setOnStatusChangedListener(this);

        this.mBtnZoom = (Button) findViewById(R.id.btnZoom);
        this.mSpnParcels = (Spinner) findViewById(R.id.spnParcels);

        QueryParameters qryLotNums = new QueryParameters();
        qryLotNums.setReturnGeometry(false);
        qryLotNums.setOutFields(new String[]{mLotNumColName});
        qryLotNums.setWhere(mLotNumColName + " > 0");

        mLotLayerURL = getString(R.string.basemap_url) + "/" + mLotLayerID;
        QueryTask qtask = new QueryTask(mLotLayerURL);


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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
    public void zoomToFeature(View v) {

        /** =============================================================================
         * Your code goes here
         * ============================================================================= */

        quickToast("Inside ZoomToFeature");

        QueryParameters qryLotNums = new QueryParameters();
        qryLotNums.setReturnGeometry(true);
        qryLotNums.setOutFields(new String[]{mLotNumColName});
        qryLotNums.setWhere((String) mSpnParcels.getSelectedItem());

        mLotLayerURL = getString(R.string.basemap_url) + "/" + mLotLayerID;
        QueryTask qtask = new QueryTask(mLotLayerURL);

        try {
            /** ================================
             * add your code here
             * ==================================*/
            FeatureResult fSet = qtask.execute(qryLotNums);
            Feature tmpFeat = (Feature) fSet.iterator().next();

            Envelope epFeat = new Envelope((Point) tmpFeat.getGeometry());

            getMapView().setExtent(epFeat);








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


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onStatusChanged(Object o, STATUS status) {
        if (status == STATUS.LAYER_LOADED) {


        }


    }


}
