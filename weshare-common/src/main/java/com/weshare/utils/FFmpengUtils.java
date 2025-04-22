package com.weshare.utils;

import com.weshare.config.AppConfig;
import com.weshare.entity.constants.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FFmpengUtils {
    @Resource
    private AppConfig appConfig;

    public void createImageThumbnail(String filePath){
        String CMD = "ffmpeg -i \"%s\" -vf scale=200:-1 \"%s\"";
        CMD = String.format(CMD,filePath,filePath+ Constants.IMAGE_THUMBNAIL_SUFFIX);
        ProcessUtils.executeCommand(CMD,appConfig.getShowFFmpegLog());


    }
}
