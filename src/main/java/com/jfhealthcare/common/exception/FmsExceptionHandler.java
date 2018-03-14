package com.jfhealthcare.common.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jfhealthcare.common.base.BaseResponse;
import com.jfhealthcare.common.enums.FileUploadResponseCodeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * 异常处理器
 */
@Slf4j
@RestControllerAdvice
public class FmsExceptionHandler {
	/**
	 * 自定义异常
	 */
	@ExceptionHandler(FmsException.class)
	public BaseResponse handleException(FmsException e) {
		log.error(e.getMessage(), e);
		return BaseResponse.getFailResponse(e.getCode(), e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public BaseResponse handleException(Exception e) {
		log.error("代码中未补获的系统异常", e);
		return BaseResponse.getFailResponse(FileUploadResponseCodeEnum.ERROR_CODE_500.getCode(), e.getMessage());
	}
}
