package com.test.verticle;

import static com.jayway.restassured.RestAssured.get;
import static com.test.utils.TestUtils.getRandomPort;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.RestAssured;
import com.test.verticle.handler.VertxInvocationHandler;
import com.test.verticle.handler.impl.LoginHandler;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class IntegrationTest {

	@BeforeClass
	public static void configureRestAssured() {
		Vertx vertx = Vertx.vertx();

		JsonObject config = new JsonObject().put("server.port", getRandomPort());

		LoginHandler loginHandler = new LoginHandler();
		loginHandler.setInvocationHandler(new VertxInvocationHandler(vertx));

		RestVerticle restVerticle = new RestVerticle();
		restVerticle.setRequestHandlers(Arrays.asList(loginHandler));

		ServiceVerticle serviceVerticle = new ServiceVerticle();
		serviceVerticle.setServiceHandlers(Arrays.asList(loginHandler));

		DeploymentOptions options = new DeploymentOptions().setConfig(config);
		vertx.deployVerticle(restVerticle, options);
		vertx.deployVerticle(serviceVerticle, options);

		RestAssured.baseURI = "http://localhost";
		RestAssured.port = config.getInteger("server.port");
	}

	@AfterClass
	public static void unconfigureRestAssured() {
		RestAssured.reset();
		Vertx.vertx().close();
	}

	@Test
	public void check_login_ok() {
		get("/login?username=test&password=test").then().assertThat().statusCode(200);
	}

	@Test
	public void check_login_missing_username() {
		get("/login").then().assertThat().statusCode(403).body("type", equalTo("error")).body("message",
				equalTo("Username is required"));
	}

	@Test
	public void check_login_missing_password() {
		get("/login?username=test").then().assertThat().statusCode(403).body("type", equalTo("error")).body("message",
				equalTo("Password is required"));
	}

}
