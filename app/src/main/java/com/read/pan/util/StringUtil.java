package com.read.pan.util;

import java.util.UUID;

public class StringUtil {
	/**
	 * 更改文件名为uuid
	 * 如:1.png————>2266aeb9-c5c3-46be-81f5-8b9ba3f719d6.png
	 */
	public static String changeFileName(String oldName){
		String suffix=oldName.substring(oldName.indexOf("."));
		return UUID.randomUUID().toString()+suffix;
	}
	/**
	 * 根据书名路径获得书名加扩展名
	 */
	public static String getBookInfo(String path,String name){
		String result=name;
		String suffix=path.substring(path.lastIndexOf("."));
		result+=suffix;
		return result;
	}
}
