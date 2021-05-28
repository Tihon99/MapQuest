package com.example.mymapapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class NovelActivity extends AppCompatActivity {

    TextView textNovella;
    AppCompatButton buttonRight;
    AppCompatButton buttonLeft;

    int count;
    String partNovel;

    Quest quest;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel);

        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        textNovella     =   findViewById(R.id.text_view_novel);
        buttonLeft      =   findViewById(R.id.button_left);
        buttonRight     =   findViewById(R.id.button_right);

        partNovel = getIntent().getStringExtra("partString");
        int part = getIntent().getIntExtra("partInt", 0);

        quest = new Quest(0,0,"", false, false);

        if (part == 0) {

            String lines[] = readFile(quest.getTagButtonOne(partNovel)).split("\n");

            count = 1;
            textNovella.setText(lines[count]);

            buttonRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (count + 1 != lines.length) {
                        count++;
                        textNovella.setText(lines[count]);
                        if (count + 1 == lines.length) {
                            buttonRight.setText("Вернуться на карту");
                            buttonRight.setBackground(getResources().getDrawable(R.drawable.button_slyle_stoke_yellow));
                        }
                        Log.d("Novel-if", "if-" + lines[count]);
                    } else {
                        Intent intent1 = new Intent(NovelActivity.this, MapsActivity.class);
                        intent1.putExtra("markerChoosed", true);
                        intent1.putExtra("part1", partNovel);
                        //Log.d("Novel-if", "else" + partNovel);
                        setResult(Constants.REQUEST_CODE_ONE, intent1);
                        finish();
                    }

                }
            });

            buttonLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (count > 1) {
                        buttonRight.setText("вперёд");
                        buttonRight.setBackground(getResources().getDrawable(R.drawable.button_slyle_stoke_blue));
                        count--;
                        textNovella.setText(lines[count]);
                    }
                }
            });
        } else {

            String lines[] = readFile(quest.getTagButtonTwo(partNovel)).split("\n");
            count = 1;
            textNovella.setText(lines[count]);

            buttonRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (count + 1 != lines.length) {
                        count++;
                        textNovella.setText(lines[count]);
                        if (count + 1 == lines.length) {
                            buttonRight.setText("Вернуться на карту");
                            buttonRight.setBackground(getResources().getDrawable(R.drawable.button_slyle_stoke_yellow));
                        }
                    } else {
                        Intent intent1 = new Intent(NovelActivity.this, MapsActivity.class);
                        intent1.putExtra("markerChoosed", false);
                        intent1.putExtra("part1", partNovel);
                        //Log.e("login activity", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + partNovel);
                        setResult(Constants.REQUEST_CODE_ONE, intent1);
                        finish();
                    }

                }
            });

            buttonLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (count > 1) {
                        buttonRight.setText("вперёд");
                        buttonRight.setBackground(getResources().getDrawable(R.drawable.button_slyle_stoke_blue));
                        count--;
                        textNovella.setText(lines[count]);
                    }
                }
            });
        }

    }

    private String readFile(String tag) {
        String ret = "";
        tag += ".txt";
        Log.d("File", "readFile(): " + tag);
        try {
            InputStream inputStream;

            inputStream = getApplicationContext().getAssets().open(tag);

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
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }
}
