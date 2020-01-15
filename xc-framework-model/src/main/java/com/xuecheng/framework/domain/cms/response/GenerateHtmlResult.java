package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.Response.ResponseResult;
import com.xuecheng.framework.model.Response.ResultCode;
import lombok.Data;

/**
 * Created by mrt on 2018/3/31.
 */
@Data
public class GenerateHtmlResult extends ResponseResult {
    String html;
    public GenerateHtmlResult(ResultCode resultCode, String html) {
        super(resultCode);
        this.html = html;
    }
}
