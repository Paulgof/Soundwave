package com.paulgof.soundwave.controller;

import com.paulgof.soundwave.OfflineMode;
import com.paulgof.soundwave.model.Audio;
import com.paulgof.soundwave.model.AudioPlayer;

import java.util.ArrayList;

/**
  Created by Paulgof on 5/7/2017.
 */

public class AudioController { // core transaction

    OfflineMode mOfflineMode;

    AudioPlayer mAudioPlayer = new AudioPlayer(this);

    private static ArrayList<Audio> sAudioList;
    private static int sPosition = -1;
    private static boolean sStatus;

    

    public AudioController(OfflineMode offlineMode) {
        mOfflineMode = offlineMode;
        setAudioList(offlineMode.audioList);
    }

    public void changeAudioList(ArrayList<Audio> arrayList) {
        setAudioList(arrayList);
    }

    public void changePosition(int position) {
        if(position == sPosition) {
            changeStatus(!sStatus);
        } else if(position < 0) {
            changePosition(sAudioList.size()-1);
        }  else if(position == sAudioList.size()) {
            changePosition(0);
        } else  {
            setPosition(position);
            mAudioPlayer.playAudio();
            changeStatus(true);
        }
    }

    public void changeStatus(boolean status) {
        setStatus(status);
        mAudioPlayer.audioSwitch();
        mOfflineMode.changeStatus();
    }

    private void setPosition(int position) {
        sPosition = position;
        mOfflineMode.changePosition();
    }


    private void setStatus(boolean status) {
        sStatus = status;
    }

    private void setAudioList(ArrayList<Audio> audioList) {
        sAudioList = audioList;
    }

    public int getPosition() {
        return sPosition;
    }

    public boolean isStatus() {
        return sStatus;
    }

    public ArrayList<Audio> getAudioList() {
        return sAudioList;
    }

    public void stopper() {
        mAudioPlayer.releaseMP();
    }
}
