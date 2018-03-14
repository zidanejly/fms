package com.jfhealthcare.common.validator;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.jfhealthcare.common.exception.FmsException;

import java.util.Set;

public class ValidatorUtils {
    private static Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 校验对象
     * @param object        待校验对象
     * @param groups        待校验的组
     * @throws FmsException  校验不通过，则报FmsException异常
     */
    public static void validateEntity(Object object, Class<?>... groups)
            throws FmsException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            for(ConstraintViolation<Object> constraint:  constraintViolations){
                msg.append(constraint.getMessage()).append(" ");
            }
            throw new FmsException(msg.toString());
        }
    }
}
