package com.jfhealthcare.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.jfhealthcare.common.entity.FailedSopInfo;
import com.jfhealthcare.common.entity.FileUploadMessage;
import com.jfhealthcare.common.entity.SuccessSopInfo;
import com.jfhealthcare.common.entity.WarnningSopInfo;
import com.jfhealthcare.common.enums.ClientInfoCollectionEnum;
import com.jfhealthcare.common.enums.FileUploadResponseCodeEnum;

@Component
public class DealFileUploadResponseUtil {
	/**
	 * 这个方法用来判断 dicom服务器返回的结果里面(返回的HTTP状态为:200,202,409)是否有错误信息 当200,202中有错误信息的时候
	 * 会返回203 当200,202中没有错误信息的时候 会返回202 当409中的错误原因有 272 42752 - 43007 时,会返回500
	 * ,如果不在上述错误原因时,应该返回203拒收！
	 */
	boolean retry = false;

	public int getResultJsonToFum(String jsonResult, FileUploadMessage fum) {
		jsonResult = StringUtils.isBlank(jsonResult) ? "{}" : jsonResult;
		HashMap joResult = JSONObject.parseObject(jsonResult, HashMap.class);
		List<SuccessSopInfo> successSopInfoList = new ArrayList<SuccessSopInfo>();
		List<WarnningSopInfo> warnningSopInfoList = new ArrayList<WarnningSopInfo>();
		List<FailedSopInfo> failedSopInfoList = new ArrayList<FailedSopInfo>();
		List<String> retrieveURL = new ArrayList<String>();
		// 获取成功信息
		String str99 = "";
		if (joResult.get("00081199") != null) {
			str99 = joResult.get("00081199").toString();
		}
		// 获取失败信息
		String str98 = "";
		if (joResult.get("00081198") != null) {
			str98 = joResult.get("00081198").toString();
		}
		// 获取其他失败信息
		String str9A = "";
		if (joResult.get("0008119A") != null) {
			str9A = joResult.get("0008119A").toString();
		}
		// 获取路径信息
		String str90 = "";
		if (joResult.get("00081190") != null) {
			str90 = joResult.get("00081190").toString();
		}

		if (StringUtils.isNotBlank(str90) && str90.startsWith("{") && str90.endsWith("}")) {
			dealWith90(retrieveURL, str90);
			fum.setRetrieveURL(retrieveURL);
		}
		if (StringUtils.isNotBlank(str99) && str99.startsWith("{") && str99.endsWith("}")) {
			dealWithStr99(successSopInfoList, warnningSopInfoList, str99);
			if (successSopInfoList.size() != 0) {
				fum.setSuccessSopInfoList(successSopInfoList);
			}
			if (warnningSopInfoList.size() != 0) {
				fum.setWarnningSopInfoList(warnningSopInfoList);
			}
		}
		if (StringUtils.isNotBlank(str98) && str98.startsWith("{") && str98.endsWith("}")) {
			dealWithStr98(failedSopInfoList, str98);
			fum.setFailedSopInfoList(failedSopInfoList);
		}
		if (StringUtils.isNotBlank(str9A)) {
			dealWithStr9A(str9A);
			fum.setOtherFailed(str9A);
		}
		if ((fum.getFailedSopInfoList() == null || fum.getFailedSopInfoList().size() == 0)
				&& (fum.getSuccessSopInfoList() == null || fum.getSuccessSopInfoList().size() == 0)) {
			return FileUploadResponseCodeEnum.ERROR_CODE_203.getCode();
		} else if ((fum.getFailedSopInfoList() == null || fum.getFailedSopInfoList().size() == 0)
				&& fum.getSuccessSopInfoList() != null && fum.getSuccessSopInfoList().size() > 0) {
			return FileUploadResponseCodeEnum.ERROR_CODE_202.getCode();
		} else if (fum.getFailedSopInfoList() != null && fum.getFailedSopInfoList().size() > 0 && retry) {
			return FileUploadResponseCodeEnum.ERROR_CODE_500.getCode();
		} else if (fum.getFailedSopInfoList() != null && fum.getFailedSopInfoList().size() > 0 && !retry) {
			return FileUploadResponseCodeEnum.ERROR_CODE_203.getCode();
		} else {
			return FileUploadResponseCodeEnum.ERROR_CODE_202.getCode();
		}
	}

