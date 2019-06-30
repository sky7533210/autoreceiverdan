package com.sky.util;

import java.io.UnsupportedEncodingException;

public class Jiami {
	private static byte[] my= new byte[] {0x11,0x1c,0x3a,0x48,0x56,0x11,0x26,0x58,0x2a,0x45};
	public static byte[] jiami(byte[] yw){
		for(int i=0;i<yw.length;++i){
			yw[i]=(byte) (yw[i]^my[i%my.length]);
		}		
		return yw;
	}
	public static String jiami2String(byte[] yw){
		try {
			return new String(jiami(yw),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	public static String jiami2String(String str) {
		try {
			return jiami2String(str.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
