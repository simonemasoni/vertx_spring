package com.test.verticle.handler.impl;

import org.springframework.stereotype.Component;

import com.test.verticle.handler.AbstractRequestHandler;
import com.test.verticle.util.ValidationResult;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@Component
public class LoginHandler extends AbstractRequestHandler {

	public LoginHandler() {
		super(HttpMethod.GET, "/login");
	}

	@Override
	protected JsonObject extractParams(RoutingContext ctx) {
		String username = ctx.request().getParam("username");
		String password = ctx.request().getParam("password");
		return new JsonObject().put("username", username).put("password", password);
	}

	@Override
	protected ValidationResult validate(JsonObject inputParams) {
		ValidationResult result = new ValidationResult();
		if (inputParams.getString("username") == null) {
			JsonObject error = new JsonObject().put("type", "error").put("message", "Username is required");
			result.setStatusCode(403);
			result.setValid(false);
			result.setResult(error);
		} else if (inputParams.getString("password") == null) {
			JsonObject error = new JsonObject().put("type", "error").put("message", "Password is required");
			result.setStatusCode(403);
			result.setValid(false);
			result.setResult(error);
		}
		return result;
	}

	@Override
	public JsonObject invokeService(JsonObject params) {
		return new JsonObject().put("result", "ok");
	}
	
	

}
