package com.weshare.admin.controller;

import com.weshare.config.AppConfig;
import com.weshare.entity.constants.Constants;
import com.weshare.entity.enums.DateTimePatternEnum;
import com.weshare.entity.enums.ResponseCodeEnum;
import com.weshare.entity.po.CategoryInfo;
import com.weshare.entity.query.CategoryInfoQuery;
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
import java.util.List;

@RestController
@RequestMapping("/file")
@Validated
@Slf4j
public class FileController extends ABaseController{
    @Resource
    private AppConfig appConfig;
    @Resource
    FFmpengUtils fFmpengUtils;

    @RequestMapping("/uploadImage")
    public ResponseVO uploadImage(@NotNull MultipartFile file,
                                   @NotNull Boolean createThumbnail) throws IOException {
        String month = DateUtil.format(new Date(), DateTimePatternEnum.YYYYMM.getPattern());
        String folder = appConfig.getProjectFolder()+ Constants.FILE_FOLDER+Constants.FILE_COVER+month;
        File folderFile = new File(folder);
        if(!folderFile.exists()){
            folderFile.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String fileSuffix = StringTools.getFileSuffix(fileName);
        String realFileName = StringTools.getRandomString(Constants.LENGTH_30)+fileSuffix;
        String filePath = folder + "/"+ realFileName;
        file.transferTo(new File(filePath));
        if(createThumbnail){
            //生成缩略图
            fFmpengUtils.createImageThumbnail(filePath);
        }
        return getSuccessResponseVO(Constants.FILE_COVER+month+"/"+realFileName);
    }
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
