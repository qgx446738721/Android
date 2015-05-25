package org.voiddog.lib.http;

import android.util.Log;

import java.io.UnsupportedEncodingException;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

public class GsonRequest extends Request<HttpResponsePacket> {
	private final Listener<HttpResponsePacket> mListener;

	private Gson mGson;
	private String params;

	public GsonRequest(int method, String url, String params,
			Listener<HttpResponsePacket> listener, ErrorListener errorListener) {
		super(method, url, errorListener);

		mGson = new Gson();
		mListener = listener;
		this.params = params;
		setShouldCache(false);

		setRetryPolicy(new DefaultRetryPolicy(2 * 60 * 1000, 0,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

	public GsonRequest(String url, String params,
			Listener<HttpResponsePacket> listener, ErrorListener errorListener) {
		this(Method.POST, url, params, listener, errorListener);
		this.params = params;
	}

	@Override
	protected Response<HttpResponsePacket> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			Log.i("TAG", jsonString);
			return Response.success(mGson.fromJson(jsonString, HttpResponsePacket.class),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(HttpResponsePacket response) {
		mListener.onResponse(response);
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		return params == null ? super.getBody() : params.getBytes();
	}
}
