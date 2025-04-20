package com.weshare.entity.constants;

public class Constants {
    public static final Integer ONE =1;
    public static final Integer ZERO=0;

    public static final Integer LENGTH_10 =10;
    //由字母、数字、特殊字符，任意2种组成，8-18位
    public static final String REGEX_PASSWORD ="^(?![a-zA-Z]+$)(?!\\d+$)(?![^\\da-zA-Z\\s]+$).{8,18}$";
    public static final  Integer REDIS_KEY_EXPIRES_ONE_MINUTE =60000;
    public  static  final String REDIS_KEY_PREFIX="weshare:";
    public  static  String REDIS_KEY_CHECK_CODE =REDIS_KEY_PREFIX+"checkcode:";
}
