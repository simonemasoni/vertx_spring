package com.test.verticle.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.test.verticle.util.ValidationResult;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public abstract class AbstractRequestHandler implements RequestHandler, ServiceHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private HttpMethod method;
	private String path;

	@Autowired
	private InvocationHandler invocationHandler;

	public AbstractRequestHandler(HttpMethod method, String path) {
		super();
		this.method = method;
		this.path = path;
	}

	@Override
	public HttpMethod getMethod() {
		return method;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public final Handler<RoutingContext> handle() {
		return ctx -> {
			ctx.response().putHeader("content-type", getResponseContentType());
			JsonObject inputParams = extractParams(ctx);
			ValidationResult validationResult = validate(inputParams);
			if (validationResult.isValid()) {
				// valid invocation
				invocationHandler.invoke(getPath(), inputParams, replyHandler(ctx.response()));
			} else {
				// validation error
				ctx.response().setStatusCode(validationResult.getStatusCode())
						.end(validationResult.getResult().encode());
			}
		};
	}

	private Handler<AsyncResult<Message<JsonObject>>> replyHandler(HttpServerResponse response) {
		return ar -> {
			if (ar.succeeded()) {
				// manage successful reply
				Message<JsonObject> msg = ar.result();
				response.setStatusCode(200).end(msg.body().encode());
			} else {
				// manage failure reply
				logger.error("Error invoking " + getMethod() + ":" + getPath(), ar.cause());
				response.setStatusCode(500)
						.end(new JsonObject().put("type", "error").put("message", ar.cause().getMessage()).encode());
			}
		};
	}

	/**
	 * Extract params from routing context
	 * 
	 * @param ctx
	 *            routing context
	 * @return params as json object. Default getBodyAsJson
	 */
	protected JsonObject extractParams(RoutingContext ctx) {
		return ctx.getBodyAsJson();
	}

	protected ValidationResult validate(JsonObject inputParams) {
		return ValidationResult.valid();
	}

	private String getResponseContentType() {
		return "application/json";
	}

	public void setInvocationHandler(InvocationHandler invocationHandler) {
		this.invocationHandler = invocationHandler;
	}

}
