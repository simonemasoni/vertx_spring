package com.test.verticle.handler;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

public interface RequestHandler {

	HttpMethod getMethod();

	String getPath();

	Handler<RoutingContext> handle();
	
}
