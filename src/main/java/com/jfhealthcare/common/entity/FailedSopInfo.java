package com.jfhealthcare.common.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class FailedSopInfo  implements Serializable{
	private static final long serialVersionUID = 5362354L;
	private List<String> referencedSOPClassUID;
	private List<String> referencedSOPInstanceUID;
	private String failureReason;
}
