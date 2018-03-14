package com.jfhealthcare.common.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
@Data
public class WarnningSopInfo  implements Serializable{
	private static final long serialVersionUID = 5352323L;
	private List<String> referencedSOPClassUID;
	private List<String> referencedSOPInstanceUID;
	private List<String> retrieveURL;
	private String originalAttributesSequence;
	private String warnningReason;
}
