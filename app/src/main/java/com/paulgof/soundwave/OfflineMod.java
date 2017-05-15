package com.paulgof.soundwave;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.paulgof.soundwave.controller.AudioAdapter;
import com.paulgof.soundwave.controller.AudioController;
import com.paulgof.soundwave.controller.AudioDistributor;
import com.paulgof.soundwave.model.Audio;

import java.util.ArrayList;

/**
  Created by Paulgof on 5/13/2017.
 */

public class OfflineMod extends AppCompatActivity {

     public ArrayList<Audio> audioList;
    AudioDistributor audioDistributor;
    AudioController audioController;
    ContentResolver contentResolver;

    View audioControl;
    TextView firstName;
    TextView secondName;
    Button prePlay;
    Button mainPlay;
    Button postPlay;

    int flagPosition = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_mode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contentResolver = getContentResolver();
        audioDistributor = new AudioDistributor();
        audioList = audioDistributor.loadAudio(contentResolver);
        audioController = new AudioController(this);

        intoAudioList();
        intoControlView();
    }

    private void intoAudioList() {
        if (audioList.size() > 0) {

            AudioAdapter audioAdapter = new AudioAdapter(this, audioList);
            ListView listView = (ListView) findViewById(R.id.off_line);
            listView.setAdapter(audioAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    audioController.changePosition(position);
                    audioControl.setVisibility(View.VISIBLE);

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.offline_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                audioController.stopper();
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void intoControlView() {
        audioControl = findViewById(R.id.audio_control);
        firstName = (TextView) findViewById(R.id.firstName);
        secondName = (TextView) findViewById(R.id.secondName);
        firstName.setSelected(true);
        prePlay = (Button) findViewById(R.id.preButton);
        prePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioController.changePosition(audioController.getPosition()-1);
            }
        });
        mainPlay = (Button) findViewById(R.id.mainButton);
        mainPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioController.changeStatus(!audioController.isStatus());
                if(audioController.isStatus()){
                    mainPlay.setBackgroundResource(R.drawable.pause);
                } else {
                    mainPlay.setBackgroundResource(R.drawable.play_button);
                }
            }
        });
        postPlay = (Button) findViewById(R.id.postButton);
        postPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioController.changePosition(audioController.getPosition()+1);
            }
        });

    }

    public void changePosition() {
        firstName.setText(audioController.getAudioList()
                .get(audioController.getPosition()).getTitle());
        secondName.setText(audioController.getAudioList()
                .get(audioController.getPosition()).getArtist());

    }

    public void changeStatus() {
        if (audioController.isStatus()) {
            mainPlay.setBackgroundResource(R.drawable.pause);
        } else {
            mainPlay.setBackgroundResource(R.drawable.play_button);
        }
    }

    @Override
    protected void onDestroy() {
        audioController.stopper();
        super.onDestroy();
    }
}
