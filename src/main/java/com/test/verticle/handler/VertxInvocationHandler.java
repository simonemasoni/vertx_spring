package com.test.verticle.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class VertxInvocationHandler implements InvocationHandler {

	private Vertx vertx; 

	public VertxInvocationHandler(Vertx vertx) {
		super();
		this.vertx = vertx;
	}

	@Override
	public void invoke(String path, JsonObject inputParams, Handler<AsyncResult<Message<JsonObject>>> replyHandler) {
		vertx.eventBus().send(path, inputParams, replyHandler);
	}

}
