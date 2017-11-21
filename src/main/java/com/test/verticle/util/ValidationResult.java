package com.test.verticle.util;

import io.vertx.core.json.JsonObject;

public class ValidationResult {

	private boolean valid = true;
	private int statusCode = 200;
	private JsonObject result = new JsonObject();

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public JsonObject getResult() {
		return result;
	}

	public void setResult(JsonObject result) {
		this.result = result;
	}
	
	
	public static ValidationResult valid() {
		ValidationResult res = new ValidationResult();
		res.setValid(true);
		res.setStatusCode(200);
		res.setResult(new JsonObject());
		return res;
	}

}
