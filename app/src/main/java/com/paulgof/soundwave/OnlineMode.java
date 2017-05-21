package com.paulgof.soundwave;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.paulgof.soundwave.controller.AudioAdapter;
import com.paulgof.soundwave.controller.AudioDistributor;
import com.paulgof.soundwave.model.Audio;
import com.paulgof.soundwave.network.AudioStream;
import com.paulgof.soundwave.network.ClientController;
import com.paulgof.soundwave.network.NsdHelper;

import java.util.ArrayList;

/**
  Created by Paulgof on 5/13/2017.
 */

public class OnlineMode extends AppCompatActivity {

    NsdHelper nsdHelper;
    Handler updateHandler;
    public AudioStream audioStream;
    ClientController mClientController;

    public ArrayList<Audio> audioList;
    AudioDistributor audioDistributor;
    ContentResolver contentResolver;

    public Button connectButton;

    //Commands
    public final String connectCom = "/!Connecting!/";
    public final String existCom = "/!Exist!/";
    public final String notExistCom = "/!notExist!/";
    public final String readyCom = "/!readyCom!/";
    public final String audioStartCom = "/!Start!/";
    public final String audioStopCom = "/!Stop!/";

    public int flagPosition = -1;
    public boolean flagPlayed = false;

    public View audioControl;
    public TextView firstName;
    public TextView secondName;
    public Button mainPlay;
    public Button postPlay;
    public Button prePlay;
    public ProgressBar mProgressBar;
    public TextView currentTime;
    public TextView totalTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_mode);

        //Add classes for network
        updateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String chatLine = msg.getData().getString("msg");
                addChatLine(chatLine);
            }
        };
        audioStream = new AudioStream(updateHandler);
        nsdHelper = new NsdHelper(this);
        nsdHelper.initializeNsd();
        nsdHelper.registerService(audioStream.getLocalPort());//registration

        //Add audio at AudioList
        contentResolver = getContentResolver();
        audioDistributor = new AudioDistributor();
        audioList = audioDistributor.loadAudio(contentResolver);
        intoAudioList();

        connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NsdServiceInfo service = nsdHelper.getChosenServiceInfo();
                if (service != null) {
                    audioStream.connectToServer(service.getHost(),
                            service.getPort());
                    audioStream.sendMessage(connectCom);
                }
            }
        });

        mClientController = new ClientController(this);

        makeControlView();
    }

    private void intoAudioList() {
        if (audioList.size() > 0) {

            AudioAdapter audioAdapter = new AudioAdapter(this, audioList);
            ListView listView = (ListView) findViewById(R.id.on_list);
            listView.setAdapter(audioAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position == flagPosition && flagPlayed) {
                        flagPlayed = false;
                        audioStream.sendMessage(audioStopCom);
                    } else if (position == flagPosition && !flagPlayed) {
                        flagPlayed = true;
                        audioStream.sendMessage(audioStartCom);
                    } else {
                        flagPosition = position;
                        String audioTitle = audioList.get(position).getTitle();
                        audioStream.sendMessage(audioTitle);
                        flagPlayed = true;
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.online_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addChatLine(String line) { //
        mClientController.sendString(line);
    }

    @Override
    protected void onPause() {
        if (nsdHelper != null) {
            nsdHelper.stopDiscovery();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nsdHelper != null) {
            nsdHelper.discoverServices();
        }
    }

    @Override
    protected void onDestroy() {
        nsdHelper.tearDown();
        mClientController.killMP();
        try {
            audioStream.tearDown();
        } catch (Exception e) {}
        super.onDestroy();
    }

    public void makeToast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void makeControlView() {
        audioControl = findViewById(R.id.audio_control);
        firstName = (TextView) findViewById(R.id.firstName);
        secondName = (TextView) findViewById(R.id.secondName);
        firstName.setSelected(true);
        mainPlay = (Button) findViewById(R.id.mainButton);
        mainPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!flagPlayed){
                    mainPlay.setBackgroundResource(R.drawable.pause);
                    flagPlayed = true;
                    audioStream.sendMessage(audioStartCom);
                } else {
                    mainPlay.setBackgroundResource(R.drawable.play_button);
                    flagPlayed = false;
                    audioStream.sendMessage(audioStopCom);
                }
            }
        });
        postPlay = (Button) findViewById(R.id.postButton);
        postPlay.setVisibility(View.GONE);
        prePlay = (Button) findViewById(R.id.preButton);
        prePlay.setVisibility(View.GONE);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        currentTime = (TextView) findViewById(R.id.currentTime);
        totalTime = (TextView) findViewById(R.id.totalTime);
    }

    public void notExistReaction(String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineMode.this);
        builder.setTitle("Аудио отсутсвует!")
                .setMessage("Упс, на одном из устройств отсутсвует " +
                        title + ". Пожалуйста, выберете другое аудио или передайте " +
                        "отсутсвуещее аудио на устройство.")
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
