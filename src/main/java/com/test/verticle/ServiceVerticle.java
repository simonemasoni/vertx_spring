package com.test.verticle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.verticle.handler.ServiceHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

@Service
public class ServiceVerticle extends AbstractVerticle {

	@Autowired
	private List<ServiceHandler> serviceHandlers;

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		for (ServiceHandler serviceHandler : serviceHandlers) {
			vertx.eventBus().consumer(serviceHandler.getPath(), handler(serviceHandler));
		}
		startFuture.succeeded();
	}

	private Handler<Message<JsonObject>> handler(ServiceHandler serviceHandler) {
		return msg -> {
			vertx.executeBlocking(ar -> {
				try {
					ar.complete(serviceHandler.invokeService(msg.body()));
				} catch (Exception e) {
					ar.fail(e);
				}
			}, res -> {
				if (res.succeeded()) {
					msg.reply(res.result());
				} else {
					int failureCode = 500;
					String message = res.cause().getMessage();
					msg.fail(failureCode, message);
				}
			});
		};
	}

	public void setServiceHandlers(List<ServiceHandler> serviceHandlers) {
		this.serviceHandlers = serviceHandlers;
	}

}
