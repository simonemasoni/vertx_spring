package com.test.verticle.handler;

public abstract class AbstractServiceHandler implements ServiceHandler {

	private String path;

	@Override
	public String getPath() {
		return path;
	}

}
