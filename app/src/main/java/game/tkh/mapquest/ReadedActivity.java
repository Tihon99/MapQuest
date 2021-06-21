package game.tkh.mapquest;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class ReadedActivity extends AppCompatActivity implements MyAdapter.OnQuestListener{

    SQLiteDatabase db;

    RecyclerView recyclerView;

    ArrayList<Quest> readedList;
    Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readed);

        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        readedList = new ArrayList<>();

        closeButton = findViewById(R.id.back_button);

        db = getBaseContext().openOrCreateDatabase("app_data.db", MODE_PRIVATE, null);

        Cursor libSQL = db.rawQuery("Select * from quests",null);
        libSQL.moveToFirst();
        while(!libSQL.isAfterLast()) {
            String tag = libSQL.getString(libSQL.getColumnIndex("tag"));
            boolean readed = (libSQL.getInt(libSQL.getColumnIndex("readed")) != 0);
            boolean selected = (libSQL.getInt(libSQL.getColumnIndex("selected")) != 0);
            float lat = libSQL.getFloat(libSQL.getColumnIndex("lat"));
            float lon = libSQL.getFloat(libSQL.getColumnIndex("lon"));
            int position = libSQL.getInt(libSQL.getColumnIndex("position"));
            if(readed) {
                readedList.add(new Quest(lat, lon, tag, readed, selected, position));
            }
            libSQL.moveToNext();
        }

        //recyclerView = findViewById(R.id.closeButton);

        recyclerView = findViewById(R.id.recList);
        MyAdapter adapter = new MyAdapter(this, readedList, this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        closeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onQuestCLick(int position) {
        Quest quest = readedList.get(position);
        String tag = quest.tag;
        Intent intent = new Intent(this, NovelActivity.class);
        intent.putExtra("partString", tag);
        intent.putExtra("markOption", quest.marker);
        intent.putExtra("partInt", 1);
        startActivityForResult(intent, Constants.REQUEST_CODE_ONE);
    }

}