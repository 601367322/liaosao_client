package com.duanqu.qupai.minisdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;

import com.duanqu.qupai.android.app.QupaiDraftManager;
import com.duanqu.qupai.android.app.QupaiServiceImpl;
import com.duanqu.qupai.editor.EditorResult;
import com.duanqu.qupai.engine.session.MovieExportOptions;
import com.duanqu.qupai.engine.session.VideoSessionCreateInfo;
import com.duanqu.qupai.minisdk.common.Contant;
import com.duanqu.qupai.minisdk.common.FileUtils;
import com.duanqu.qupai.recorder.EditorCreateInfo;
import com.duanqu.qupai.upload.AuthService;
import com.duanqu.qupai.upload.QupaiAuthListener;

import java.io.File;

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

       /* *//**
         * 压缩参数，可以自由调节
         *//*
        MovieExportOptions movie_options = new MovieExportOptions.Builder()
                .setVideoProfile("high")
                .setVideoBitrate(Contant.DEFAULT_BITRATE)
                .setVideoPreset(Contant.DEFAULT_VIDEO_Preset).setVideoRateCRF(Contant.DEFAULT_VIDEO_RATE_CRF)
                .setOutputVideoLevel(Contant.DEFAULT_VIDEO_LEVEL)
                .setOutputVideoTune(Contant.DEFAULT_VIDEO_TUNE)
                .configureMuxer(Contant.DEFAULT_VIDEO_MOV_FLAGS_KEY, Contant.DEFAULT_VIDEO_MOV_FLAGS_VALUE)
                .build();

        *//**
         * 界面参数
         *//*
        VideoSessionCreateInfo create_info = new VideoSessionCreateInfo.Builder()
                .setOutputDurationLimit(Contant.DEFAULT_DURATION_MAX_LIMIT)
                .setOutputDurationMin(Contant.DEFAULT_DURATION_LIMIT_MIN)
                .setMovieExportOptions(movie_options)
                .setWaterMarkPath(Contant.WATER_MARK_PATH)
                .setWaterMarkPosition(1)
                .setBeautyProgress(beautySkinProgress)
                .setBeautySkinOn(true)
                .setCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT)
                .setVideoSize(480, 480)
                .setCaptureHeight(context.getResources().getDimension(com.duanqu.qupai.minisdk.R.dimen.qupai_recorder_capture_height_size))
                .setBeautySkinViewOn(true)
                .setFlashLightOn(true)
                .setTimelineTimeIndicator(true)
                .build();

        EditorCreateInfo _CreateInfo = new EditorCreateInfo();
        _CreateInfo.setSessionCreateInfo(create_info);
        _CreateInfo.setNextIntent(null);
        _CreateInfo.setOutputThumbnailSize(480, 480);//输出图片宽高
        String videoPath = FileUtils.newOutgoingFilePath();
        _CreateInfo.setOutputVideoPath(videoPath);//输出视频路径
        _CreateInfo.setOutputThumbnailPath(videoPath + "_thumb");//输出图片路径*/

        initAuth(context,"","",Contant.space);
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

        /**
         * 界面参数
         */
        VideoSessionCreateInfo create_info = new VideoSessionCreateInfo.Builder()
                .setOutputDurationLimit(Contant.DEFAULT_DURATION_MAX_LIMIT)
                .setOutputDurationMin(Contant.DEFAULT_DURATION_LIMIT_MIN)
                .setMovieExportOptions(movie_options)
                .setWaterMarkPath(Contant.WATER_MARK_PATH)
                .setWaterMarkPosition(1)
                .setBeautyProgress(beautySkinProgress)
                .setBeautySkinOn(true)
                .setCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT)
                .setVideoSize(480, 480)
                .setCaptureHeight(context.getResources().getDimension(com.duanqu.qupai.minisdk.R.dimen.qupai_recorder_capture_height_size))
                .setBeautySkinViewOn(true)
                .setFlashLightOn(true)
                .setTimelineTimeIndicator(true)
                .build();

        EditorCreateInfo _CreateInfo = new EditorCreateInfo();
        _CreateInfo.setSessionCreateInfo(create_info);
        _CreateInfo.setNextIntent(null);
        _CreateInfo.setOutputThumbnailSize(480, 480);//输出图片宽高
        String videoPath = FileUtils.newOutgoingFilePath(context);
        _CreateInfo.setOutputVideoPath(videoPath);//输出视频路径
        _CreateInfo.setOutputThumbnailPath(videoPath + ".png");


        QupaiServiceImpl qupaiService = new QupaiServiceImpl.Builder()
                .setEditorCreateInfo(_CreateInfo).build();
        qupaiService.showRecordPage(context, 3);

    }

    public static File[] onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            EditorResult result = new EditorResult(data);
            //得到视频path，和缩略图path的数组，返回十张缩略图,和视频时长
            String videoPath = result.getPath();

            File[] file = new File[2];
            file[0] = new File(videoPath);
            file[1] = new File(videoPath + ".png");

            //删除草稿
            QupaiDraftManager draftManager = new QupaiDraftManager();
            draftManager.deleteDraft(data);

            return file;
        }
        return null;
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
