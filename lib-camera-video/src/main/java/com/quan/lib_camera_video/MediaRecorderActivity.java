package com.quan.lib_camera_video;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quan.lib_camera_video.camera.MediaRecorderBase;
import com.quan.lib_camera_video.camera.MediaRecorderNative;
import com.quan.lib_camera_video.camera.VCamera;
import com.quan.lib_camera_video.camera.model.MediaObject;
import com.quan.lib_camera_video.camera.util.ConvertToUtils;
import com.quan.lib_camera_video.camera.util.DeviceUtils;
import com.quan.lib_camera_video.camera.util.FileUtils;
import com.quan.lib_camera_video.camera.util.StringUtils;
import com.quan.lib_camera_video.custom.ProgressView;
import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;

/**
 * 视频录制
 *
 * @author tangjun@yixia.com
 */
public class MediaRecorderActivity extends AppCompatActivity implements
        MediaRecorderBase.OnErrorListener, MediaRecorderBase.OnPreparedListener,
        MediaRecorderBase.OnEncodeListener {

    /**
     * 录制最长时间
     */
    public final static int RECORD_TIME_MAX = 8 * 1000;
    /**
     * 录制最小时间
     */
    public final static int RECORD_TIME_MIN = 2 * 1000;
    /**
     * 刷新进度条
     */
    private static final int HANDLE_INVALIDATE_PROGRESS = 0;
    /**
     * 转码进度
     */
    private static final int HANDLER_ENCODING_PROGRESS = 101;
    /**
     * 转码结束
     */
    private static final int HANDLER_ENCODING_END = 102;
    /**
     * 超时
     */
    private static final int HANDLER_RUNTIME = 103;
    /**
     * 延迟拍摄停止
     */
    private static final int HANDLE_STOP_RECORD = 1;
    /**
     * 对焦
     */
    private static final int HANDLE_HIDE_RECORD_FOCUS = 2;

    /**
     * 前后摄像头切换
     */
    private MenuItem mCameraSwitch;
    /**
     * 闪光灯
     */
    private MenuItem mRecordLed;
    /**
     * 拍摄按钮
     */
    private TextView mRecordController;

    /**
     * 底部条
     */
    private RelativeLayout mBottomLayout;
    /**
     * 摄像头数据显示画布
     */
    private SurfaceView mSurfaceView;
    /**
     * 录制进度
     */
    private ProgressView mProgressView;
    /**
     * 对焦动画
     */
    private Animation mFocusAnimation;

    /**
     * SDK视频录制对象
     */
    private MediaRecorderBase mMediaRecorder;
    /**
     * 视频信息
     */
    private MediaObject mMediaObject;

    /**
     * 需要重新编译（拍摄新的或者回删）
     */
    private boolean mRebuild;
    /**
     * on
     */
    private boolean mCreated;
    /**
     * 是否是点击状态
     */
    private volatile boolean mPressedStatus;
    /**
     * 是否已经释放
     */
    private volatile boolean mReleased;
    /**
     * 对焦图片宽度
     */
    private int mFocusWidth;
    /**
     * 底部背景色
     */
    private int mBackgroundColorNormal, mBackgroundColorPress;
    /**
     * 屏幕宽度
     */
    private int mWindowWidth;
    public static final String APKCardPath = Environment.getExternalStorageDirectory().getPath() + "/ls/";//SD卡地址
    public static final String APKCardPathChatVideo = APKCardPath + "chat/video/";//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCreated = false;
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
        VCamera.setVideoCachePath(APKCardPathChatVideo);
        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(true);
        // 初始化拍摄SDK，必须
        VCamera.initialize(this);

        loadIntent();
        loadViews();
        mCreated = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recoder_menu, menu);
        mCameraSwitch = menu.findItem(R.id.c_switch);
        mRecordLed = menu.findItem(R.id.led);

        // ~~~ 设置数据

        // 是否支持前置摄像头
        if (!MediaRecorderBase.isSupportFrontCamera()) {
            mCameraSwitch.setVisible(false);
        }
        // 是否支持闪光灯
        if (!DeviceUtils.isSupportCameraLedFlash(getPackageManager())) {
            mRecordLed.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.c_switch) {
            if (mRecordLed.isChecked()) {
                if (mMediaRecorder != null) {
                    mMediaRecorder.toggleFlashMode();
                }
                mRecordLed.setChecked(false);
            }

            if (mMediaRecorder != null) {
                mMediaRecorder.switchCamera();
            }

            if (mMediaRecorder.isFrontCamera()) {
                mRecordLed.setEnabled(false);
            } else {
                mRecordLed.setEnabled(true);
            }
        } else if (i == android.R.id.home) {
            onBackPressed();
        } else if (i == R.id.led) {
            if (mMediaRecorder != null) {
                if (mMediaRecorder.isFrontCamera()) {
                    return true;
                }
            }

            if (mMediaRecorder != null) {
                mMediaRecorder.toggleFlashMode();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 加载传入的参数
     */
    private void loadIntent() {
        mWindowWidth = DeviceUtils.getScreenWidth(this);

        mFocusWidth = ConvertToUtils.dipToPX(this, 64);
        mBackgroundColorNormal = getResources().getColor(R.color.black);// camera_bottom_bg
        mBackgroundColorPress = getResources().getColor(
                R.color.camera_bottom_press_bg);
    }

    /**
     * 加载视图
     */
    private void loadViews() {
        setContentView(R.layout.activity_media_recorder);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("小视频");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        // ~~~ 绑定控件
        mSurfaceView = (SurfaceView) findViewById(R.id.record_preview);
        mProgressView = (ProgressView) findViewById(R.id.record_progress);

        mProgressView.setListener(new ProgressView.RuntimeListener() {
            @Override
            public void runtime() {
                stopRecord();
            }
        });

        mRecordController = (TextView) findViewById(R.id.record_controller);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);

        mBottomLayout.setOnTouchListener(mOnVideoControllerTouchListener);


        mProgressView.setMaxDuration(RECORD_TIME_MAX);

        initSurfaceView();
    }

    /**
     * 初始化拍摄SDK
     */
    private void initMediaRecorder() {
        mMediaRecorder = new MediaRecorderNative(this);
        mRebuild = true;

        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setOnEncodeListener(this);
        File f = new File(VCamera.getVideoCachePath());
        if (!FileUtils.checkFile(f)) {
            f.mkdirs();
        }
        String key = String.valueOf(System.currentTimeMillis());
        mMediaObject = mMediaRecorder.setOutputDirectory(key,
                VCamera.getVideoCachePath() + key);
        mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
        mMediaRecorder.prepare();
    }

    /** 初始化画布 */
    private void initSurfaceView() {
        final int w = DeviceUtils.getScreenWidth(this);
        ((RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams()).topMargin = w;
        ((RelativeLayout.LayoutParams) mProgressView.getLayoutParams()).topMargin = w;

        int width = w;
        int height = w * 4 / 3;
        //
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSurfaceView
                .getLayoutParams();
        lp.width = width;
        lp.height = height;
        mSurfaceView.setLayoutParams(lp);
    }

    /**
     * 点击屏幕录制
     */
    private View.OnTouchListener mOnVideoControllerTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mMediaRecorder == null) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 检测是否手动对焦
                    // 判断是否已经超时
                    if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                        return true;
                    }

                    startRecord();
                    break;

                case MotionEvent.ACTION_UP:
                    // 暂停
                    stopRecord();
                    break;
            }
            return true;
        }

    };

    @Override
    public void onResume() {
        super.onResume();
        UtilityAdapter.freeFilterParser();
        UtilityAdapter.initFilterParser();

        if (mMediaRecorder == null) {
            initMediaRecorder();
        } else {
            mRecordLed.setChecked(false);
            mMediaRecorder.prepare();
            mProgressView.setData(mMediaObject);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRecord();
        UtilityAdapter.freeFilterParser();
        if (!mReleased) {
            if (mMediaRecorder != null)
                mMediaRecorder.release();
        }
        mReleased = false;
    }

    /**
     * 开始录制
     */
    private void startRecord() {
        if (mMediaRecorder != null) {
            MediaObject.MediaPart part = mMediaRecorder.startRecord();
            if (part == null) {
                return;
            }
            mProgressView.setData(mMediaObject);
        }

        mRebuild = true;
        mPressedStatus = true;
        mRecordController.setSelected(true);
        mBottomLayout.setBackgroundColor(mBackgroundColorPress);

        if (mHandler != null) {
            mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
            mHandler.sendEmptyMessage(HANDLE_INVALIDATE_PROGRESS);

            mHandler.removeMessages(HANDLE_STOP_RECORD);
            mHandler.sendEmptyMessageDelayed(HANDLE_STOP_RECORD,
                    RECORD_TIME_MAX - mMediaObject.getDuration());
        }
        mCameraSwitch.setEnabled(false);
        mRecordLed.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        if (mMediaObject != null && mMediaObject.getDuration() > 1) {
            // 未转码
            new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage(R.string.record_camera_exit_dialog_message)
                    .setNegativeButton(
                            R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    mMediaObject.delete();
                                    finish();
                                    overridePendingTransition(
                                            R.anim.push_bottom_in,
                                            R.anim.push_bottom_out);
                                }

                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no,
                            null).setCancelable(false).show();
            return;
        }

        if (mMediaObject != null)
            mMediaObject.delete();
        finish();
    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        if (mPressedStatus) {
            mPressedStatus = false;
            mRecordController.setSelected(false);
            mBottomLayout.setBackgroundColor(mBackgroundColorNormal);

            if (mMediaRecorder != null) {
                mMediaRecorder.stopRecord();
            }

            mCameraSwitch.setEnabled(true);
            mRecordLed.setEnabled(true);

            mHandler.removeMessages(HANDLE_STOP_RECORD);
            checkStatus();

            mMediaRecorder.startEncoding();
        }
    }

    /**
     * 检查录制时间，显示/隐藏下一步按钮
     */
    private int checkStatus() {
        int duration = 0;
        if (!isFinishing() && mMediaObject != null) {
            duration = mMediaObject.getDuration();
            if (duration < RECORD_TIME_MIN) {
                if (duration == 0) {
                    mCameraSwitch.setVisible(true);
                }
            }
        }
        return duration;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_INVALIDATE_PROGRESS:
                    if (mMediaRecorder != null && !isFinishing()) {
                        if (mProgressView != null)
                            mProgressView.invalidate();
                        // if (mPressedStatus)
                        // titleText.setText(String.format("%.1f",
                        // mMediaRecorder.getDuration() / 1000F));
                        if (mPressedStatus)
                            sendEmptyMessageDelayed(0, 30);
                    }
                    break;
                case HANDLER_ENCODING_PROGRESS:// 读取进度
                    int progress = UtilityAdapter
                            .FilterParserInfo(UtilityAdapter.FILTERINFO_PROGRESS);
                    if (mProgressDialog != null) {
                        mProgressDialog.setMessage(getString(
                                R.string.record_preview_encoding_format, progress));
                    }
                    if (progress < 100)
                        sendEmptyMessageDelayed(HANDLER_ENCODING_PROGRESS, 200);
                    else {
                        sendEmptyMessage(HANDLER_ENCODING_END);
                    }
                    break;
                case HANDLER_ENCODING_END:
                    hideProgress();
                    finish();
                    break;
            }
        }
    };


    @Override
    public void onEncodeStart() {
        showProgress("", getString(R.string.record_camera_progress_message));
    }

    @Override
    public void onEncodeProgress(int progress) {
    }

    /**
     * 转码完成
     */
    @Override
    public void onEncodeComplete() {
        hideProgress();
        Intent intent = new Intent();
        intent.putExtra("file", mMediaObject.getOutputVideoPath());
        intent.putExtra("thumb", mMediaObject.getOutputVideoThumbPath());
        setResult(RESULT_OK, intent);
        finish();
        //录制完成
    }

    /**
     * 转码失败 检查sdcard是否可用，检查分块是否存在
     */
    @Override
    public void onEncodeError() {
        hideProgress();
        Toast.makeText(this, R.string.record_video_transcoding_faild,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVideoError(int what, int extra) {

    }

    @Override
    public void onAudioError(int what, String message) {

    }

    @Override
    public void onPrepared() {

    }

    protected ProgressDialog mProgressDialog;

    public ProgressDialog showProgress(String title, String message) {
        return showProgress(title, message, -1);
    }

    public ProgressDialog showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(this, theme);
            else
                mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!StringUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
        return mProgressDialog;
    }

    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        hideProgress();
        mProgressDialog = null;
    }
}
