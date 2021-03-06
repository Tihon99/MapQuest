package game.tkh.mapquest;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, CustomResultReceiver.AppReceiver {

    private CustomResultReceiver resultReceiver;
    private View infoWindowContainer;
    private View readNovelContainer;
    SQLiteDatabase db;

    double lat, lon;
    LatLng userPlace;
    Marker userLoc;
    MarkerOptions user;
    int userRadius = 2000;

    String part;

    boolean camMoveToUser = true;
    boolean markerChoosed = false;
    boolean flagQuest = false;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    private GoogleMap mMap;
    HashMap<String, Marker> markDict = new HashMap<>();
    HashMap<String, Quest> questsDict = new HashMap<>();
    Marker savedMarker;


    TextView tasks;
    TextView title;
    TextView description;
    AppCompatButton readNovelBtn;
    AppCompatButton button;
    FloatingActionButton readedBtn;



    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationService();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startLocationService();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        userPlace = new LatLng(52.27537, 104.2774);

        Drawable vector = getResources().getDrawable(R.drawable.ic_baseline_emoji_people_24);
        Bitmap bitmap = getBitmap((VectorDrawable) vector, R.color.black);
        user = new MarkerOptions().position(userPlace).title("???? ??????????").icon(BitmapDescriptorFactory.fromBitmap(bitmap));


        title = findViewById(R.id.quest_title);
        button = findViewById(R.id.button);
        readNovelBtn = findViewById(R.id.button_read_novel);
        description = findViewById(R.id.descriprion);
        readedBtn = findViewById(R.id.readed_button);

        readedBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReadedActivity.class);
            startActivity(intent);
        });


        readNovelBtn.setVisibility(View.INVISIBLE);

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        }
        //onRequestPermissionsResult();
        else {
            startLocationService();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        tasks = findViewById(R.id.tasks);
        tasks.setText("???????????????? ?????????? ???? ??????????");


        infoWindowContainer = findViewById(R.id.container_popup);
        infoWindowContainer.setVisibility(View.INVISIBLE);

        db = getBaseContext().openOrCreateDatabase("app_data.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS quests (id INTEGER PRIMARY KEY AUTOINCREMENT, lat REAL, lon REAL, tag TEXT, readed INTEGER, selected INTEGER, position INTEGER)");
        Cursor libSQL = db.rawQuery("Select * from quests",null);
        /*
        if(libSQL.getCount() == 0) {
            readQuestsFromFile(db, true);
        }*/
        libSQL.moveToFirst();

        while(!libSQL.isAfterLast()) {
            String tag = libSQL.getString(libSQL.getColumnIndex("tag"));
            boolean readed = (libSQL.getInt(libSQL.getColumnIndex("readed")) != 0);
            boolean selected = (libSQL.getInt(libSQL.getColumnIndex("selected")) != 0);
            float lat = libSQL.getFloat(libSQL.getColumnIndex("lat"));
            float lon = libSQL.getFloat(libSQL.getColumnIndex("lon"));
            int position = libSQL.getInt(libSQL.getColumnIndex("position"));
            Log.d("tag", tag +","+lat+","+lon);
            Quest quest = new Quest(lat, lon, tag, readed, selected, position);
            questsDict.put(tag, quest);

            if(selected) {
                markerChoosed = true;
                button.setText("????????????");
            }
            libSQL.moveToNext();
        }
        Log.d("tag", questsDict.size()+"");


    }

    public String getFileText (int position, String tag) {

        String lines[] = readFile(tag.replaceAll("[^A-Za-z]","")).split("\n");
        String ret = "";

        for (int i = 1; i < lines.length; i++) {

            if (lines[i].equals(Integer.toString(position) + "**")) {

                while (i + 1 < lines.length && !lines[i + 1].equals(Integer.toString(position + 1) + "**")) {
                    i++;
                    ret += lines[i];
                }
            }

        }
        return ret;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        addMarkers(questsDict, mMap);
        if(markerChoosed) {
            Drawable vectorBook = getResources().getDrawable(R.drawable.ic_baseline_menu_book_24);
            Bitmap bitmapBook = getBitmap((VectorDrawable) vectorBook, R.color.black);
            savedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapBook));
            tasks.setText("?????????? ?? ?????????????? ????????????");
        }

        mMap.setOnMapClickListener(latLng -> {
            infoWindowContainer.setVisibility(View.INVISIBLE);
            if(!markerChoosed) {
                savedMarker = null;
            }
        });
        mMap.setOnMarkerClickListener(m -> {
            if(!m.getTitle().equals("???? ??????????")){
                title.setText(m.getTitle());
                description.setText(getFileText(0, (String) m.getTag()));
                infoWindowContainer.setVisibility(View.VISIBLE);
                savedMarker = m;
            }
            return false;
        });


    }



    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context. ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager. RunningServiceInfo service : activityManager. getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }



    private void startLocationService() {
        if(!isLocationServiceRunning()) {
            Intent intent = new Intent(MapsActivity.this, LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            resultReceiver = new CustomResultReceiver(new Handler(), this);
            intent.putExtra("receiver", resultReceiver);
            startService(intent);
            Toast. makeText( this, "Location service started", Toast.LENGTH_SHORT). show();
        }
    }




    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent. setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast. makeText( this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }



    private void registerService() {
        Intent intent = new Intent(getApplicationContext(), LocationService.class);
        resultReceiver = new CustomResultReceiver(new Handler(), this);
        intent.putExtra("receiver", resultReceiver);
        startService(intent);
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        lat = resultData.getDouble("lat");
        lon = resultData.getDouble("lon");
        if(lat != 0) {
            if(userLoc != null) {
                userLoc.remove();
            }
            userPlace = new LatLng(lat, lon);

            user.position(userPlace);
            userLoc = mMap.addMarker(user);
            changeVisMarkers(questsDict, mMap);

            if(camMoveToUser) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPlace, 13f));
                camMoveToUser = !camMoveToUser;
            }

            button.setOnClickListener(v -> {
                if(savedMarker != null && !markerChoosed) {
                    String tag = savedMarker.getTag().toString();
                    tag.replaceAll("[^A-Za-z]","");
                    questsDict.get(tag).position++;
                    Intent intent = new Intent(MapsActivity.this, NovelActivity.class);
                    intent.putExtra("partString", tag);
                    //intent.putExtra("markOption", questsDict.get(tag).marker);
                    startActivityForResult(intent, Constants.REQUEST_CODE_ONE);
                }

                else if(markerChoosed) {
                    button.setText("????????????");
                    markerChoosed = false;
                    String tag = savedMarker.getTag().toString();
                    tag.replaceAll("[^A-Za-z]","");
                    questsDict.get(tag).position = 0;
                    //savedMarker = null;
                    mMap.clear();
                    userLoc = mMap.addMarker(user);
                    addMarkers(questsDict, mMap);
                    infoWindowContainer.setVisibility(View.INVISIBLE);
                    button.setText("???????????? ??????????");
                    tasks.setText("???????????????? ?????????? ???? ??????????");
                }

            });

            readNovelBtn.setOnClickListener(v -> {
                Intent intent = new Intent(MapsActivity.this, NovelActivity.class);
                if(part == null) {
                    part = savedMarker.getTag().toString();
                }
                String tag = savedMarker.getTag().toString();
                tag.replaceAll("[^A-Za-z]","");
                questsDict.get(tag).position += 1;
                Log.d("dist", "" + questsDict.get(tag).position);
                intent.putExtra("partString", part);
                intent.putExtra("position", questsDict.get(tag).position);

                startActivityForResult(intent, Constants.REQUEST_CODE_ONE);

            });


            if(markerChoosed && savedMarker != null ) {
                LatLng mcoords = savedMarker.getPosition();
                Location locationOne = new Location("Quest");
                Location locationTwo = new Location("User");
                locationOne.setLatitude(mcoords.latitude);
                locationOne.setLongitude(mcoords.longitude);
                locationTwo.setLatitude(userPlace.latitude);
                locationTwo.setLongitude(userPlace.longitude);
                float distance = findDistance(savedMarker.getPosition(), userPlace);
                if(distance < 15) {
                    String tag = savedMarker.getTag().toString();
                    tag.replaceAll("[^A-Za-z]","");

                    Drawable vectorBook = getResources().getDrawable(R.drawable.ic_baseline_menu_book_24);
                    Bitmap bitmapBook = getBitmap((VectorDrawable) vectorBook, R.color.gold);

                    savedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapBook));
                    readNovelBtn.setClickable(markerChoosed);
                    tasks.setText("???????????????? ??????????????");
                    readNovelBtn.setVisibility(View.VISIBLE);
                }
            }


        }

    }

    Bitmap getBitmap(VectorDrawable vectorDrawable, int color) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth()*2, vectorDrawable.getIntrinsicHeight()*2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(color), PorterDuff.Mode.MULTIPLY));
        vectorDrawable.setBounds(0,0,canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    void changeVisMarkers(HashMap<String, Quest> questsDict, GoogleMap mMap) {
        for(Quest q: questsDict.values()) {
            LatLng questPos = q.marker.getPosition();

            if(q.readed || findDistance(questPos, userPlace) > userRadius || (markerChoosed && !q.selected)) {
                markDict.get(q.tag).setVisible(false);
            }
            else {
                markDict.get(q.tag).setVisible(true);
            }
            if(markerChoosed && q.selected && !q.readed) {
                markDict.get(q.tag).setVisible(true);
            }
        }
    }

    void addMarkers(HashMap<String, Quest> questsDict, GoogleMap mMap) {

        for(Quest q: questsDict.values()) {
            Marker marker = mMap.addMarker(q.marker);
            marker.setTag(q.tag);
            if(q.selected) {
                savedMarker = marker;
                savedMarker.setTag(q.tag);
            }
            markDict.put(q.tag, marker);
        }
        changeVisMarkers(questsDict, mMap);
    }


    float findDistance(LatLng mcoords, LatLng userPlace) {
        Location locationOne = new Location("Quest");
        Location locationTwo = new Location("User");
        locationOne.setLatitude(mcoords.latitude);
        locationOne.setLongitude(mcoords.longitude);
        locationTwo.setLatitude(userPlace.latitude);
        locationTwo.setLongitude(userPlace.longitude);
        return locationOne.distanceTo(locationTwo);
    }


    @Override
    protected void onStop() {
        super.onStop();

        if(resultReceiver != null) {
        }
    }

    private String readFile(String tag) {
        String ret = "";
        tag += ".txt";

        try {
            InputStream inputStream = getApplicationContext().getAssets().open(tag);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String receiveString = "";

                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.d("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.d("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_ONE) {
            readNovelBtn.setClickable(false);
            markerChoosed = data.getBooleanExtra("markerChoosed", false);
            part = data.getStringExtra("part1");
            readNovelBtn.setVisibility(View.INVISIBLE);
            infoWindowContainer.setVisibility(View.INVISIBLE);
            if(markerChoosed) {
                updateQuestStat(part, "selected", 1);
                button.setText("????????????");
                mMap.clear();
                savedMarker = mMap.addMarker(questsDict.get(part).marker);
                savedMarker.setTag(part);
                userLoc = mMap.addMarker(user);
                Drawable vectorBook = getResources().getDrawable(R.drawable.ic_baseline_menu_book_24);
                Bitmap bitmapBook = getBitmap((VectorDrawable) vectorBook, R.color.black);
                savedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapBook));
                tasks.setText("?????????? ?? ?????????????? ????????????");
            }
            else {
                updateQuestStat(part, "selected", 0);
                updateQuestStat(part, "readed", 1);
                questsDict.get(part).readed = true;
                questsDict.get(part).selected = false;
                savedMarker = null;
                mMap.clear();
                addMarkers(questsDict, mMap);
                button.setText("???????????? ??????????");
                tasks.setText("???????????????? ?????????? ???? ??????????");
            }

        }
    }

    public boolean updateQuestStat(String tag, String field, int value) {
        ContentValues values = new ContentValues();
        values.put(field, value);
        int rows = db.update("quests", values,  "tag = ?" , new String[] {tag} );
        return (rows > 0);
    }
}