	private void dealWithStr9A(String str9A) {
		HashMap map9A = JSONObject.parseObject(str9A, HashMap.class);
		String map9AValue = "";
		if (map9A.get("Value") != null) {
			map9AValue = map9A.get("Value").toString();
			if(StringUtils.isNotBlank(map9AValue)&&map9AValue.startsWith("[")&&map9AValue.endsWith("]")){
				map9AValue=map9AValue.substring(1,map9AValue.length()-1);
			}
			if (StringUtils.isNotBlank(map9AValue) && StringUtils.isNumeric(map9AValue)) {
				int parseValue = Integer.parseInt(map9AValue);
				if (parseValue == 272 || (42752 <= parseValue && parseValue <= 43007)) {
					retry = true;
				}
			}
		}
	}

	public void dealWith90(List<String> retrieveURL, String str90) {
		HashMap map90 = JSONObject.parseObject(str90, HashMap.class);
		String map90Value = "";
		if (map90.get("Value") != null) {
			map90Value = map90.get("Value").toString();
		}
		if (StringUtils.isNotBlank(map90Value) && map90Value.startsWith("[") && map90Value.endsWith("]")) {
			retrieveURL.add(map90Value);
		}
	}

	public void dealWithStr98(List<FailedSopInfo> failedSopInfoList, String str98) {
		HashMap map98 = JSONObject.parseObject(str98, HashMap.class);
		String map98Value = "";
		if (map98.get("Value") != null) {
			map98Value = map98.get("Value").toString();
		}
		if (StringUtils.isNotBlank(map98Value) && map98Value.startsWith("[") && map98Value.endsWith("]")) {
			ArrayList map98ValueList = JSONObject.parseObject(map98Value, ArrayList.class);
			for (int i = 0; i < map98ValueList.size(); i++) {
				String map98ValueListn = "";
				if (map98ValueList.get(i) != null) {
					map98ValueListn = map98ValueList.get(i).toString();
				}
				if (StringUtils.isNotBlank(map98ValueListn) && map98ValueListn.startsWith("{")
						&& map98ValueListn.endsWith("}")) {
					FailedSopInfo fn = new FailedSopInfo();
					HashMap map98ValueListnMap = JSONObject.parseObject(map98ValueListn, HashMap.class);
					String map98_50 = "";
					if (map98ValueListnMap.get("00081150") != null) {
						map98_50 = map98ValueListnMap.get("00081150").toString();
					}
					String map98_55 = "";
					if (map98ValueListnMap.get("00081155") != null) {
						map98_55 = map98ValueListnMap.get("00081155").toString();
					}
					String map98_97 = "";
					if (map98ValueListnMap.get("00081197") != null) {
						map98_97 = map98ValueListnMap.get("00081197").toString();
					}

					if (StringUtils.isNotBlank(map98_50) && map98_50.startsWith("{") && map98_50.endsWith("}")) {
						HashMap map98_50Map = JSONObject.parseObject(map98_50, HashMap.class);
						String map98_50MapValue = "";
						if (map98_50Map.get("Value") != null) {
							map98_50MapValue = map98_50Map.get("Value").toString();
						}
						if (StringUtils.isNotBlank(map98_50MapValue) && map98_50MapValue.startsWith("[")
								&& map98_50MapValue.endsWith("]")) {
							ArrayList map98_50MapValueList = JSONObject.parseObject(map98_50MapValue, ArrayList.class);
							fn.setReferencedSOPClassUID(map98_50MapValueList);
						}
					}
					if (StringUtils.isNotBlank(map98_55) && map98_55.startsWith("{") && map98_55.endsWith("}")) {
						HashMap map98_55Map = JSONObject.parseObject(map98_55, HashMap.class);
						String map98_55MapValue = "";
						if (map98_55Map.get("Value") != null) {
							map98_55MapValue = map98_55Map.get("Value").toString();
						}
						if (StringUtils.isNotBlank(map98_55MapValue) && map98_55MapValue.startsWith("[")
								&& map98_55MapValue.endsWith("]")) {
							ArrayList map99_55MapValueList = JSONObject.parseObject(map98_55MapValue, ArrayList.class);
							fn.setReferencedSOPInstanceUID(map99_55MapValueList);
						}
					}
					if (StringUtils.isNotBlank(map98_97) && map98_97.startsWith("{") && map98_97.endsWith("}")) {
						HashMap map98_97Map = JSONObject.parseObject(map98_97, HashMap.class);
						String map98_97MapValue = "";
						if (map98_97Map.get("Value") != null) {
							map98_97MapValue = map98_97Map.get("Value").toString();
							if(StringUtils.isNotBlank(map98_97MapValue)&&map98_97MapValue.startsWith("[")&&map98_97MapValue.endsWith("]")){
								map98_97MapValue=map98_97MapValue.substring(1,map98_97MapValue.length()-1);
							}
							if (StringUtils.isNotBlank(map98_97MapValue) && StringUtils.isNumeric(map98_97MapValue)) {
								int parseValue = Integer.parseInt(map98_97MapValue);
								if (parseValue == 272 || (42752 <= parseValue && parseValue <= 43007)) {
									retry = true;
								}
							}
						}
						fn.setFailureReason(map98_97MapValue);
					}
					failedSopInfoList.add(fn);
				}
			}
		}
	}

