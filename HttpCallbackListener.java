package com.example.weather;

public interface HttpCallbackListener {

	public void onFinish(String response);
	public void onError(Exception e);
}
