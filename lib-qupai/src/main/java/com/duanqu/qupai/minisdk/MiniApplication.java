package com.duanqu.qupai.minisdk;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import com.duanqu.qupai.auth.AuthService;
import com.duanqu.qupai.auth.QupaiAuthListener;
import com.duanqu.qupai.engine.session.MovieExportOptions;
import com.duanqu.qupai.engine.session.ProjectOptions;
import com.duanqu.qupai.engine.session.UISettings;
import com.duanqu.qupai.engine.session.VideoSessionCreateInfo;
import com.duanqu.qupai.minisdk.common.Contant;
import com.duanqu.qupai.sdk.android.QupaiManager;
import com.duanqu.qupai.sdk.android.QupaiService;

public class MiniApplication {

    private static final String AUTHTAG = "QupaiAuth";

    private Context context;

    private static MiniApplication instance;

    public MiniApplication(Context context) {
        this.context = context;
    }

    public static synchronized MiniApplication getInstance(Context context) {
        if (instance == null) {
            instance = new MiniApplication(context.getApplicationContext());
        }
        return instance;
    }

    public void startRecordActivity(Activity context) {
        //美颜参数:1-100.这里不设指定为80,这个值只在第一次设置，之后在录制界面滑动美颜参数之后系统会记住上一次滑动的状态
        int beautySkinProgress = 100;
        initAuth(context.getApplicationContext(),"23239015","b82bd9e75ac3a71840063202126c5162",Contant.space);
        /**
         * 压缩参数，可以自由调节
         */
        MovieExportOptions movie_options = new MovieExportOptions.Builder()
                .setVideoProfile("high")
                .setVideoBitrate(Contant.DEFAULT_BITRATE)
                .setVideoPreset(Contant.DEFAULT_VIDEO_Preset).setVideoRateCRF(Contant.DEFAULT_VIDEO_RATE_CRF)
                .setOutputVideoLevel(Contant.DEFAULT_VIDEO_LEVEL)
                .setOutputVideoTune(Contant.DEFAULT_VIDEO_TUNE)
                .configureMuxer(Contant.DEFAULT_VIDEO_MOV_FLAGS_KEY, Contant.DEFAULT_VIDEO_MOV_FLAGS_VALUE)
                .build();

        //输出视频的参数
        ProjectOptions projectOptions = new ProjectOptions.Builder() //输出视频宽高目前只能设置1：1的宽高，建议设置480*480.
                .setVideoSize(480, 480) //帧率 .setVideoFrameRate(30)
                //时长区间
                .setDurationRange(Contant.DEFAULT_DURATION_LIMIT_MIN, Contant.DEFAULT_DURATION_MAX_LIMIT).get();

        /**
         * 界面参数
         */
        VideoSessionCreateInfo create_info = new VideoSessionCreateInfo.Builder()
                .setMovieExportOptions(movie_options)
                .setWaterMarkPath(Contant.WATER_MARK_PATH)
                .setWaterMarkPosition(1)
                .setBeautyProgress(beautySkinProgress)
                .setBeautySkinOn(true)
                .setCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT)
                .setCaptureHeight(context.getResources().getDimension(com.duanqu.qupai.minisdk.R.dimen.qupai_recorder_capture_height_size))
                .setTimelineTimeIndicator(true)
                .build();

        //UI设置参数
        UISettings _UISettings = new UISettings() {
            @Override
            public boolean hasEditor() {
                return false;//是否需要编辑功能
            }

            @Override
            public boolean hasImporter() {
                return false;//是否需要导入功能
            }

            @Override
            public boolean hasGuide() {
                return false;//是否启动引导功能，建议用户第一次使用时设置为true
            }

            @Override
            public boolean hasSkinBeautifer() {
                return true;//是否显示美颜图标
            }
        };
        QupaiService qupaiService = QupaiManager.getQupaiService(context);

        qupaiService.initRecord(create_info, projectOptions, _UISettings);

        qupaiService.showRecordPage(context, 10001, false);

    }

    /**
     * 鉴权 建议只调用一次,在Application调用。在demo里面为了测试调用了多次 得到accessToken
     * @param context
     * @param appKey    appkey
     * @param appsecret appsecret
     * @param space     space
     */
    private void initAuth(Context context ,String appKey,String appsecret,String space){
        AuthService service = AuthService.getInstance();
        service.setQupaiAuthListener(new QupaiAuthListener() {
            @Override
            public void onAuthError(int errorCode, String message) {
                Log.e(AUTHTAG, "ErrorCode" + errorCode + "message" + message);
            }

            @Override
            public void onAuthComplte(int responseCode, String responseMessage) {
                Contant.accessToken = responseMessage;
            }
        });
        service.startAuth(context,appKey, appsecret, space);
    }
}
