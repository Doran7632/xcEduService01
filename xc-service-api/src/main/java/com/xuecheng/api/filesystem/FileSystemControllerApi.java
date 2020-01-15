package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(value="上传文件接口",description = "上传文件")
public interface FileSystemControllerApi {

    //上传文件
    @ApiOperation("上传文件接口")
    public UploadFileResult uploadFile(MultipartFile multipartFile, String businessKey, String filetag, String metadata);


}
