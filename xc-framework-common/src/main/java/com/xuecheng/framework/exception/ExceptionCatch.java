package com.xuecheng.framework.exception;


import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.Response.CommonCode;
import com.xuecheng.framework.model.Response.ResponseResult;
import com.xuecheng.framework.model.Response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *  同意异常捕获类
 *
 */
@ControllerAdvice //控制器增强
public class ExceptionCatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    private static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCETPIONS;
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();

    static {
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVAILD_PARAM);
    }

    /**
     * 当有这个注解的时候，会捕获一切注解中声明的异常
     * 并且在方法赋值到方法形参中，
     *
     * 比如，service中有处抛出异常，这里就会捕获到这个异常，然后返回ResponseReuslt，
     * ResponseResult中有errorCode，errorMessage等的信息,
     * 注意加 @ResponseBody
     *
     * @param customException
     * @return
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException customException){

        LOGGER.error("catch Exception:{}",customException.getMessage());
        ResultCode resultCode = customException.getResultCode();
        return new ResponseResult(resultCode);
    }


    /**
     * 捕获不可预知异常,
     *  将比方说 HttpMessageNotReadableException 这个异常出现的时候，给指定的异常信息（在枚举类中列举），
     *  其他情况给定 “系统繁忙”的异常信息
     *  步骤
     *       1：设定map（设定的map是谷歌的一个map，一旦添加就不可以操作，属于只读的情况），
     *          在静态代码块中将枚举类中的错误信息，添加进map
     *       2：在下方方法中判断，如果map中有信息，并且符合枚举类中的某个，
     *          那么就返回，没有就返回一致的 “系统繁忙”
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception exception){

        LOGGER.error("catch Exception:{}",exception.getMessage());
        if(EXCETPIONS == null){
            EXCETPIONS = builder.build();//map需要构建
        }
        ResultCode resultCode = EXCETPIONS.get(exception.getClass());
        if(resultCode != null){
            return new ResponseResult(resultCode);
        }else{
            return new ResponseResult(CommonCode.SERVER_ERROR);
        }
    }
}
