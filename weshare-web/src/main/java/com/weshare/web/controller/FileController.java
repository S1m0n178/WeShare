package com.weshare.web.controller;

import com.weshare.config.AppConfig;
import com.weshare.entity.constants.Constants;
import com.weshare.entity.enums.DateTimePatternEnum;
import com.weshare.entity.enums.ResponseCodeEnum;
import com.weshare.entity.vo.ResponseVO;
import com.weshare.exception.BusinessException;
import com.weshare.utils.DateUtil;
import com.weshare.utils.FFmpengUtils;
import com.weshare.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

@RestController
@RequestMapping("/file")
@Validated
@Slf4j
public class FileController extends ABaseController{
    @Resource
    private AppConfig appConfig;

    @RequestMapping("/getResource")
    public void getResource(HttpServletResponse response,@NotNull String sourceName) throws IOException {
       if(!StringTools.pathIsOk(sourceName)){
           throw  new BusinessException((ResponseCodeEnum.CODE_600));
       }
       String suffix = StringTools.getFileSuffix(sourceName);
       response.setContentType("image/"+suffix.replace(".",""));
       response.setHeader("Cache-Control","max-age=2592000");
       readFile(response,sourceName);
    }
    protected void readFile(HttpServletResponse response,String filePath){
        File file = new File(appConfig.getProjectFolder()+Constants.FILE_FOLDER+filePath);
        if(!file.exists()){
            return;
        }
        try(OutputStream out = response.getOutputStream(); FileInputStream in = new FileInputStream(file)){
            byte[] byteData = new byte[1024];
            int len = 0;
            while((len = in.read(byteData))!=-1){
                out.write(byteData,0,len);
            }
            out.flush();
        }catch (Exception e){
            log.error("读取文件异常",e);
        }
    }
}
