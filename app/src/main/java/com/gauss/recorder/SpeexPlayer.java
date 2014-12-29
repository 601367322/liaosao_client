/**
 *
 */
package com.gauss.recorder;

import android.media.MediaPlayer.OnCompletionListener;

import com.gauss.speex.encode.SpeexDecoder;

import java.io.File;

/**
 * @author Gauss
 */
public class SpeexPlayer {
    private String fileName = null;
    private SpeexDecoder speexdec = null;
    private Thread th;

    public SpeexPlayer(String fileName) {

        this.fileName = fileName;
        System.out.println(this.fileName);
        try {
            speexdec = new SpeexDecoder(new File(this.fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        speexdec.setOnCompletionListener(listener);
    }

    public void startPlay() {
        RecordPlayThread rpt = new RecordPlayThread();

        th = new Thread(rpt);
        th.start();
    }

    public void stopPlay() {
        if (th != null) {
            th.interrupt();
        }
    }

    boolean isPlay = true;

    class RecordPlayThread extends Thread {

        public void run() {
            try {
                if (speexdec != null)
                    speexdec.decode();

            } catch (Exception t) {
                t.printStackTrace();
            }
        }
    }

    ;
}
