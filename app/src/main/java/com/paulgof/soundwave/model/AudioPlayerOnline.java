package com.paulgof.soundwave.model;

import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;

/**
  Created by Paulgof on 5/15/2017.
 */

public class AudioPlayerOnline {

    public MediaPlayer mMediaPlayer;

    public void prepareAudio(ArrayList<Audio> audios, int position) {
        try {
            releaseMP();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(audios.get(position).getData());
            mMediaPlayer.prepare();
        } catch (IOException ioe) {}
    }

    public void startAudio() {
        mMediaPlayer.start();
    }

    public void pauseAudio() {
        mMediaPlayer.pause();
    }

    public void releaseMP() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
                mMediaPlayer = null;
            } catch (Exception e) {}
        }
    }



}
