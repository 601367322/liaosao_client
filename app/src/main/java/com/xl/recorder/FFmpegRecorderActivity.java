package com.xl.recorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.util.AnimUtil;
import com.xl.util.LogUtil;
import com.xl.util.StaticFactory;
import com.xl.util.ToastUtil;
import com.xl.util.Utils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * 视频参数设置在RecorderParameters类里面
 */
@OptionsMenu(R.menu.video_menu)
@EActivity(R.layout.activity_recorder)
public class FFmpegRecorderActivity extends BaseBackActivity implements MasterLayout.OnCompleteListener {

    private final static String CLASS_LABEL = "RecordActivity";
    private final static String LOG_TAG = CLASS_LABEL;

    private PowerManager.WakeLock mWakeLock;
    //视频文件的存放地址
    private String strVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "rec_video.mp4";
    //视频文件对象
    private File fileVideoPath = null;
    //判断是否需要录制，手指按下继续，抬起时暂停
    boolean recording = false;
    //是否开启闪光灯
    boolean isFlashOn = false;

    //录制视频和保存音频的类
    private volatile MediaRecorder videoRecorder;

    //判断是否是前置摄像头
    private boolean isPreviewOn = false;
    private Camera mCamera;

    //预览的宽高和屏幕宽高
    private int previewWidth = 480, screenWidth = 480;
    private int previewHeight = 480, screenHeight = 800;

    //摄像头以及它的参数
    private Camera cameraDevice;
    private CameraView cameraView;
    Parameters cameraParameters = null;

    //分别为 默认摄像头（后置）、默认调用摄像头的分辨率、被选择的摄像头（前置或者后置）
    int defaultCameraId = -1, defaultScreenResolution = -1, cameraSelection = 0;

    //包含显示摄像头数据的surfaceView
    RelativeLayout topLayout = null;

    //视频帧率
    private int frameRate = 30;
    //录制的最长时间
    private int recordingTime = 15000;
    //已录制时间
    private int totalRecodingTime = 0;
    //录制的最短时间
    private int recordingMinimumTime = 3000;

    private byte[] bufferByte;

    @OptionsMenuItem(R.id.light)
    MenuItem light_menu;
    @OptionsMenuItem(R.id.camera)
    MenuItem camera_menu;

    @ViewById(R.id.start_btn)
    public MasterLayout startBtn;

    @ViewById(R.id.reset_btn)
    public MasterLayout resetBtn;

    @ViewById(R.id.play_btn)
    public MasterLayout playBtn;