	public void dealWithStr99(List<SuccessSopInfo> successSopInfoList, List<WarnningSopInfo> warnningSopInfoList,
			String str99) {
		HashMap map99 = JSONObject.parseObject(str99, HashMap.class);
		String map99Value = "";
		if (map99.get("Value") != null) {
			map99Value = map99.get("Value").toString();
		}
		if (StringUtils.isNotBlank(map99Value) && map99Value.startsWith("[") && map99Value.endsWith("]")) {
			ArrayList map99ValueList = JSONObject.parseObject(map99Value, ArrayList.class);
			for (int i = 0; i < map99ValueList.size(); i++) {
				String map99ValueListn = "";
				if (map99ValueList.get(i) != null) {
					map99ValueListn = map99ValueList.get(i).toString();
				}
				if (StringUtils.isNotBlank(map99ValueListn) && map99ValueListn.startsWith("{")
						&& map99ValueListn.endsWith("}")) {
					SuccessSopInfo sn = new SuccessSopInfo();
					WarnningSopInfo wn = new WarnningSopInfo();
					boolean isWarnning = false;
					HashMap map99ValueListnMap = JSONObject.parseObject(map99ValueListn, HashMap.class);
					String map99_50 = "";
					String map99_55 = "";
					String map99_90 = "";
					String map99_96 = "";
					String map99_61 = "";
					if (map99ValueListnMap.get("00081150") != null) {
						map99_50 = map99ValueListnMap.get("00081150").toString();
					}
					if (map99ValueListnMap.get("00081155") != null) {
						map99_55 = map99ValueListnMap.get("00081155").toString();
					}
					if (map99ValueListnMap.get("00081190") != null) {
						map99_90 = map99ValueListnMap.get("00081190").toString();
					}
					if (map99ValueListnMap.get("00081196") != null) {
						map99_96 = map99ValueListnMap.get("00081196").toString();
					}
					if (map99ValueListnMap.get("04000561") != null) {
						map99_61 = map99ValueListnMap.get("04000561").toString();
					}

					if (StringUtils.isNotBlank(map99_96) && map99_96.startsWith("{") && map99_96.endsWith("}")) {
						HashMap map99_96Map = JSONObject.parseObject(map99_96, HashMap.class);
						isWarnning = true;
						String map99_96MapValue = "";
						if (map99_96Map.get("Value") != null) {
							map99_96MapValue = map99_96Map.get("Value").toString();
						}
						wn.setWarnningReason(map99_96MapValue);
					}
					if (StringUtils.isNotBlank(map99_61) && map99_61.startsWith("{") && map99_61.endsWith("}")) {
						HashMap map99_61Map = JSONObject.parseObject(map99_61, HashMap.class);
						isWarnning = true;
						String map99_61MapValue = "";
						if (map99_61Map.get("Value") != null) {
							map99_61MapValue = map99_61Map.get("Value").toString();
						}
						if (isWarnning) {
							wn.setOriginalAttributesSequence(map99_61MapValue);
						} else {
							sn.setOriginalAttributesSequence(map99_61MapValue);
						}
					}
					if (StringUtils.isNotBlank(map99_50) && map99_50.startsWith("{") && map99_50.endsWith("}")) {
						HashMap map99_50Map = JSONObject.parseObject(map99_50, HashMap.class);
						String map99_50MapValue = "";
						if (map99_50Map.get("Value") != null) {
							map99_50MapValue = map99_50Map.get("Value").toString();
						}
						if (StringUtils.isNotBlank(map99_50MapValue) && map99_50MapValue.startsWith("[")
								&& map99_50MapValue.endsWith("]")) {
							ArrayList map99_50MapValueList = JSONObject.parseObject(map99_50MapValue, ArrayList.class);
							if (isWarnning) {
								wn.setReferencedSOPClassUID(map99_50MapValueList);
							} else {
								sn.setReferencedSOPClassUID(map99_50MapValueList);
							}
						}
					}
					if (StringUtils.isNotBlank(map99_55) && map99_55.startsWith("{") && map99_55.endsWith("}")) {
						HashMap map99_55Map = JSONObject.parseObject(map99_55, HashMap.class);
						String map99_55MapValue = "";
						if (map99_55Map.get("Value") != null) {
							map99_55MapValue = map99_55Map.get("Value").toString();
						}
						if (StringUtils.isNotBlank(map99_55MapValue) && map99_55MapValue.startsWith("[")
								&& map99_55MapValue.endsWith("]")) {
							ArrayList map99_55MapValueList = JSONObject.parseObject(map99_55MapValue, ArrayList.class);
							if (isWarnning) {
								wn.setReferencedSOPInstanceUID(map99_55MapValueList);
							} else {
								sn.setReferencedSOPInstanceUID(map99_55MapValueList);
							}
						}
					}
					if (StringUtils.isNotBlank(map99_90) && map99_90.startsWith("{") && map99_90.endsWith("}")) {
						HashMap map99_90Map = JSONObject.parseObject(map99_90, HashMap.class);
						String map99_90MapValue = "";
						if (map99_90Map.get("Value") != null) {
							map99_90MapValue = map99_90Map.get("Value").toString();
						}
						if (StringUtils.isNotBlank(map99_90MapValue) && map99_90MapValue.startsWith("[")
								&& map99_90MapValue.endsWith("]")) {
							ArrayList map99_90MapValueList = JSONObject.parseObject(map99_90MapValue, ArrayList.class);
							if (isWarnning) {
								wn.setRetrieveURL(map99_90MapValueList);
							} else {
								sn.setRetrieveURL(map99_90MapValueList);
							}
						}
					}
					if (isWarnning) {
						warnningSopInfoList.add(wn);
					} else {
						successSopInfoList.add(sn);
					}
				}
			}

		}
	}
	
