package com.test.verticle.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public interface InvocationHandler {

	void invoke(String path, JsonObject inputParams, Handler<AsyncResult<Message<JsonObject>>> replyHandler);

}
