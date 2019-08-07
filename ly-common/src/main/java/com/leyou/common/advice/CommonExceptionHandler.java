package com.leyou.common.advice;

import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 通用异常处理  J进行捕获
 * @author hhr
 */
@ControllerAdvice
public class CommonExceptionHandler {

    /**运行时异常*/
    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handlerException(LyException e){

        return  ResponseEntity.status(e.getExceptionEnum().getCode())
                    .body(new ExceptionResult(e.getExceptionEnum()));
    }
}
