package com.duanqu.qupai.minisdk.common;


public class Contant {

    /**
     * 默认最大时长
     */
    public static int DEFAULT_DURATION_MAX_LIMIT = 10;

    public static int DEFAULT_DURATION_LIMIT_MIN = 3;
    /**
     * 默认码率
     */
    public static int DEFAULT_BITRATE = 800 * 1024;
    /**
     * 默认CRF参数
     */
    public static int DEFAULT_VIDEO_RATE_CRF = 6;

    /**
     * VideoPreset
     */
    public static String DEFAULT_VIDEO_Preset = "faster";
    /**
     * VideoLevel
     */
    public static int DEFAULT_VIDEO_LEVEL = 30;

    /**
     * VideoTune
     */
    public static String DEFAULT_VIDEO_TUNE = "zerolatency";
    /**
     * movflags_KEY
     */
    public static String DEFAULT_VIDEO_MOV_FLAGS_KEY = "movflags";

    /**
     * movflags_VALUE
     */
    public static String DEFAULT_VIDEO_MOV_FLAGS_VALUE = "+faststart";

    /**
     * 默认Video保存路径，请开发者自行指定
     */
    public static String VIDEOPATH;

    public static String THUMBNAILPATH = VIDEOPATH + ".jpg";
    /**
     * 水印本地路径，文件必须为rgba格式的PNG图片
     */
    public static String WATER_MARK_PATH = "assets://Qupai/watermark/qupai-logo.png";
    /**
     * 水印位置1为右上，2为右下
     */
    public static int WATER_MARK_POSITION = 1;

    public static String appkey = "204a673404151f6";
    public static String appsecret = "75c87b4a5abf4e55b53258b14b6cca3f";
    public static String tags = "tags";
    public static String description = "description";
    public static int shareType = 1; //是否公开 0公开分享 1私有(default) 公开类视频不需要AccessToken授权

    public static String accessToken;//accessToken 通过调用授权接口得到
    public static String space = "workspace"; //存储目录 建议使用uid cid之类的信息
    public static String domain = "http://sdk.s.qupai.me";//当前TEST应用的域名。该地址每个应用都不同

}