    @ViewById(R.id.right_view)
    public View rightView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            camera_menu.setVisible(true);
        } else {
            camera_menu.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }


    private boolean initSuccess = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
        mWakeLock.acquire();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //Find screen dimensions
        screenWidth = displaymetrics.widthPixels;
        screenHeight = displaymetrics.heightPixels;


    }

    @Override
    protected void init() {
        startBtn.setListener(this);
        playBtn.setPlay();
        resetBtn.setClear();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!initSuccess)
            return false;
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onResume() {
        super.onResume();

        initLayout();

        if (mWakeLock == null) {
            //获取唤醒锁,保持屏幕常亮
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
            mWakeLock.acquire();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        recording = false;

        releaseResources();

        if (cameraView != null) {
            cameraView.stopPreview();
            if (cameraDevice != null) {
                cameraDevice.setPreviewCallback(null);
                cameraDevice.release();
            }
            cameraDevice = null;
            ((ViewGroup) cameraView.getParent()).removeAllViews();
        }
        mCamera = null;
        cameraView = null;
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initLayout() {

//        startBtn.set
        initCameraLayout();
    }

    private void initCameraLayout() {
        new AsyncTask<String, Integer, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                boolean result = setCamera();

                if (!initSuccess) {

                    initVideoRecorder();

                    initSuccess = true;
                }

                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (!result || cameraDevice == null) {
                    //FuncCore.showToast(FFmpegRecorderActivity.this, "无法连接到相机");
                    finish();
                    return;
                }

                topLayout = (RelativeLayout) findViewById(R.id.recorder_surface_parent);
                if (topLayout != null && topLayout.getChildCount() > 0)
                    topLayout.removeAllViews();

                cameraView = new CameraView(FFmpegRecorderActivity.this, cameraDevice);

                handleSurfaceChanged();

                float scale = (float) previewHeight / (float) screenWidth;
                int height = (int) ((float) previewWidth / scale);

                //设置surface的宽高
                RelativeLayout.LayoutParams layoutParam1 = new RelativeLayout.LayoutParams(screenWidth, height);
                layoutParam1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

                RelativeLayout.LayoutParams layoutParam2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                layoutParam2.topMargin = screenWidth;

                View view = new View(FFmpegRecorderActivity.this);
                view.setFocusable(false);
                view.setBackgroundColor(Color.BLACK);
                view.setFocusableInTouchMode(false);

                topLayout.addView(cameraView, layoutParam1);
                topLayout.addView(view, layoutParam2);

                if (light_menu != null) {
                    if (cameraSelection == CameraInfo.CAMERA_FACING_FRONT)
                        light_menu.setVisible(false);
                    else
                        light_menu.setVisible(true);
                }
            }

        }.execute("start");
    }

    private boolean setCamera() {
        try {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                int numberOfCameras = Camera.getNumberOfCameras();

                CameraInfo cameraInfo = new CameraInfo();
                for (int i = 0; i < numberOfCameras; i++) {
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == cameraSelection) {
                        defaultCameraId = i;
                    }
                }
            }
            stopPreview();
            if (mCamera != null)
                mCamera.release();

            if (defaultCameraId >= 0)
                cameraDevice = Camera.open(defaultCameraId);
            else
                cameraDevice = Camera.open();

        } catch (Exception e) {
            return false;
        }
        return true;
    }


    private void initVideoRecorder() {
        if (fileVideoPath == null) {
            File file = new File(StaticFactory.APKCardPathChat);
            if (!file.exists()) {
                file.mkdirs();
            }
            strVideoPath = StaticFactory.APKCardPathChat + String.valueOf((String
                    .valueOf(new Date().getTime()) + ".mp4")
                    .hashCode());

            fileVideoPath = new File(strVideoPath);

            if (fileVideoPath.exists()) {
                fileVideoPath.delete();
            }
        }
    }

    public void startRecording() {
        try {
            videoRecorder = new MediaRecorder();
            videoRecorder.reset();
            mCamera.unlock();
            videoRecorder.setCamera(mCamera);
            videoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源
            videoRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频源
            videoRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 视频输出格式
            videoRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);// 音频格式
            videoRecorder.setVideoSize(previewWidth, previewHeight);// 设置分辨率：
            videoRecorder.setVideoFrameRate(frameRate);// 这个我把它去掉了，感觉没什么用
            videoRecorder.setVideoEncodingBitRate(1000000);// 设置帧频率，然后就清晰了
            if (cameraSelection == 1) {
                videoRecorder.setOrientationHint(270);// 前置摄像头输出旋转270度，保持竖屏录制
            } else {
                videoRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
            }
            videoRecorder.setMaxDuration(recordingTime + 5 * 1000);
            videoRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);// 视频录制格式
            videoRecorder.setPreviewDisplay(cameraView.mHolder.getSurface());
            videoRecorder.setOutputFile(strVideoPath);
            videoRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    LogUtil.d("onError");
                }
            });
            videoRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    LogUtil.d("onInfo");
                }
            });
            videoRecorder.prepare();
            try {
                videoRecorder.start();
                recording = true;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                animateStartBtn();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        if (fileVideoPath.exists()) {
            Utils.showDialog(this, R.drawable.dialog_icon, R.string.kiding, R.string.no_save_radio, R.string.exit, R.string.cancle_send_btn, null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    videoTheEnd(false);
                    finish();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }, null, null, null);
        } else {
            finish();
        }
    }

    @Override
    public void homeClick() {
        onBackPressed();
    }

    /**
     * 显示摄像头的内容，以及返回摄像头的每一帧数据
     *
     * @author QD
     */
    class CameraView extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;


        public CameraView(Context context, Camera camera) {
            super(context);
            try {
                mCamera = camera;
                cameraParameters = mCamera.getParameters();
                mHolder = getHolder();
                mHolder.addCallback(CameraView.this);
                mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }


        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (mCamera != null) {
                try {
                    stopPreview();
                    mCamera.setPreviewDisplay(holder);
                } catch (Exception exception) {
                    mCamera.release();
                    mCamera = null;
                }
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (isPreviewOn)
                mCamera.stopPreview();
            handleSurfaceChanged();
            startPreview();
            mCamera.autoFocus(null);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            try {
                mHolder.addCallback(null);
                mCamera.setPreviewCallback(null);

            } catch (RuntimeException e) {
            }
        }

        public void startPreview() {
            if (!isPreviewOn && mCamera != null) {
                isPreviewOn = true;
                mCamera.startPreview();
            }
        }

        public void stopPreview() {
            if (isPreviewOn && mCamera != null) {
                isPreviewOn = false;
                mCamera.stopPreview();
            }
        }
    }

    /**
     * 关闭摄像头的预览
     */
    public void stopPreview() {
        if (isPreviewOn && mCamera != null) {
            isPreviewOn = false;
            mCamera.stopPreview();

        }
    }

    private void handleSurfaceChanged() {
        if (mCamera == null) {
            //showToast(this, "无法连接到相机");
            finish();
            return;
        }
        //获取摄像头的所有支持的分辨率
        List<Size> resolutionList = Util.getResolutionList(mCamera);
        if (resolutionList != null && resolutionList.size() > 0) {
            Collections.sort(resolutionList, new Util.ResolutionComparator());
            Size previewSize = null;
            if (defaultScreenResolution == -1) {
                boolean hasSize = false;
                //如果摄像头支持640*480，那么强制设为640*480
                for (int i = 0; i < resolutionList.size(); i++) {
                    Size size = resolutionList.get(i);
                    if (size != null && size.width == 640 && size.height == 480) {
                        previewSize = size;
                        hasSize = true;
                        break;
                    }
                }
                //如果不支持设为中间的那个
                if (!hasSize) {
                    int mediumResolution = resolutionList.size() / 2;
                    if (mediumResolution >= resolutionList.size())
                        mediumResolution = resolutionList.size() - 1;
                    previewSize = resolutionList.get(mediumResolution);
                }
            } else {
                if (defaultScreenResolution >= resolutionList.size())
                    defaultScreenResolution = resolutionList.size() - 1;
                previewSize = resolutionList.get(defaultScreenResolution);
            }
            //获取计算过的摄像头分辨率
            if (previewSize != null) {
                previewWidth = previewSize.width;
                previewHeight = previewSize.height;
                cameraParameters.setPreviewSize(previewWidth, previewHeight);
                if (videoRecorder != null) {
                    try {
                        videoRecorder.setVideoSize(previewWidth, previewHeight);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        bufferByte = new byte[previewWidth * previewHeight * 3 / 2];

        mCamera.addCallbackBuffer(bufferByte);

        //设置预览帧率
        cameraParameters.setPreviewFrameRate(frameRate);

        //系统版本为8一下的不支持这种对焦
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            mCamera.setDisplayOrientation(Util.determineDisplayOrientation(FFmpegRecorderActivity.this, defaultCameraId));
            List<String> focusModes = cameraParameters.getSupportedFocusModes();
            if (focusModes != null) {
                Log.i("video", Build.MODEL);
                if (((Build.MODEL.startsWith("GT-I950"))
                        || (Build.MODEL.endsWith("SCH-I959"))
                        || (Build.MODEL.endsWith("MEIZU MX3"))) && focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {

                    cameraParameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                } else if (focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    cameraParameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                } else
                    cameraParameters.setFocusMode(Parameters.FOCUS_MODE_FIXED);
            }
        } else
            mCamera.setDisplayOrientation(90);
        try {
            mCamera.setParameters(cameraParameters);
        }catch (Throwable throwable){
            ToastUtil.toast(this,"无法正确识别摄像头，可能无法正常拍摄。");
        }

    }

    @OptionsItem(R.id.light)
    public void light_click() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            //showToast(this, "不能开启闪光灯");
            return;
        }
        //闪光灯
        if (isFlashOn) {
            isFlashOn = false;
            light_menu.setIcon(R.drawable.ic_icn_flashlight_off);
            cameraParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        } else {
            isFlashOn = true;
            light_menu.setIcon(R.drawable.ic_icn_flashlight_on);
            cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
        }
        mCamera.setParameters(cameraParameters);
    }

    @OptionsItem(R.id.camera)
    public void camera_click() {
        //转换摄像头
        cameraSelection = ((cameraSelection == CameraInfo.CAMERA_FACING_BACK) ? CameraInfo.CAMERA_FACING_FRONT : CameraInfo.CAMERA_FACING_BACK);
        initCameraLayout();

        if (cameraSelection == CameraInfo.CAMERA_FACING_FRONT)
            light_menu.setVisible(false);
        else {
            light_menu.setVisible(true);
            if (isFlashOn) {
                cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(cameraParameters);
            }
        }
    }

    /**
     * 结束录制
     *
     * @param isSuccess
     */
    public void videoTheEnd(boolean isSuccess) {
        releaseResources();
        if (fileVideoPath != null && fileVideoPath.exists() && !isSuccess)
            fileVideoPath.delete();
    }


    /**
     * 保存录制的视频文件
     */
    private void saveRecording() {
        videoTheEnd(true);
    }

    /**
     * 释放资源，停止录制视频和音频
     */
    private void releaseResources() {
        try {
            if (videoRecorder != null) {
                videoRecorder.stop();
                videoRecorder.release();
            }
            recording = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        videoRecorder = null;
    }

    ValueAnimator progress_animator;

    @Click
    public void start_btn() {
        switch (startBtn.flg_frmwrk_mode) {
            case 1:
                if (!recording) {
                    startRecording();
                }
                break;
            case 2:
                if (recording) {
                    if (progress_animator != null) {
                        progress_animator.cancel();
                    }
                    if (totalRecodingTime < recordingMinimumTime) {//太短了
                        videoTheEnd(false);
                        ToastUtil.toast(FFmpegRecorderActivity.this, getString(R.string.your_JJ_so_short), R.drawable.weisuo);
                        startBtn.reset();
                    } else {
                        saveRecording();
                        startBtn.cusview.setupprogress(100);
                    }
                }
                break;
            case 3:
                Intent intent = new Intent();
                intent.putExtra("filename", fileVideoPath.getAbsolutePath());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Click
    public void reset_btn() {
        startBtn.reset();
        videoTheEnd(false);

        AnimatorSet set = new AnimatorSet();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                resetBtn.setVisibility(View.GONE);
                playBtn.setVisibility(View.GONE);
            }
        });
        set.playTogether(ObjectAnimator.ofFloat(resetBtn, AnimUtil.ALPHA, 1f, 0f), ObjectAnimator.ofFloat(playBtn, AnimUtil.ALPHA, 1f, 0f), ObjectAnimator.ofFloat(startBtn, AnimUtil.TRANSLATIONX, 0));
        set.start();
    }

    @Click
    public void play_btn() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "video/mp4";
        Uri uri = Uri.parse("file://" + fileVideoPath.getAbsolutePath());
        intent.setDataAndType(uri, type);
        startActivity(intent);
    }

    public void animateStartBtn() {
        startBtn.animation();
        progress_animator = ObjectAnimator.ofInt(0, 100);
        progress_animator.setDuration(recordingTime);
        progress_animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                totalRecodingTime = (int) animation.getCurrentPlayTime();
                startBtn.cusview.setupprogress((Integer) animation.getAnimatedValue());
            }
        });
        progress_animator.start();
    }

    @Override
    public void complete() {
        resetBtn.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(resetBtn, AnimUtil.ALPHA, 0f, 1f).start();
        ObjectAnimator.ofFloat(playBtn, AnimUtil.ALPHA, 0f, 1f).start();
        ObjectAnimator.ofFloat(startBtn, AnimUtil.TRANSLATIONX, 0, rightView.getX() - startBtn.getX()).start();
    }

}