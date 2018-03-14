package com.jfhealthcare.common.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class UploadFileEntity implements Serializable {
	private static final long serialVersionUID = 620033658740613124L;
	long from = 0;
	long size = 0;
	long to = 0;
	long total = 0;
	String hash = "";
	String userId = "";
	String fragment_signature = "";
}