	/**
	 * 202情况下返回组装好的messageMap
	 */
	public HashMap<String, Object> getMessageMap(FileUploadMessage fum, String userId) {
		HashMap<String, Object> mp = new HashMap<String, Object>();
		HashMap<String, Object> mpUID = new HashMap<String, Object>();
		List<String> retrieveURL = fum.getSuccessSopInfoList().get(0).getRetrieveURL();
		String successUrl = "";
		if (retrieveURL != null && retrieveURL.size() > 0) {
			successUrl = StringUtils.isNotBlank(retrieveURL.get(0)) ? retrieveURL.get(0) : "";
		}
		String studyUID = WordUtils.getSubUtilSimple(successUrl, "studies/(.*?)/series");
		mpUID.put("studyUID", studyUID);
		String seriesUID = WordUtils.getSubUtilSimple(successUrl, "series/(.*?)/instances");
		mpUID.put("seriesUID", seriesUID);
		String sopUID = WordUtils.getSubUtilSimple(successUrl, "instances/(.*?)$");
		mpUID.put("sopUID", sopUID);
		mpUID.put("userId", userId);

		mp.put("code", ClientInfoCollectionEnum.FILE_ARC_SUCCESS.getCode());
		mp.put("message", JSONObject.toJSONString(mpUID));
		mp.put("level","info");
		return mp;
	}

	public static void main(String[] args) {
		DealFileUploadResponseUtil d = new DealFileUploadResponseUtil();
		int r = d.getResultJsonToFum(
				"{\"00081190\":{\"vr\":\"UR\"},\"00081198\":{\"vr\":\"SQ\",\"Value\":[{\"00081150\":{\"vr\":\"UI\",\"Value\":[\"1.2.840.10008.1.3.10\"]},\"00081155\":{\"vr\":\"UI\"},\"00081197\":{\"vr\":\"US\",\"Value\":[]}}]}}",
				new FileUploadMessage());
		System.out.println(r);
	}
}
