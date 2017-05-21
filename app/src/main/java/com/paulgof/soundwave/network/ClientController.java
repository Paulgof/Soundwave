package com.paulgof.soundwave.network;

import android.os.Handler;
import android.view.View;

import com.paulgof.soundwave.OnlineMode;
import com.paulgof.soundwave.R;
import com.paulgof.soundwave.controller.Utilities;
import com.paulgof.soundwave.model.AudioPlayerOnline;

/**
  Created by Paulgof on 5/15/2017.
 */

public class ClientController {

    private OnlineMode mOnlineMode;

    AudioPlayerOnline mPlayerOnline = new AudioPlayerOnline();

    boolean isConnect = false;
    boolean isExist = false;
    boolean isExistTrans = false;
    boolean isReady = false;

    int position;

    public String theTitle;

    private Handler mHandler = new Handler();
    private Utilities utils;

    public ClientController(OnlineMode onlineMode) {
        mOnlineMode = onlineMode;
        utils = new Utilities();
    }


    public void sendString(String message) {
        if (message.equals(mOnlineMode.connectCom)) {
            isConnect = true;
            mOnlineMode.connectButton.setVisibility(View.GONE);
        } else if (message.equals(mOnlineMode.existCom)) {

            if (isExistTrans) {
                mPlayerOnline.prepareAudio(mOnlineMode.audioList, position);
                mPlayerOnline.startAudio();
                mPlayerOnline.pauseAudio();//for better synchronization
                mOnlineMode.audioControl.setVisibility(View.VISIBLE);
                mOnlineMode.firstName.setText(mOnlineMode.audioList.get(position).getTitle());
                mOnlineMode.secondName.setText(mOnlineMode.audioList.get(position).getArtist());
                mOnlineMode.audioStream.sendMessage(mOnlineMode.readyCom);
            } else {
                isExistTrans = true;
            }

        } else if (message.equals(mOnlineMode.notExistCom)) {

            mOnlineMode.notExistReaction(theTitle);

        } else if (message.equals(mOnlineMode.readyCom)) {

            if (isReady) {
                mPlayerOnline.startAudio();
                updateProgressBar();
            } else {
                isReady = true;
            }

        } else if (message.equals(mOnlineMode.audioStartCom)) {

            mPlayerOnline.startAudio();
            mOnlineMode.flagPlayed = true;
            mOnlineMode.mainPlay.setBackgroundResource(R.drawable.pause);

        } else if (message.equals(mOnlineMode.audioStopCom)) {

            mPlayerOnline.pauseAudio();
            mOnlineMode.flagPlayed = false;
            mOnlineMode.mainPlay.setBackgroundResource(R.drawable.play_button);

        } else {
            theTitle = message;
            isExistTrans = false;
            isReady = false;
            mHandler.removeCallbacks(mUpdateTimeTask);
            killMP();
            mOnlineMode.flagPlayed = true;
            mOnlineMode.mainPlay.setBackgroundResource(R.drawable.pause);
            mOnlineMode.audioControl.setVisibility(View.GONE);
            for (int i = 0; i < mOnlineMode.audioList.size(); i++) {
                if (mOnlineMode.audioList.get(i).getTitle().equals(message)) {
                    isExist = true;
                    //theTitle = mOnlineMode.audioList.get(i).getTitle();
                    position = i;
                    break;
                }
            }
            if (isExist) {
                mOnlineMode.audioStream.sendMessage(mOnlineMode.existCom);
                isExist = false;
            } else {
                mOnlineMode.audioStream.sendMessage(mOnlineMode.notExistCom);
            }
        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() { //TODO
        public void run() {
            long totalDuration = mPlayerOnline.mMediaPlayer.getDuration();
            long currentDuration = mPlayerOnline.mMediaPlayer.getCurrentPosition();
            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            mOnlineMode.mProgressBar.setProgress(progress);
            // Displaying Total Duration time
            mOnlineMode.totalTime.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            mOnlineMode.currentTime.setText("" + utils.milliSecondsToTimer(currentDuration));
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    public void killMP() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        mPlayerOnline.releaseMP();
    }


}
