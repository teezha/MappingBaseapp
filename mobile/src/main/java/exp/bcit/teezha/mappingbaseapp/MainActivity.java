package exp.bcit.teezha.mappingbaseapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements OnStatusChangedListener {

    MapView mapView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addBCLayer();
        getMapView().setOnStatusChangedListener(this);

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
        if(status == STATUS.LAYER_LOADED) {
            zoom();

        }


    }


    public void zoom() {
        /** =============================================================================
         * zoom the map to a known location: crescent beach
         * =============================================================================*/
        double lat, lon;
        lat = 49.060369722746124;
        lon = -122.87796020507814;
        boolean animated = true;
        double scale = 40000;
        /** =============================================================================
         * 2 step zoom: center then scale
         * =============================================================================*/
        getMapView().centerAt(lat, lon, animated);
        getMapView().setScale(scale);
    }
}
