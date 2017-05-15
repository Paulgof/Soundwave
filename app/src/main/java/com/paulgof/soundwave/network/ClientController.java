package com.paulgof.soundwave.network;

import com.paulgof.soundwave.OnlineMode;
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


    public ClientController(OnlineMode onlineMode) {
        mOnlineMode = onlineMode;
    }


    public void sendString(String message) {
        if (message.equals(mOnlineMode.connectCom)) {
            isConnect = true;
        } else if (message.equals(mOnlineMode.existCom)) {

            if (isExistTrans) {
                mPlayerOnline.prepareAudio(mOnlineMode.audioList, position);
                mOnlineMode.audioStream.sendMessage(mOnlineMode.readyCom);
            } else {
                isExistTrans = true;
            }

        } else if (message.equals(mOnlineMode.notExistCom)) {

        } else if (message.equals(mOnlineMode.readyCom)) {

            if (isReady) {
                mPlayerOnline.startAudio();
            } else {
                isReady = true;
            }

        } else if (message.equals(mOnlineMode.audioStartCom)) {

            mPlayerOnline.startAudio();

        } else if (message.equals(mOnlineMode.audioStopCom)) {

            mPlayerOnline.pauseAudio();

        } else {
            isExistTrans = false;
            isReady = false;
            for (int i = 0; i < mOnlineMode.audioList.size(); i++) {
                if (mOnlineMode.audioList.get(i).getTitle().equals(message)) {
                    isExist = true;
                    position = i;
                    break;
                }
            }
            if (isExist) {
                mOnlineMode.audioStream.sendMessage(mOnlineMode.existCom);
                isExist = false;
            }
        }
    }
    

}
