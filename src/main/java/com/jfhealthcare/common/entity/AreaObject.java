package com.jfhealthcare.common.entity;

import java.util.LinkedList;

import lombok.Data;

@Data
public class AreaObject {
	LinkedList<String> result;
	boolean isNeed = true;
}
