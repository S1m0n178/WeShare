package com.weshare.web.controller;

import com.weshare.component.RedisComponent;
import com.weshare.entity.constants.Constants;
import com.weshare.entity.dto.TokenUserInfoDto;
import com.weshare.entity.dto.UploadingFileDto;
import com.weshare.entity.vo.ResponseVO;
import com.weshare.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/sysSetting")
@Validated
@Slf4j
public class SysSettingController extends ABaseController{
    @Resource
    private RedisComponent redisComponent;

    @RequestMapping("/getSetting")
    public ResponseVO getSetting(){
        return getSuccessResponseVO(redisComponent.getSysSettingDto());
    }
}
