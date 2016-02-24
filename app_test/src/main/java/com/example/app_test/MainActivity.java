/*
package com.example.app_test;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.duanqu.qupai.android.app.QupaiServiceImpl;
import com.duanqu.qupai.engine.session.MovieExportOptions;
import com.duanqu.qupai.engine.session.VideoSessionCreateInfo;
import com.duanqu.qupai.minisdk.common.Contant;
import com.duanqu.qupai.minisdk.common.FileUtils;
import com.duanqu.qupai.recorder.EditorCreateInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecordActivity();
            }
        });
    }

    private void startRecordActivity() {
        //美颜参数:1-100.这里不设指定为80,这个值只在第一次设置，之后在录制界面滑动美颜参数之后系统会记住上一次滑动的状态
        int beautySkinProgress = 100;

        */
/**
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

        */
/**
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
                .setCaptureHeight(getResources().getDimension(com.duanqu.qupai.minisdk.R.dimen.qupai_recorder_capture_height_size))
                .setBeautySkinViewOn(true)
                .setFlashLightOn(true)
                .setTimelineTimeIndicator(true)
                .build();

        EditorCreateInfo _CreateInfo = new EditorCreateInfo();
        _CreateInfo.setSessionCreateInfo(create_info);
        _CreateInfo.setNextIntent(null);
        _CreateInfo.setOutputThumbnailSize(480, 480);//输出图片宽高
        String videoPath = FileUtils.newOutgoingFilePath(this);
        _CreateInfo.setOutputVideoPath(videoPath);//输出视频路径
        _CreateInfo.setOutputThumbnailPath(videoPath + ".jpg");//输出图片路径

        QupaiServiceImpl qupaiService = new QupaiServiceImpl.Builder()
                .setEditorCreateInfo(_CreateInfo).build();
        qupaiService.showRecordPage(this, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
*/
