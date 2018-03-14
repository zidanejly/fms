package com.jfhealthcare.common.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
@Data
public class FileUploadMessage implements Serializable{
	private static final long serialVersionUID = 5352354L;
	private List<SuccessSopInfo> successSopInfoList;
	private List<WarnningSopInfo> warnningSopInfoList;
	private List<FailedSopInfo> failedSopInfoList;
	private List<String> retrieveURL;
	private String otherFailed;
}
