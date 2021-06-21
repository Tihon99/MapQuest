package game.tkh.mapquest;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class NovelActivity extends AppCompatActivity {

    TextView textNovella;
    AppCompatButton buttonRight;
    AppCompatButton buttonLeft;

    int position, i = 1, novelCode, j = 1, pos = 1;
    Quest quest;
    String partNovel;


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
        novelCode = getIntent().getIntExtra("partInt", 0);
        position = getIntent().getIntExtra("position", 1);

        quest = new Quest(0,0,"", false, false, position);

        if (novelCode == 0) {

            //String lines[] = readFile(partNovel.replaceAll("[^A-Za-z]","")).split("\n");
            Log.d("lines", "" + position);
            String lines[] = readFile(partNovel);
            Log.d("lines", "" + partNovel);
            Log.d("lines", "" + Integer.toString(position));

            for (i = 1; i < lines.length; i++) {
                if (lines[i].equals(Integer.toString(position) + "**")) {
                    i++;
                    textNovella.setText(lines[i]);
                    Log.d("linesi", "" + i);
                    break;
                }
            }



            buttonRight.setOnClickListener(v -> {
                Log.d("linesi", "" + i);

                if (i + 1 == lines.length || lines[i].equals(Integer.toString(position + 1) + "**")) {
                    if (lines[i].equals(Integer.toString(position + 1) + "**")) {
                        Intent intent1 = new Intent(NovelActivity.this, MapsActivity.class);
                        intent1.putExtra("markerChoosed", true);
                        intent1.putExtra("part1", partNovel);
                        //Log.d("Novel-if", "else" + partNovel);
                        setResult(Constants.REQUEST_CODE_ONE, intent1);
                        finish();
                    }
                    else {
                        Intent intent1 = new Intent(NovelActivity.this, MapsActivity.class);
                        intent1.putExtra("markerChoosed", false);
                        intent1.putExtra("part1", partNovel);
                        //Log.d("Novel-if", "else" + partNovel);
                        setResult(Constants.REQUEST_CODE_ONE, intent1);
                        finish();
                    }
                }
                else
                    if (i + 1 < lines.length && !lines[i + 1].equals(Integer.toString(position + 1) + "**")) {
                        i++;
                        textNovella.setText(lines[i]);
                        if (i + 2 == lines.length || lines[i + 1].equals(Integer.toString(position + 1) + "**")) {
                            buttonRight.setText("Вернуться на карту");
                            buttonRight.setBackground(getResources().getDrawable(R.drawable.button_slyle_stoke_yellow));
                            i++;
                        }
                    }

            });


            buttonLeft.setOnClickListener(v -> {
                if (!lines[i - 1].equals(Integer.toString(1) + "**") && !lines[i - 1].equals(Integer.toString(position) + "**")) {
                    buttonRight.setText("вперёд");
                    buttonRight.setBackground(getResources().getDrawable(R.drawable.button_slyle_stoke_blue));
                    i--;
                    textNovella.setText(lines[i]);
                }
            });

            /*for ( i = 1; i < lines.length; i++) {
                if ((lines[i].equals(Integer.toString(position + 1) + "**") || i + 1 == lines.length) && position > 0) {
                    if (i + 1 == lines.length) {
                        buttonLeft.setOnClickListener(v -> {
                            if (i > 1 && !lines[i].equals(Integer.toString(position) + "**")) {
                                textNovella.setText(lines[i]);
                                i--;
                            }
                        });

                    }
                    else {
                        buttonLeft.setOnClickListener(v -> {
                            if (i - 1 > 1 && !lines[i - 1].equals(Integer.toString(position) + "**"));{
                                i--;
                                textNovella.setText(lines[i]);
                            }
                        });
                    }
                }
            }*/

        }

        else {

            String lines[] = readFile(partNovel);

            for (i = 1; i < lines.length; i++) {
                if (lines[i].equals(Integer.toString(pos) + "**")) {
                    i++;
                    textNovella.setText(lines[i]);
                    Log.d("linesi", "" + i);
                    break;
                }
            }

            buttonRight.setOnClickListener(v -> {
                Log.d("linesi", "" + i);

                if (i + 1 == lines.length) {
                    Intent intent1 = new Intent(NovelActivity.this, ReadedActivity.class);
                    startActivity(intent1);
                    finish();

                }
                else {
                    i++;
                    textNovella.setText(lines[i]);
                    if (lines[i + 1].equals(Integer.toString(pos + 1) + "**")) {
                        i++;
                        pos++;
                    }
                    if (i + 2 == lines.length) {
                        buttonRight.setText("Вернуться на карту");
                        buttonRight.setBackground(getResources().getDrawable(R.drawable.button_slyle_stoke_yellow));
                        i++;
                    }
                }

            });

            buttonLeft.setOnClickListener(v -> {
                if (!lines[i - 1].equals(Integer.toString(pos) + "**")) {
                    buttonRight.setText("вперёд");
                    buttonRight.setBackground(getResources().getDrawable(R.drawable.button_slyle_stoke_blue));
                    i--;
                    textNovella.setText(lines[i]);
                }
                else {
                    i--;
                    pos--;
                }
            });
            //String lines[] = readFile(partNovel.replaceAll("[^A-Za-z]","")).split("\n");


            /*for ( i = 1; i < lines.length; i++) {
                if (lines[i].equals(Integer.toString(position) + "**")) {
                    buttonRight.setOnClickListener(v -> {
                        if (i + 1 < lines.length && !lines[i + 1].equals(Integer.toString(position + 1) + "**")) {
                            i++;
                            textNovella.setText(lines[i]);
                        }
                    });
                    position++;
                }
            }

            for ( i = 1; i < lines.length; i++) {
                if ((lines[i].equals(Integer.toString(position + 1) + "**") || i + 1 == lines.length) && position > 0) {
                    if (i + 1 == lines.length) {
                        buttonLeft.setOnClickListener(v -> {
                            if (i > 1 && !lines[i].equals(Integer.toString(position) + "**")) {
                                textNovella.setText(lines[i]);
                                i--;
                            }
                        });

                    }
                    else {
                        buttonLeft.setOnClickListener(v -> {
                            if (i - 1 > 1 && !lines[i - 1].equals(Integer.toString(position) + "**"));{
                                i--;
                                textNovella.setText(lines[i]);
                            }
                        });
                    }
                    position--;
                    i--;
                }
            }
*/
/*            count = 1;
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
            });*/
        }

    }

    private String[] readFile(String tag) {
        String path = getExternalFilesDir(null).getAbsolutePath() + "/questTexts/";

        ArrayList<String> bufferArray = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path + tag.replaceAll("[^A-Za-z]","") + ".txt"));
            String line;
            while((line = br.readLine()) != null){
                bufferArray.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //String lines[] = readFile(quest.getTagButtonOne(partNovel.replaceAll("[^A-Za-z]",""))).split("\n");
        return bufferArray.toArray(new String[bufferArray.size()]);
    }
}
