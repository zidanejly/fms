package com.jfhealthcare.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 字符工具类
 * 
 * @author xujinma
 */
public class WordUtils {
	// 判断一个字符是否是中文  
    public static boolean isChinese(char c) {  
        return c >= 0x4E00 &&  c <= 0x9FA5;// 根据字节码判断  
    }  
    // 判断一个字符串是否含有中文  
    public static boolean isChinese(String str) {  
        if (str == null) return false;  
        for (char c : str.toCharArray()) {  
            if (isChinese(c)) return true;// 有一个中文字符就返回  
        }  
        return false;
    } 
    /**
	 * 正则匹配获取数据
	 * */
	public static String getSubUtilSimple(String soap,String rgex){  
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式  
        Matcher m = pattern.matcher(soap);  
        while(m.find()){  
            return m.group(1);
        }  
        return "";  
    } 
	public static void main(String[] args) {
		String url = "http://192.168.10.97:8080/dcm4chee-arc/aets/piclarc/rs/studies/1.2.840.114062.2.123.123.123.121.2017.9.6.11.30.58.2083980945.1/series/1.2.840.113564.52.20170906112839306.2460/instances/1.2.840.113564.54.20170906112839306.2460";
		String subUtilSimple1 = getSubUtilSimple(url,"studies/(.*?)/series");
		String subUtilSimple2 = getSubUtilSimple(url,"series/(.*?)/instances");
		String subUtilSimple3 = getSubUtilSimple(url,"instances/(.*?)$");
		System.out.println(subUtilSimple1);
		System.out.println(subUtilSimple2);
		System.out.println(subUtilSimple3);
	}
}
