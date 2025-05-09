package com.weshare.entity.constants;

public class Constants {
    public static final Integer ONE =1;
    public static final Integer ZERO=0;

    public static final Integer LENGTH_10 =10;
    public static final Integer LENGTH_15 =15;

    public static final Integer LENGTH_30 =30;
    public static final Integer MB_SIZE = 1024*1024;

    //由字母、数字、特殊字符，任意2种组成，8-18位
    public static final String REGEX_PASSWORD ="^(?![a-zA-Z]+$)(?!\\d+$)(?![^\\da-zA-Z\\s]+$).{8,18}$";
    public static final  Integer REDIS_KEY_EXPIRES_ONE_MINUTE =60000;
    public static final  Integer REDIS_KEY_EXPIRES_ONE_DAY =REDIS_KEY_EXPIRES_ONE_MINUTE*60*24;
    public static final  Integer TIME_SECONDS_DAY =REDIS_KEY_EXPIRES_ONE_DAY/1000;
    public static final  Integer REDIS_KEY_EXPIRES_ONE_WEEK =REDIS_KEY_EXPIRES_ONE_DAY*7;
    public static final  Integer TIME_SECONDS_WEEK =REDIS_KEY_EXPIRES_ONE_WEEK/1000;

    public static final String FILE_FOLDER ="file/";
    public static final String FILE_COVER ="cover/";
    public static final String FILE_VIDEO ="video/";
    public static final String FILE_FOLDER_TEMP ="temp/";


    public  static  final String REDIS_KEY_PREFIX="weshare:";
    public  static final String REDIS_KEY_CHECK_CODE =REDIS_KEY_PREFIX+"checkcode:";
    public  static final String REDIS_KEY_TOKEN_WEB =REDIS_KEY_PREFIX+"token:web:";
    public  static final String REDIS_KEY_TOKEN_ADMIN =REDIS_KEY_PREFIX+"token:admin:";
    public  static final String TOKEN_WEB = "token";
    public  static final String TOKEN_ADMIN = "adminToken";
    public  static final String REDIS_KEY_CATEGORY_LIST = "category:list:";
    public static final String IMAGE_THUMBNAIL_SUFFIX = "_thumbnail.jpg";
    public static final String REDIS_KEY_UPLOADING_FILE = REDIS_KEY_PREFIX+"uploading:";
    public static final String REDIS_KEY_SYS_SETTING = REDIS_KEY_PREFIX +"sysSetting:";

}
