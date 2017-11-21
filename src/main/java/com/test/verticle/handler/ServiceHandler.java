package com.test.verticle.handler;

import io.vertx.core.json.JsonObject;

public interface ServiceHandler {

	String getPath();
	
	JsonObject invokeService(JsonObject params);

}
