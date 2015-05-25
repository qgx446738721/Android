package org.voiddog.lib.http;

import java.lang.reflect.Type;

import com.google.gson.Gson;

public class HttpResponsePacket {
	private static final Gson gson = new Gson();
	public int code;
	public String message;
	public String data;

	public <T> T getData(Type type) {
		try {
			if (code != 0) {
				return null;
			}
			return gson.fromJson(data, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
