package com.weshare.web.controller;

import com.weshare.component.RedisComponent;
import com.weshare.config.AppConfig;
import com.weshare.entity.constants.Constants;
import com.weshare.entity.dto.SysSettingDto;
import com.weshare.entity.dto.TokenUserInfoDto;
import com.weshare.entity.dto.UploadingFileDto;
import com.weshare.entity.enums.DateTimePatternEnum;
import com.weshare.entity.enums.ResponseCodeEnum;
import com.weshare.entity.vo.ResponseVO;
import com.weshare.exception.BusinessException;
import com.weshare.utils.DateUtil;
import com.weshare.utils.FFmpengUtils;
import com.weshare.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
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
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private FFmpengUtils fFmpengUtils;

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
    @RequestMapping("/preUploadVideo")
    public ResponseVO preUploadVideo(@NotEmpty String fileName,@NotNull Integer chunks)  {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        String uploadId = redisComponent.savePreVideoFileInfo(tokenUserInfoDto.getUserId(),fileName,chunks);
        return getSuccessResponseVO(uploadId);
    }
    @RequestMapping("/uploadVideo")
    public ResponseVO UploadVideo(@NotNull MultipartFile chunkFile,@NotNull Integer chunkIndex,@NotEmpty String uploadId) throws IOException {

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UploadingFileDto fileDto = redisComponent.getUploadVideoFile(tokenUserInfoDto.getUserId(),uploadId);
        if(fileDto==null){
            throw new BusinessException("文件不存在，请重新上传");
        }
        SysSettingDto sysSettingDto = redisComponent.getSysSettingDto();

        if(fileDto.getFileSize()>sysSettingDto.getVideoSize() * Constants.MB_SIZE){
            throw new BusinessException("文件超过大小限制");
        }

        if((chunkIndex-1)> fileDto.getChunkIndex() || chunkIndex >fileDto.getChunks()-1){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        String folder = appConfig.getProjectFolder()+Constants.FILE_FOLDER+Constants.FILE_FOLDER_TEMP+fileDto.getFilePath();
        File targetFile = new File(folder+"/"+chunkIndex);
        chunkFile.transferTo(targetFile);
        fileDto.setChunkIndex(chunkIndex);
        fileDto.setFileSize(fileDto.getFileSize()+chunkFile.getSize());

        redisComponent.updateVideoFileInfo(tokenUserInfoDto.getUserId(), fileDto);
        return getSuccessResponseVO(null);
    }
    @RequestMapping("/delUploadVideo")
    public ResponseVO delUploadVideo(@NotEmpty String uploadId) throws IOException {

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UploadingFileDto fileDto = redisComponent.getUploadVideoFile(tokenUserInfoDto.getUserId(),uploadId);
        if(fileDto ==null){
            throw new BusinessException("文件不存在，请重新上传");
        }
        redisComponent.delVideoFileInfo(tokenUserInfoDto.getUserId(),uploadId);
        FileUtils.deleteDirectory(new File(appConfig.getProjectFolder()+Constants.FILE_FOLDER+Constants.FILE_FOLDER_TEMP+fileDto.getFilePath()));
        return getSuccessResponseVO(uploadId);
    }
    @RequestMapping("/UploadImage")
    public ResponseVO UploadImage(@NotNull MultipartFile file,@NotNull Boolean createThumbnail ) throws IOException {
        String day = DateUtil.format(new Date(),DateTimePatternEnum.YYYYMMDD.getPattern());
        String folder = appConfig.getProjectFolder()+Constants.FILE_FOLDER+Constants.FILE_COVER+day;
        File folderFile = new File(folder);
        if(!folderFile.exists()){
            folderFile.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String fileSuffix = StringTools.getFileSuffix(fileName);
        String realFileName = StringTools.getRandomString(Constants.LENGTH_30)+fileSuffix;
        String filePath = folder +"/"+realFileName;
        file.transferTo(new File(filePath));
        if(createThumbnail!=null &&createThumbnail){
            fFmpengUtils.createImageThumbnail(filePath);
        }

        return getSuccessResponseVO(Constants.FILE_COVER+day+"/"+realFileName);
    }
}
