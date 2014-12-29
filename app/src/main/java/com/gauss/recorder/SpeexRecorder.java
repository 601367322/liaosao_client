package com.gauss.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.gauss.speex.encode.SpeexEncoder;
import com.xl.util.LogUtil;

import java.io.File;

public class SpeexRecorder implements Runnable {

    private static final String TAG = "SpeexRecorder";

    private volatile boolean isRecording;
    private final Object mutex = new Object();
    private static final int frequency = 8000;
    private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    public static int packagesize = 160;
    private String fileName = null;
    private MicRealTimeListenerSpeex mtl;
    private long milltime;

    public SpeexRecorder(String fileName, MicRealTimeListenerSpeex mtl) {
        super();
        this.fileName = fileName;
        this.mtl = mtl;
    }

    public void run() {
        AudioRecord recordInstance=null;
        try {
            SpeexEncoder encoder = new SpeexEncoder(this.fileName);
            Thread encodeThread = new Thread(encoder);
            encoder.setRecording(true);
            encodeThread.start();
            milltime = System.currentTimeMillis();
            synchronized (mutex) {
                while (!this.isRecording) {
                    try {
                        mutex.wait();
                    } catch (InterruptedException e) {
                        throw new IllegalStateException("Wait() interrupted!", e);
                    }
                }
            }
            android.os.Process
                    .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            int bufferRead = 0;
            int bufferSize = AudioRecord.getMinBufferSize(frequency,
                    AudioFormat.CHANNEL_IN_MONO, audioEncoding);

            short[] tempBuffer = new short[packagesize];

            recordInstance = new AudioRecord(
                    MediaRecorder.AudioSource.MIC, frequency,
                    AudioFormat.CHANNEL_IN_MONO, audioEncoding, bufferSize);

            recordInstance.startRecording();

            while (this.isRecording) {
                LogUtil.d(TAG, "start to recording.........");
                bufferRead = recordInstance.read(tempBuffer, 0, packagesize);
                // bufferRead = recordInstance.read(tempBuffer, 0, 320);
                if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
                    throw new IllegalStateException(
                            "read() returned AudioRecord.ERROR_INVALID_OPERATION");
                } else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
                    throw new IllegalStateException(
                            "read() returned AudioRecord.ERROR_BAD_VALUE");
                } else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
                    throw new IllegalStateException(
                            "read() returned AudioRecord.ERROR_INVALID_OPERATION");
                }
                LogUtil.d(TAG, "put data into encoder collector....");

                int v = 0;
                for (int i = 0; i < tempBuffer.length; i++) {
                    v += tempBuffer[i] * tempBuffer[i];
                }
    //			Log.i("spl", String.valueOf(v / (float) bufferRead));
                int value = (int) (Math.abs((int) (v / (float) bufferRead) / 10000) >> 1);
    //			double dB = 10*Math.log10((double)(v/(float)bufferRead));
                mtl.getMicRealTimeSize(value);
                encoder.putData(tempBuffer, bufferRead);

            }
            recordInstance.stop();
            recordInstance.release();
            recordInstance = null;
            // tell encoder to stop.
            encoder.setRecording(false);

            if (System.currentTimeMillis() - milltime < 1000) {
                File file = new File(fileName);
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(recordInstance!=null&&recordInstance.getState()==AudioRecord.STATE_INITIALIZED) {
                recordInstance.stop();
                recordInstance.release();
                recordInstance = null;
            }
        }
    }

    public void setRecording(boolean isRecording) {
        synchronized (mutex) {
            this.isRecording = isRecording;
            if (this.isRecording) {
                mutex.notify();
            }
        }
    }

    public boolean isRecording() {
        synchronized (mutex) {
            return isRecording;
        }
    }
}
