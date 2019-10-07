package com.rossiter.mike.uktidalstreams;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import org.mapsforge.map.android.rendertheme.AssetsRenderTheme;
import org.mapsforge.map.rendertheme.XmlRenderTheme;
import org.osmdroid.api.IMapController;

import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.mapsforge.MapsForgeTileProvider;
import org.osmdroid.mapsforge.MapsForgeTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.views.MapView;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.xmlpull.v1.XmlPullParserException;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;


import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.Route;
import io.ticofab.androidgpxparser.parser.domain.RoutePoint;



public class MapActivity extends AppCompatActivity {

    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;

    MapsForgeTileSource fromFiles = null;
    MapsForgeTileProvider forge = null;

    // Progress Dialog
    private ProgressDialog pDialog;

    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    // File url to download
    private static String file_url = "https://s3.eu-west-2.amazonaws.com/ukmaps/uk.map";

    int width;
    int height;
    GeoPoint minDistArrow;

    String showMe = "yes";

    GPXParser mParser = new GPXParser();
    Gpx parsedGpx = null;
    Polyline myRouteLine;

    SharedPreferences sharedPreferences;
    boolean isMyNumber;
    String myLocLat;
    String myLocLon;

    GeoPoint lastKnownLocation;
    Marker currentLocationMarker;
    Marker currentArrowMarker;
    Marker centreMarker;


    IMapController mapController;

    MapActivity currentActivity = null;
    MapActivity app;

    MapView mapView;

    TextView textTime;
    TextView textPlusMinus;
    TextView textDirSpd;
    Spinner spinHW;
    Spinner spinPort;

    Date spinnerHWDate;

    Button nowButton;
    Button hwButton;

    Button prevButton;
    Button nextButton;

    String portSelected;
    String portNumber;

    String sitePage = "";
    TideComp tideComp = null;
    SunComp sunComp = null;
    Utilities utilities = null;
    PreviousNext pn = null;
    CalendarUtils calendarUtils = null;

    int myFromHW;
    Date myChangingDate;
    Calendar myCalendar;

    /* for previous-next logic */
    int m_myChoice;
    int button_id, next_id;
    int back_id;
    String fb;

    ConfigValues configValues;
    ArrayList<String> harmonicArray = null; // the full,raw database
    ArrayList<String> decodedHarmonics = null;
    LinkedHashMap<Integer, String> indexArray = null; // a map of integers to full descriptive strings
    LinkedHashMap<Integer, String> titleArray = null; // a map of integers to title-only strings
    LinkedHashMap<String, Integer> reverseArray = null; // a map of title-only strings to integers

    List<Polygon> polygons = new ArrayList<Polygon>();
    ArrayList<OverlayItem> locations = new ArrayList<OverlayItem>();
    List<Marker> markers = new ArrayList<Marker>();
    Marker coorLoad;
    Marker locationMarker;
    List<Marker> markerListArrows = new ArrayList<>();
    List<String> arrayHW = new ArrayList<>();

    public List<Diamond> myDiamonds;
    public List<Port> myPorts;

    Point pointXY;
    boolean showingChart = false;

    boolean useGPS = false;
    boolean useLocation;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.filechooser: {
                onButtonChooseFile();
                return true;
            }
            case R.id.cleargpx: {
                onClearGPX();
                return true;
            }

            case R.id.about:
                Intent intent3 = new Intent();
                intent3.setClassName(this, "com.rossiter.mike.uktidalstreams.ShowAbout");
                startActivity(intent3);
                return true;

            case R.id.downloadmaps: {

                new DownloadFileFromURL().execute(file_url);
                return true;
            }

            case R.id.settings: {

                Intent intent = new Intent();
                intent.setClassName(this, "com.rossiter.mike.uktidalstreams.SettingsActivity");
                startActivity(intent);
                return true;
            }
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public GeoPoint[] myPortLocations = new GeoPoint[]{
            new GeoPoint(49.830, -3.728), // Devonport
            new GeoPoint(50.801, -1.112),// Portsmouth
            new GeoPoint(50.516, -1.308),// Dover
            new GeoPoint(51.503, 1.104),// Sheerness
            new GeoPoint(52.309, 1.665), // Lowestoft
            new GeoPoint(53.382, 1.067), // Immingham
            new GeoPoint(56.200, -2.243), // Leith
            new GeoPoint(58.225, -2.259), // Aberdeen
            new GeoPoint(58.697, -3.025), // Wick
            new GeoPoint(60.338, -0.971), // Lerwick
            new GeoPoint(57.507, -5.998), // Ullapool
            new GeoPoint(53.528, -3.638), // Liverpool
            new GeoPoint(53.171, -4.634), // Holyhead
            new GeoPoint(51.686, -5.263), // Milford Haven
            new GeoPoint(51.304, -3.592), // Avonmouth
            new GeoPoint(49.367, -2.366),// St.Helier
    }; // end of mainactivity variables

