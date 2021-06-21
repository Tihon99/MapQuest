package game.tkh.mapquest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {


    long downloadID;
    SQLiteDatabase db;
    Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        db = getBaseContext().openOrCreateDatabase("app_data.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS quests (id INTEGER PRIMARY KEY AUTOINCREMENT, lat REAL, lon REAL, tag TEXT, readed INTEGER, selected INTEGER, position INTEGER)");
        readQuestsFromFile(db);

        DownloadFile("https://tihon99.github.io/files/quests.csv", "questList", "quests_buf.csv");
        mainIntent = new Intent(this, MapsActivity.class);

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                try {
                    if(checkUpdateFile()) {
                        readQuestsFromFile(db);

                    }
                    startActivity(mainIntent);
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));



        ImageView imageView = findViewById(R.id.imageSplash);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade);

        animation.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });


        imageView.startAnimation(animation);
    }

    private void DownloadFile(String uri, String destFolder, String fileName) {
        String path = getExternalFilesDir(null).getAbsolutePath() + "/" + destFolder + "/" + fileName;
        File f = new File(path);
        if(f.exists() && !f.isDirectory()) {
            f.delete();
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri))
                .setTitle("Quest list")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setAllowedOverMetered(true)
                .setDestinationInExternalFilesDir(this,destFolder + File.separator , fileName);
        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadID = dm.enqueue(request);
    }


    private boolean checkUpdateFile() throws IOException {
        String path = getExternalFilesDir(null).getAbsolutePath() + "/questList/";
        String path2 = getExternalFilesDir(null).getAbsolutePath() + "/questList/";
        File f1 = new File(path, "quests.csv");
        File f2 = new File(path, "quests_buf.csv");
        Log.d("filerename", f1.getAbsolutePath());
        if(f1.exists() && !f1.isDirectory()) {
            BufferedReader br1 = new BufferedReader(new FileReader(path + "quests.csv"));
            BufferedReader br2 = new BufferedReader(new FileReader(path + "quests_buf.csv"));
            LocalDateTime dateTime1 = LocalDateTime.parse(br1.readLine());
            LocalDateTime dateTime2 = LocalDateTime.parse(br2.readLine());
            br1.close();
            br2.close();
            if(!dateTime1.isEqual(dateTime2)) {
                f2.renameTo(f1);
                return true;
            }
            return false;

        }
        else {
            f1.delete();
            f2.setReadable(true);
            f2.setWritable(true);
            boolean s = f2.renameTo(f1);
            Log.d("filerename", s+"  " + f2.getName());
            Log.d("filerename", f1.getName());
            return true;
        }

    }

    private void readQuestsFromFile(SQLiteDatabase db) {
        List myEntries = null;
        Map<String, String> values;
        CSVReaderHeaderAware reader = null;
        String tableName = "quests";
        ArrayList<String> tagsDB = new ArrayList<>();
        ArrayList<String> tagsCSV = new ArrayList<>();
        int countDB =  (int) DatabaseUtils.queryNumEntries(db, tableName);
        try {
            //Map<String, String> values = new CSVReaderHeaderAware(new FileReader(getExternalFilesDir(null).getAbsolutePath() + "/questList/quests.csv")).readMap();
            FileReader file = new FileReader(getExternalFilesDir(null).getAbsolutePath() + "/questList/quests.csv");

            Log.d("file3", file.toString());
            BufferedReader br = new BufferedReader(new FileReader(getExternalFilesDir(null).getAbsolutePath() + "/questList/quests.csv"));
            br.readLine();
            reader = new CSVReaderHeaderAware(br);
            //String[] nextLine;
            Log.d("tag3", "----------------");
            ArrayList<String> arrayFileNames = new ArrayList();

            while (((values = (Map<String, String>) reader.readMap())) != null)
            {
                String tag = values.get("tag");
                tagsCSV.add(tag);
                String fileName = tag.replaceAll("[^A-Za-z]","");
                float lat = Float.parseFloat(values.get("lat"));
                float lon = Float.parseFloat(values.get("lon"));
                //Log.d("tag23", tag +","+lat+","+lon);
                if(!arrayFileNames.contains(fileName)) {
                    Log.d("tag3", fileName);
                    arrayFileNames.add(fileName);
                    DownloadFile("https://tihon99.github.io/files/textNovels/" + fileName + ".txt", "questTexts", fileName + ".txt");
                  /*  DownloadFile("https://tihon99.github.io/files/quests/" + fileName + "one.txt", "questTexts", fileName + "one.txt");
                    DownloadFile("https://tihon99.github.io/files/quests/" + fileName + "two.txt", "questTexts", fileName + "two.txt");*/
                }
                Log.d("tag3", tag);
                if(!CheckDataExist(db, tableName, "tag", tag)) {
                    db.execSQL("INSERT INTO quests VALUES (null, "+lat+", "+lon+", \""+tag+"\", 0, 0, 0);");
                }
                else {
                    ContentValues cv = new ContentValues();
                    cv.put("lat",lat);
                    cv.put("lon",lon);
                    db.update(tableName, cv, "tag = ?", new String[]{tag});
                }
            }
            if(tagsCSV.size() < countDB) {
                Cursor libSQL = db.rawQuery("Select * from quests",null);
                libSQL.moveToFirst();

                while(!libSQL.isAfterLast()) {
                    String tag = libSQL.getString(libSQL.getColumnIndex("tag"));
                    tagsDB.add(tag);
                    libSQL.moveToNext();
                }
                for (String tagDB: tagsDB) {
                    if(!tagsCSV.contains(tagDB)) {
                        db.delete(tableName,"tag = ?", new String[]{tagDB});
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }
    }


    public static boolean CheckDataExist(SQLiteDatabase db, String TableName, String dbfield, String fieldValue) {
        String Query = String.format("Select * from %s where %s = \"%s\"", TableName, dbfield, fieldValue);
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

}
