package com.xuecheng.framework.domain.ucenter.response;

import com.xuecheng.framework.model.Response.ResponseResult;
import com.xuecheng.framework.model.Response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by mrt on 2018/5/21.
 */
@Data
@ToString
@NoArgsConstructor
public class LoginResult extends ResponseResult {
    public LoginResult(ResultCode resultCode,String token) {
        super(resultCode);
        this.token = token;
    }
    private String token;
}