    public Point[] myPoints = new Point[]{
            new Point(0, 0),
            new Point(0, -10),
            new Point(55, -10),
            new Point(55, -25),
            new Point(100, 0),
            new Point(55, 25),
            new Point(55, 10),
            new Point(0, 10),
            new Point(0, 0),
            // end of mainactivity variables
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        MapsForgeTileSource.createInstance(this.getApplication());

        minDistArrow = new GeoPoint(0.0, 0.0);

        try {
            makeDiamonds();
            makePorts();

        } catch (Exception e) {

        }

        savedInstanceState = getIntent().getExtras();
        if (savedInstanceState != null) {
            showMe = savedInstanceState.getString("key");
            //The key argument here must match that used in the other activity
        }

        if (showMe.equals("no")) {
            //do nothing
        } else {
            new showSplash().execute();
        }

        new CopyData().execute();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.tidal_arrows);
        actionBar.setTitle(" UK Tidal Streams (Demo)");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }


        }
            // Permission has already been granted

            myRouteLine = new Polyline();

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            useLocation = sharedPreferences.getBoolean("ShowMyLocation", false);

            spinHW = (Spinner) findViewById(R.id.spinHW);
            spinPort = (Spinner) findViewById(R.id.spinPort);

            nowButton = (Button) findViewById(R.id.myArrowButton);
            hwButton = (Button) findViewById(R.id.myHWButton);

            nextButton = (Button) findViewById(R.id.buttonNext);
            prevButton = (Button) findViewById(R.id.buttonPrev);

            configValues = new ConfigValues();
            sunComp = new SunComp(this);
            tideComp = new TideComp(this);
            utilities = new Utilities();
            pn = new PreviousNext(this);
            calendarUtils = new CalendarUtils();

            app = (this);
            app.currentActivity = this;

            //inflate and create the map
            setContentView(R.layout.activity_main);
            mapView = (MapView) findViewById(R.id.mapView);

            String myMapName = Environment.getExternalStorageDirectory().toString() + "/Download/uk.map";
            File myMapFile = new File(myMapName);
            if (myMapFile.exists()) {
                showMapsforgeFile(myMapFile);
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_message)
                        .setTitle(R.string.dialog_title);

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });


                AlertDialog dialog = builder.create();
                dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new DownloadFileFromURL().execute(file_url);
                            }
                        });
                dialog.show();
            }

            mapView.setBuiltInZoomControls(true);
            mapView.setMultiTouchControls(true);
            mapController = mapView.getController();
            mapController.setZoom(8.);

            mapView.setUseDataConnection(false);
            mapView.setMaxZoomLevel(14.0);
            mapView.setMinZoomLevel(8.0);

            double dd = 50.0;

            GeoPoint startLocation = new GeoPoint(50., -4.);
            currentArrowMarker = new Marker(mapView);
            currentArrowMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
            currentArrowMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            currentArrowMarker.setPanToView(true);

            currentArrowMarker.setPosition(startLocation);

            myCalendar = Calendar.getInstance();
            myChangingDate = myCalendar.getTime();

            portSelected = "Devonport";
            portNumber = "0014";

            updateHW("Devonport");
            updatePort();

            myFromHW = 0;
            button_id = 6; //High Water
            next_id = 7;
            back_id = 5;
            m_myChoice = 0;
            fb = "";

            drawNow();

            Button myButton = (Button) findViewById(R.id.buttonCalendar);
            myButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(app, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }

            });

            spinPort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String s = spinPort.getSelectedItem().toString();
                    onSelectPort(s);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });


            spinHW.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    m_myChoice = spinHW.getSelectedItemPosition();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            mapView.setMapListener(new DelayedMapListener(new MapListener() {
                @Override
                public boolean onZoom(final ZoomEvent e) {

                    Log.i("zoom", e.toString());

                    drawArrows(portNumber, myFromHW);
                    minDistArrow = getArrowDistances(width, height);
                    getInfoNearestArrow(minDistArrow.getLatitude(), minDistArrow.getLongitude());

                    mapView.invalidate();
                    return true;
                }

                @Override
                public boolean onScroll(final ScrollEvent e) {

                    Log.i("scroll", e.toString());

                    drawArrows(portNumber, myFromHW);
                    minDistArrow = getArrowDistances(width, height);
                    getInfoNearestArrow(minDistArrow.getLatitude(), minDistArrow.getLongitude());

                    mapView.invalidate();
                    return true;
                }
            }, 1000));

            ShowMyLocation(useLocation);

            if (useGPS) {
                double latitude;
                double longitude;
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();

                GeoPoint currentLocation = new GeoPoint(latitude, longitude);
                currentLocationMarker = new Marker(mapView);
                currentLocationMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
                currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                currentLocationMarker.setPanToView(true);
                currentLocationMarker.setTitle("lat: " + latitude + "\n" + "lon: " + longitude);
                currentLocationMarker.setPosition(currentLocation);

                mapView.getOverlays().add(currentLocationMarker);
                mapController.setCenter(lastKnownLocation);
                mapView.invalidate();
            }

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            width = dm.widthPixels;
            height = dm.heightPixels;
            CrossHairOverlay crossHairOverlay = new CrossHairOverlay(mapView);
            crossHairOverlay.setScaleBarOffset(0, (int) (40 * dm.density));
            crossHairOverlay.setCentred(true);
            crossHairOverlay.setScaleBarOffset(dm.widthPixels / 2, height / 2);
            mapView.getOverlays().add(crossHairOverlay);

    }
    // end of oncreate *******
    //
    //

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateHW(portSelected);
            portNumber = getPortNumber(portSelected);
            drawArrows(portNumber, 0);
            //navigateToMarkers();
            removeCurrentArrowMarker();
            mapView.invalidate();
            String calString = pn.getHWSpinnerDate(0);
            String id = pn.getButtonId(0);
            setLabels(calString, id);
        }

    };

    private void setOverlayLoc(Location overlayloc) {
        GeoPoint overlocGeoPoint = new GeoPoint(overlayloc);
        locationMarker = new Marker(mapView);

        locationMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
        locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        locationMarker.setPanToView(true);
        locationMarker.setTextLabelBackgroundColor(R.drawable.transparent);
        double dlat = overlocGeoPoint.getLatitude();
        double dlon = overlocGeoPoint.getLongitude();
        String slat = Double.toString(dlat);
        String slon = Double.toString(dlon);

        locationMarker.setTitle("lat: " + slat + "\n" + "lon: " + slon);
        locationMarker.setPosition(overlocGeoPoint);

    }

    public void drawArrows(String portNum, int fromHW) {

        int indexFromHW = fromHW + 6;
        for (Polygon line : polygons) {
            this.mapView.getOverlays().remove(line);
        }
        for (Marker marker : markers) {
            this.mapView.getOverlays().remove(marker);
        }

        mapView.invalidate();
        polygons.clear();
        markers.clear();
        markerListArrows.clear();

        double dLat, dLon;
        String dr = "";
        double dir = 0.;
        double rate = 0.;
        double spRate = 0.;
        double npRate = 0.;

        int myDiamondSize = myDiamonds.size();
        int count = 0;

        for (int i = 0; i < myDiamondSize; i++) {
            if (myDiamonds.get(i).getNum().equals(portNum)) {

                dLat = utilities.stringToLat(myDiamonds.get(i).getLat());
                dLon = utilities.stringToLon(myDiamonds.get(i).getLon());
                dr = myDiamonds.get(i).getDirRate(indexFromHW);
                if (dr.length() == 7) {
                    dir = utilities.getSpdDirSpringNeap(dr, "d");
                    spRate = utilities.getSpdDirSpringNeap(dr, "s");
                    npRate = utilities.getSpdDirSpringNeap(dr, "n");
                } else {
                    dir = 0.;
                    spRate = 0.;
                    npRate = 0.;
                }
                rate = calcRateCurrent(spRate, npRate);
                GeoPoint myLatLon = new GeoPoint(dLat, dLon);
                Projection projection = this.mapView.getProjection();
                Point myPointLatLon = projection.toPixels(myLatLon, pointXY);

                if ((dir <= 360) && (dir >= 0)) {
                    createPolygon(myPointLatLon.x, myPointLatLon.y, dir, rate);
                }
                count++;
            }
        }

        mapView.invalidate();
    }

    public int createPolygon(int x, int y, double dir, double rate) {

        double iDir = dir;

        GeoPoint myLL = new GeoPoint(50., 0.);
        List<GeoPoint> geoPoints = new ArrayList<>();

        int colour = utilities.getSpeedColour(rate);

        Polygon line = new Polygon();
        line.setFillColor(colour);
        line.setStrokeWidth(1);
        line.setStrokeColor(colour);

        double PI = 3.1412;

        if (dir < 90) {
            dir += 360;
        }

        dir -= 90;

        double rot_angle = dir;
        double sin_rot = Math.sin(rot_angle * PI / 180.);
        double cos_rot = Math.cos(rot_angle * PI / 180.);

        // Walk thru the point list
        double xt, yt;
        int x1, y1, ix1, ix2, iy1, iy2;

        xt = (double) myPoints[0].x;
        yt = (double) myPoints[0].y;

        double xp = (xt * cos_rot) - (yt * sin_rot);
        double yp = (xt * sin_rot) + (yt * cos_rot);

        double scale = 0.3 * rate;

        x1 = (int) (xp * scale);
        y1 = (int) (yp * scale);

        for (int ip = 1; ip < 9; ip++) {

            xt = (double) myPoints[ip].x;
            yt = (double) myPoints[ip].y;

            xp = (xt * cos_rot) - (yt * sin_rot);
            yp = (xt * sin_rot) + (yt * cos_rot);

            int x2 = (int) (xp * scale);
            int y2 = (int) (yp * scale);

            ix1 = x1 + x;
            iy1 = y1 + y;
            ix2 = x2 + x;
            iy2 = y2 + y;

            myLL = geoPointFromScreenCoords(ix1, iy1, this.mapView);
            if (myLL != null)
                geoPoints.add(myLL);

            myLL = geoPointFromScreenCoords(ix2, iy2, this.mapView);
            if (myLL != null)
                geoPoints.add(myLL);


            x1 = x2;
            y1 = y2;
        }

        line.setPoints(geoPoints);


        coorLoad = new Marker(mapView);

        String d2 = String.format("%03.0f", iDir);
        String r2 = String.format("%.1f", rate);

        coorLoad.setTitle(" " + d2 + " / " + r2 + " ");

        if (myLL != null) {
            coorLoad.setPosition(myLL);
        }

        // OverlayItem myItem = new OverlayItem("Title", "Description", myLL);
        // items.add(myItem);

        // mapView.getOverlays().add(coorLoad);
        markerListArrows.add(coorLoad);

        //Toast.makeText(getApplicationContext(), Double.toString(pointXY.x),
        // Toast.LENGTH_LONG).show();

        polygons.add(line);
        markers.add(coorLoad);

        this.mapView.getOverlayManager().add(line);

        return 1;
    }


    public void setLabels(String timeText, String timePlusMinus) {

        textPlusMinus = (TextView) findViewById(R.id.textPlusMinus);
        textPlusMinus.setText(timePlusMinus);

        textTime = (TextView) findViewById(R.id.textTime);
        textTime.setText(timeText);
    }

    public void onHW(View view) {

        button_id = 6; //High Water
        next_id = 7;
        back_id = 5;

        portNumber = getPortNumber(portSelected);

        spinHW.setSelection(m_myChoice);

        String st_mydate = spinHW.getSelectedItem().toString();
        Date m_dt = pn.spinHWStringToDate(st_mydate);

        myFromHW = 0;
        drawArrows(portNumber, myFromHW);
        minDistArrow = getArrowDistances(width, height);
        getInfoNearestArrow(minDistArrow.getLatitude(), minDistArrow.getLongitude());

        mapView.invalidate();

        String calString = pn.dateToString(m_dt);
        String id = pn.getButtonId(myFromHW);
        setLabels(calString, id);
    }

    public void onButtonChooseFile() {
        new FileChooser(this).setFileListener(new FileChooser.FileSelectedListener() {
            @Override
            public void fileSelected(final File file) {
                if( file == null) {
                    return;
                }
                // Show the file
                drawGPX(file.getAbsolutePath());
            }
        }).showDialog();

    }

    public int onNow(View v) {

        int i = drawNow();
        return i;
    }


    public int drawNow() {

        updateHWToday(portSelected);
        portNumber = getPortNumber(portSelected);

        Calendar calendar = Calendar.getInstance();
        Date nowDate = calendar.getTime();
        myCalendar.setTime(nowDate);
        myFromHW = pn.hoursDiffFromHWarrayTime(nowDate);

        if (myFromHW == 999) {
            return 0;
        }

        drawArrows(portNumber, myFromHW);
        minDistArrow = getArrowDistances(width, height);
        getInfoNearestArrow(minDistArrow.getLatitude(), minDistArrow.getLongitude());

        mapView.invalidate();
        searchArrows();

        String calString = pn.dateToString(nowDate);
        String id = pn.getButtonId(myFromHW);
        setLabels(calString, id);

        button_id = 6 + myFromHW;

        switch (button_id) {  // And make the label depending HW+6, HW-6 etc
            case 0: {
                next_id = 1;
                back_id = 12;
                m_myChoice = spinHW.getSelectedItemPosition();
                break;
            }
            case 12: {
                next_id = 0;
                back_id = 11;
                m_myChoice = spinHW.getSelectedItemPosition();
                break;
            }

            default: {
                next_id = button_id + 1;
                back_id = button_id - 1;
                m_myChoice = spinHW.getSelectedItemPosition();
            }
        }

        return 1;
    }

    public void onNext(View v) {

        if (fb.equals("B") || (button_id == 12)) {
            m_myChoice++;
            fb = "";
        }

        String s;
        String st_mydate;
        Date myDate;
        Date m_dt;

        button_id = next_id;

        int c = pn.getHWSpinnerItemsCount();

        long sixHours = 6 * 60 * 60 * 1000;
        long myHalfDay = 12 * 3600000;

        // Test if we have gone beyond the current list of HW.
        if (m_myChoice >= c) {
            myDate = myCalendar.getTime();
            long mi = myDate.getTime();
            mi += myHalfDay;
            myCalendar.setTimeInMillis(mi);

            String p = spinPort.getSelectedItem().toString(); // Get the port selected
            updateHW(p);

            m_myChoice = 0;
            spinHW.setSelection(m_myChoice);

        } else {
            spinHW.setSelection(m_myChoice);
        }
        //
        // End of test.
        switch (button_id) {
            case 12: {
                back_id = 11;
                next_id = 0;
                fb = "F";

                st_mydate = spinHW.getSelectedItem().toString();
                m_dt = pn.spinHWStringToDate(st_mydate);
                long mil = m_dt.getTime();
                mil += sixHours;
                s = pn.dateToString(pn.longToDate(mil));

                myFromHW = pn.buttonToHW(button_id);
                drawArrows(portNumber, myFromHW);
                minDistArrow = getArrowDistances(width, height);
                getInfoNearestArrow(minDistArrow.getLatitude(), minDistArrow.getLongitude());

                mapView.invalidate();
                searchArrows();
                myCalendar.setTimeInMillis(mil);

                String id = pn.getButtonId(myFromHW);
                setLabels(s, id);

                break;
            }
            case 0: {
                back_id = 12;
                next_id = 1;

                spinHW.setSelection(m_myChoice);
                st_mydate = spinHW.getSelectedItem().toString();
                m_dt = pn.spinHWStringToDate(st_mydate);
                long mill = m_dt.getTime();
                mill -= sixHours;
                s = pn.dateToString(pn.longToDate(mill));

                myFromHW = pn.buttonToHW(button_id);
                drawArrows(portNumber, myFromHW);
                minDistArrow = getArrowDistances(width, height);
                getInfoNearestArrow(minDistArrow.getLatitude(), minDistArrow.getLongitude());

                mapView.invalidate();

                searchArrows();
                myCalendar.setTimeInMillis(mill);

                String id = pn.getButtonId(myFromHW);
                setLabels(s, id);

                break;
            }

            default: {

                next_id++;
                st_mydate = spinHW.getSelectedItem().toString();
                m_dt = pn.spinHWStringToDate(st_mydate);
                long milli = m_dt.getTime();
                milli -= sixHours;
                milli += button_id * 60 * 60 * 1000;
                s = pn.dateToString(pn.longToDate(milli));

                myFromHW = pn.buttonToHW(button_id);
                drawArrows(portNumber, myFromHW);
                minDistArrow = getArrowDistances(width, height);
                getInfoNearestArrow(minDistArrow.getLatitude(), minDistArrow.getLongitude());

                mapView.invalidate();
                searchArrows();
                myCalendar.setTimeInMillis(milli);

                String id = pn.getButtonId(myFromHW);
                setLabels(s, id);

                back_id = next_id - 2;
            }
        }
    }

    public void onPrevious(View v) {

        if (fb.equals("F") || (button_id == 0)) {
            m_myChoice--;
            fb = "";
        }

        String s;
        String st_mydate;
        Date myDate;
        Date m_dt;

        int c = pn.getHWSpinnerItemsCount();

        button_id = back_id;

        long sixHours = 6 * 60 * 60 * 1000;
        long myOneDay = 24 * 3600000;
        long myHalfDay = 12 * 3600000;


        if (m_myChoice < 0) {
            myDate = myCalendar.getTime();
            long mi = myDate.getTime();
            mi -= myHalfDay;
            myCalendar.setTimeInMillis(mi);

            String p = spinPort.getSelectedItem().toString(); // Get the port selected
            updateHW(p);

            m_myChoice = c - 1;

            spinHW.setSelection(m_myChoice);

        } else {
            spinHW.setSelection(m_myChoice);
        }
        //
        // End of test.
        //
        switch (button_id) {
            case 12: {
                back_id = 11;
                next_id = 0;

                st_mydate = spinHW.getSelectedItem().toString();
                m_dt = pn.spinHWStringToDate(st_mydate);
                long mil = m_dt.getTime();
                mil += sixHours;
                s = pn.dateToString(pn.longToDate(mil));

                myFromHW = pn.buttonToHW(button_id);
                drawArrows(portNumber, myFromHW);
                minDistArrow = getArrowDistances(width, height);
                getInfoNearestArrow(minDistArrow.getLatitude(), minDistArrow.getLongitude());

                mapView.invalidate();
                myCalendar.setTimeInMillis(mil);

                String id = pn.getButtonId(myFromHW);
                setLabels(s, id);

                break;

            }
            case 0: {
                back_id = 12;
                next_id = 1;
                m_myChoice--;
                fb = "B";

                st_mydate = spinHW.getSelectedItem().toString();
                m_dt = pn.spinHWStringToDate(st_mydate);
                long mill = m_dt.getTime();
                mill -= sixHours;
                s = pn.dateToString(pn.longToDate(mill));
                myFromHW = pn.buttonToHW(button_id);
                drawArrows(portNumber, myFromHW);
                minDistArrow = getArrowDistances(width, height);
                getInfoNearestArrow(minDistArrow.getLatitude(), minDistArrow.getLongitude());

                mapView.invalidate();
                myCalendar.setTimeInMillis(mill);

                String id = pn.getButtonId(myFromHW);
                setLabels(s, id);

                break;
            }
            default: {

                back_id--;
                st_mydate = spinHW.getSelectedItem().toString();
                m_dt = pn.spinHWStringToDate(st_mydate);
                long milli = m_dt.getTime();
                milli -= sixHours;
                milli += button_id * 60 * 60 * 1000;
                s = pn.dateToString(pn.longToDate(milli));

                myFromHW = pn.buttonToHW(button_id);
                drawArrows(portNumber, myFromHW);
                minDistArrow = getArrowDistances(width, height);
                getInfoNearestArrow(minDistArrow.getLatitude(), minDistArrow.getLongitude());

                mapView.invalidate();
                myCalendar.setTimeInMillis(milli);

                String id = pn.getButtonId(myFromHW);
                setLabels(s, id);

                next_id = button_id + 1;
            }
        } // End switch


    }

    public void updateHW(String portName) {

        String pn = portName;

        tideComp.myHWLW.clear();

        long calTime = myCalendar.getTimeInMillis();
        arrayHW = tideComp.getHWFromCalendar(calTime, pn);
        List<String> spinnerArray = new ArrayList<String>();

        for (int i = 0; i < arrayHW.size(); i++) {
            String myHW = arrayHW.get(i).toString();
            String testH = myHW.substring(myHW.length() - 1, myHW.length());
            if (testH.equalsIgnoreCase("H")) {
                spinnerArray.add(arrayHW.get(i).toString());
            }
        }

        spinHW = (Spinner) findViewById(R.id.spinHW);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view3 = super.getView(position, convertView, parent);
                TextView text = view3.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                text.setTextSize(20);
                return view3;
            }

        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinHW.setAdapter(adapter);
        spinHW.setSelection(0);

        Double r = utilities.rangeOnDay(tideComp.myHWLW);

    }

    public void updateHWToday(String portName) {

        String pn = portName;

        tideComp.myHWLW.clear();

        long calTime = System.currentTimeMillis();
        arrayHW = tideComp.getHWFromCalendar(calTime, pn);
        List<String> spinnerArray = new ArrayList<String>();
        for (int i = 0; i < arrayHW.size(); i++) {
            String myHW = arrayHW.get(i).toString();
            String testH = myHW.substring(myHW.length() - 1, myHW.length());
            if (testH.equals("H")) {
                spinnerArray.add(arrayHW.get(i).toString());
            }
        }

        spinHW = (Spinner) findViewById(R.id.spinHW);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view2 = super.getView(position, convertView, parent);
                TextView text = view2.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                text.setTextSize(20);
                return view2;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinHW.setAdapter(adapter);
        spinHW.setSelection(0);
    }

    public void updatePort() {

        List<String> spinnerArray = new ArrayList<String>();
        for (int i = 0; i < myPorts.size(); i++) {
            spinnerArray.add(myPorts.get(i).getName());
        }
        spinPort = (Spinner) findViewById(R.id.spinPort);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                text.setTextSize(20);
                return view;
            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPort.setAdapter(adapter);
    }

    public String getPortNumber(String portName) {

        portNumber = "";

        for (int i = 0; i < myPorts.size(); i++) {
            if (myPorts.get(i).getName().equalsIgnoreCase(portName)) {
                portNumber = myPorts.get(i).getNumber();

                return portNumber;
            }
        }
        return "999";
    }

    void onSelectPort(String port) {

        portSelected = port;
        portNumber = getPortNumber(portSelected);

        updateHW(portSelected);

        int pp = spinPort.getSelectedItemPosition();
        mapController.setCenter(myPortLocations[pp]);

        drawArrows(portNumber, myFromHW);
        minDistArrow = getArrowDistances(width, height);
        getInfoNearestArrow(minDistArrow.getLatitude(), minDistArrow.getLongitude());

        removeCurrentArrowMarker();
        mapView.invalidate();

        String s;
        String st_mydate;
        Date m_dt;


        button_id = 6 + myFromHW;

        switch (button_id) {
            case 0: {
                next_id = 1;
                back_id = 12;
                m_myChoice = 0;
                break;
            }
            case 12: {
                next_id = 0;
                back_id = 11;
                m_myChoice = 0;
                break;
            }

            default: {
                next_id = button_id + 1;
                back_id = button_id - 1;
            }
        }

        spinHW.setSelection(0);

        st_mydate = spinHW.getSelectedItem().toString();
        m_dt = pn.spinHWStringToDate(st_mydate);
        long mil = m_dt.getTime() + (myFromHW * 60 * 60 * 1000);
        s = pn.dateToString(pn.longToDate(mil));
        String id = pn.getButtonId(myFromHW);
        setLabels(s, id);

    }


    public Double calcRateCurrent(Double spRate, Double npRate) {

        Double dsr, dnr;
        dsr = spRate;
        dnr = npRate;

        Double retval = 0.;

        for (int i = 0; i < myPorts.size(); i++) {
            if (myPorts.get(i).getNumber().equals(portNumber)) {
                Double sr = Double.parseDouble(myPorts.get(i).getMean_spring_range());
                Double nr = Double.parseDouble(myPorts.get(i).getMean_neap_range());
                Double r = utilities.rangeOnDay(tideComp.myHWLW);

                if (myPorts.get(i).getNumber().equals(portNumber))
                retval = utilities.CalcCurrent(sr, nr, dsr, dnr, r);
            }
        }
        return retval;
    }

    private GeoPoint geoPointFromScreenCoords(int x, int y, MapView vw) {
        if (x < 0 || y < 0 ) {
            return null; // coord out of bounds
        }
        // Get the top left GeoPoint
        Projection projection = vw.getProjection();
        GeoPoint geoPointTopLeft = (GeoPoint) projection.fromPixels(0, 0);
        Point topLeftPoint = new Point();
        // Get the top left Point (includes osmdroid offsets)
        projection.toPixels(geoPointTopLeft, topLeftPoint);
        // get the GeoPoint of any point on screen
        GeoPoint rtnGeoPoint = (GeoPoint) projection.fromPixels(x, y);
        return rtnGeoPoint;
    }

    private GeoPoint geoPointFromDisplay(int x, int y, MapView vw) {
        if (x < 0 || y < 0 || x > vw.getWidth() || y > vw.getHeight()) {
            return null; // coord out of bounds
        }
        // Get the top left GeoPoint
        Projection projection = vw.getProjection();
        // get the GeoPoint of any point on screen
        GeoPoint rtnGeoPoint = (GeoPoint) projection.fromPixels(x, y);
        return rtnGeoPoint;
    }

    private Point pointFromGeoPoint(GeoPoint gp, MapView vw) {

        Point rtnPoint = new Point();
        Projection projection = vw.getProjection();
        projection.toPixels(gp, rtnPoint);
        // Get the top left GeoPoint
        GeoPoint geoPointTopLeft = (GeoPoint) projection.fromPixels(0, 0);
        Point topLeftPoint = new Point();
        // Get the top left Point (includes osmdroid offsets)
        projection.toPixels(geoPointTopLeft, topLeftPoint);
        rtnPoint.x -= topLeftPoint.x; // remove offsets
        rtnPoint.y -= topLeftPoint.y;
        if (rtnPoint.x > vw.getWidth() || rtnPoint.y > vw.getHeight() ||
                rtnPoint.x < 0 || rtnPoint.y < 0) {
            return null; // gp must be off the screen
        }
        return rtnPoint;
    }

    public void makeDiamonds() throws IOException{

        AssetManager aManager = getAssets();
        InputStream fiStream = aManager.open("vmh_data/ukho.txt");

        try {
            myDiamonds = DiamondParser.parse(fiStream);
            fiStream.close();

        } catch (Exception e) {
            //Log.e("Assets Error:", e.toString());
            //Toast.makeText(getApplicationContext(), "error",
                   // Toast.LENGTH_LONG).show();
        }

        fiStream.close();

    }

    void makePorts() throws IOException{

        AssetManager aManager = getAssets();
        InputStream fiStream = aManager.open("vmh_data/ukports.txt");

        try {
            myPorts = PortParser.parse(fiStream);
            fiStream.close();

        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(), "error",
                    //Toast.LENGTH_LONG).show();
        }

        fiStream.close();

    }

    String copyAssetToString(String source, String encoding) {
        StringBuilder os = new StringBuilder();
        try {
            InputStream is = getAssets().open(source);
            Reader ir = new InputStreamReader(is, encoding);
            char[] buf = new char[4096];
            int len;
            while ((len = ir.read(buf)) > 0) {
                os.append(buf, 0, len);
            }
            ir.close();
        } catch (Exception e) {
            //Log.e("CopyATS Error:", e.toString());
        }
        return os.toString();
    }


    public void onResume() {
        super.onResume();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showLocation = sharedPreferences.getBoolean("ShowMyLocation", false);

        ShowMyLocation(showLocation);

        mapView.onResume();
    }

    public void onPause() {
        super.onPause();

        mapView.onPause();
    }


    public void drawGPX(String myRoute) {

        List<GeoPoint> myRoutePoints = new ArrayList<>();
        GeoPoint myRoutePoint;

        File file = new File(myRoute);
        try {
            InputStream in = new FileInputStream(file);
            parsedGpx = mParser.parse(in);
        } catch (IOException | XmlPullParserException e) {
            // do something with this exception
            String s = "GPX File has not been found";
            Toast.makeText(getApplicationContext(), s,
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        if (parsedGpx != null) {
            // log stuff
            List<Route> routes = parsedGpx.getRoutes();
            for (int i = 0; i < routes.size(); i++) {
                Route route = routes.get(i);

                List<RoutePoint> routePoints = route.getRoutePoints();
                for (int j = 0; j < routePoints.size(); j++) {
                    RoutePoint routePoint = routePoints.get(j);
                    double lat = routePoint.getLatitude();
                    double lon = routePoint.getLongitude();
                    myRoutePoint = new GeoPoint(lat, lon);
                    myRoutePoints.add(myRoutePoint);
                }
            }
        } else {

            //
        }

        myRouteLine.setPoints(myRoutePoints);
        myRouteLine.setWidth(1);

        this.mapView.getOverlayManager().add(myRouteLine);
        this.mapView.invalidate();
    }

    public void onClearGPX() {
        this.mapView.getOverlayManager().remove(myRouteLine);
        this.mapView.invalidate();
    }

    public class CopyData extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(Void... params) {
            CopyAssets();
            return "Done";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        private void CopyAssets() {

            AssetManager assetManager = getAssets();
            String[] files = null;
            try {
                files = assetManager.list("routes");
            } catch (IOException e) {

            }

            for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open("routes/" + filename);
                    out = new FileOutputStream(Environment.getExternalStorageDirectory().toString()  + "/Download/" + filename);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                } finally {
                    // Ensure that the InputStreams are closed even if there's an exception.
                    try {
                        if (out != null) {
                            out.close();
                        }

                        // If you want to close the "in" InputStream yourself then remove this
                        // from here but ensure that you close it yourself eventually.
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void copyFile(InputStream in, OutputStream out) throws IOException {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }

    }

    public class showSplash extends AsyncTask<Void, Void, String> {
        public Intent intent2;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(Void... params) {
            if (showMe.equals("yes")) { //the default at the start
                intent2 = new Intent();
                intent2.setClassName(getApplicationContext(), "com.rossiter.mike.uktidalstreams.ShowSplash");
                startActivity(intent2);
                return "Done";
            } else {
                finish();
                return "Done";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }

    private void searchArrows() {

        int myArrowsSize = markerListArrows.size();

        for (int i = 0; i < myArrowsSize; i++) {
            double arrowLat = markerListArrows.get(i).getPosition().getLatitude();
            double arrowLon = markerListArrows.get(i).getPosition().getLongitude();

            if (arrowLat == currentArrowMarker.getPosition().getLatitude() && arrowLon == currentArrowMarker.getPosition().getLongitude()) {

                TextView textDirSpd = (TextView) findViewById(R.id.textDirSpd);
                textDirSpd.setText(markerListArrows.get(i).getTitle());
            }
        }
    }

    private void getInfoNearestArrow(Double lat, Double lon) {

        int myArrowsSize = markerListArrows.size();

        for (int i = 0; i < myArrowsSize; i++) {
            double arrowLat = markerListArrows.get(i).getPosition().getLatitude();
            double arrowLon = markerListArrows.get(i).getPosition().getLongitude();

            if (arrowLat == lat && arrowLon == lon) {

                TextView textDirSpd = (TextView) findViewById(R.id.textDirSpd);
                textDirSpd.setText(markerListArrows.get(i).getTitle());
            }
        }
    }

    private GeoPoint getArrowDistances(int w, int h) {
        int R = 6371; // km
        List<Double> myDistances = new ArrayList();
        int myArrowsSize = markerListArrows.size();


        GeoPoint myCentre = geoPointFromScreenCoords(w / 2, h / 2, this.mapView);
        Double myCentreLat = myCentre.getLatitude();

        Double myCentreLon = myCentre.getLongitude();
        Double minDistance = 1000000.0;
        GeoPoint nearestGeoPoint = new GeoPoint(0.0, 0.0);

        for (int i = 0; i < myArrowsSize; i++) {
            double arrowLat = markerListArrows.get(i).getPosition().getLatitude();
            double arrowLon = markerListArrows.get(i).getPosition().getLongitude();

            double x = (arrowLon - myCentreLon) * Math.cos((myCentreLat + arrowLat) / 2);
            double y = (arrowLat - myCentreLat);
            double distance = Math.sqrt(x * x + y * y) * R;
            if (distance < minDistance) {
                minDistance = distance;
                nearestGeoPoint.setLatitude(arrowLat);
                nearestGeoPoint.setLongitude(arrowLon);

            }
        }

        return nearestGeoPoint;
    }

    private void removeCurrentArrowMarker() {

        mapView.getOverlays().remove(currentArrowMarker);

        TextView textDirSpd = (TextView) findViewById(R.id.textDirSpd);
        textDirSpd.setText("");

    }

    private void copyInputStreamToFile(InputStream in, File file) {
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "error",
                    Toast.LENGTH_LONG).show();
        } finally {
            // Ensure that the InputStreams are closed even if there's an exception.
            try {
                if (out != null) {
                    out.close();
                }

                // If you want to close the "in" InputStream yourself then remove this
                // from here but ensure that you close it yourself eventually.
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    void showMapsforgeFile(File mapFile) {
        File[] maps = new File[1];

        maps[0] = mapFile;

        XmlRenderTheme theme = null;
        try {
            theme = new AssetsRenderTheme(this.getApplicationContext(), "renderthemes/", "rendertheme-v4.xml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        fromFiles = MapsForgeTileSource.createFromFiles(maps, theme, "rendertheme-v4");
        forge = new MapsForgeTileProvider(
                new SimpleRegisterReceiver(this),
                fromFiles, null);

        mapView.setTileProvider(forge);

    }



    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString()  + "/Download/uk.map");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String myMapName = Environment.getExternalStorageDirectory().toString() + "/Download/uk.map";
            // setting downloaded into image view
            //my_image.setImageDrawable(Drawable.createFromPath(imagePath));
            File file = new File(myMapName);
            showMapsforgeFile(file);
        }

    }

    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    public void ShowMyLocation(boolean show) {

        if (show) {

            myLocLat = sharedPreferences.getString("MyLat", "0.0");
            myLocLon = sharedPreferences.getString("MyLon", "0.0");

            double dLocLat = 49.;
            double dLocLon = -4.; //Double.parseDouble(myLocLon);

            try {
                dLocLat = Double.parseDouble(myLocLat);
                isMyNumber = true;
            } catch (NumberFormatException e) {
                isMyNumber = false;
            }

            try {
                dLocLon = Double.parseDouble(myLocLon);
                isMyNumber = true;
            } catch (NumberFormatException e) {
                isMyNumber = false;
            }

            if (isMyNumber) {

                GeoPoint locationPoint = new GeoPoint(dLocLat, dLocLon);
                locationMarker = new Marker(mapView);

                locationMarker.setIcon(getResources().getDrawable(R.drawable.ic_gps_not_fixed));
                locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                locationMarker.setPanToView(true);
                locationMarker.setTextLabelBackgroundColor(R.drawable.transparent);
                double dlat = locationPoint.getLatitude();
                double dlon = locationPoint.getLongitude();
                String slat = Double.toString(dlat);
                String slon = Double.toString(dlon);

                locationMarker.setTitle("lat: " + slat + "\n" + "lon: " + slon);
                locationMarker.setPosition(locationPoint);


                mapView.getOverlays().add(locationMarker);
                mapController.setCenter(locationPoint);
                mapView.invalidate();
            } else {
               //
            }

        }
        else {
            mapView.getOverlays().remove(locationMarker);
            mapView.invalidate();
        }
    }


}
