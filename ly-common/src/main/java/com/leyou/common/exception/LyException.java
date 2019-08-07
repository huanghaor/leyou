package com.leyou.common.exception;

import com.leyou.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 实际生产环境自定义异常
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LyException extends  RuntimeException{

    private ExceptionEnum exceptionEnum;


}
