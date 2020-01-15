package com.xuecheng.framework.exception;


import com.xuecheng.framework.model.Response.ResultCode;
import lombok.Data;

/**
 *  自定义异常
 *  继承RuntimeException是为了不对代码有侵入性，因为如果继承Exception的话，方法上必须声明或者try/catch
 */
@Data
public class CustomException extends RuntimeException {

    private ResultCode resultCode;

    public CustomException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }


}
