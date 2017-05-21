package com.paulgof.soundwave.model;

import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.paulgof.soundwave.controller.AudioController;

import java.util.ArrayList;

public class AudioPlayer {

    public MediaPlayer mMediaPlayer;

    AudioController mAudioController;

    public AudioPlayer(AudioController audioController) {
        mAudioController = audioController;
    }

    public void playAudio() {
        try {
            releaseMP();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mAudioController.getAudioList().
                    get(mAudioController.getPosition()).getData());
            mMediaPlayer.prepare();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mAudioController.changePosition((mAudioController.getPosition())+1);
                }
            });
        } catch (Exception e) {

        }
    }

    public void releaseMP() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
                mMediaPlayer = null;
            } catch (Exception e) {}
        }
    }

    public void audioSwitch() {
        if (mAudioController.isStatus()) {
            mMediaPlayer.start();
        } else {
            mMediaPlayer.pause();
        }
    }
}
