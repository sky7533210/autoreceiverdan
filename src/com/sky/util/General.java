package com.sky.util;

import java.net.URLEncoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class General {
	private static  String otherHost = "https://aip.baidubce.com/rest/2.0/ocr/v1/numbers";
    public static String getCode(String base64) {
        try {
            String params = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(base64, "UTF-8");
            String accessToken = "24.578bff33366dc6bb3d087ad0e8b1ec7b.2592000.1554284933.282335-15678536";
            String result = HttpUtil.post(otherHost, accessToken, params);
            
            JSONObject jsonObject= JSON.parseObject(result);
            JSONArray jsonArray= jsonObject.getJSONArray("words_result");
            String code= jsonArray.getJSONObject(0).getString("words");
            StringBuilder sBuilder=new StringBuilder();
            for(int i=0;i<code.length();++i)
            sBuilder.append(Integer.toHexString(code.charAt(i)));           
            return sBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
