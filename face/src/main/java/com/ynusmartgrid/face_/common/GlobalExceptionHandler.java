package com.ynusmartgrid.face_.common;


import com.ynusmartgrid.face_.constant.Constant;
import com.ynusmartgrid.face_.pojo.CommonObjReturn;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;


/**
 * Created by wjs on 2022/03/12
 * 全局请求异常拦截器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =  LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = ConstraintViolationException.class)
    public CommonObjReturn<?> handle1(ConstraintViolationException ex){
            StringBuilder msg = new StringBuilder();
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            PathImpl pathImpl = (PathImpl) constraintViolation.getPropertyPath();
            String paramName = pathImpl.getLeafNode().getName();
            String message = constraintViolation.getMessage();
            msg.append("[").append(message).append("]");
        }
        logger.error(msg.toString(),ex);
        // 注意：Response类必须有get和set方法，不然会报错
        return new CommonObjReturn<Object>(null,false, msg.toString(), Constant.RS_FIELD_INVALID_RECODE);
    }

    @ExceptionHandler(value = Exception.class)
    public CommonObjReturn<?> handle1(Exception ex){
        logger.error(ex.getMessage(),ex);
        return new CommonObjReturn<Object>(null,false,ex.getMessage(),Constant.RS_FUNCTION_EXCEPTION);

    }
}