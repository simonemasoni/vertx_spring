package com.test;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.test.verticle.RestVerticle;
import com.test.verticle.ServiceVerticle;
import com.test.verticle.handler.InvocationHandler;
import com.test.verticle.handler.VertxInvocationHandler;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@SpringBootApplication
@Configuration
@EnableAsync
@ComponentScan(basePackages = { "com.test.verticle", "com.test.verticle.handler.impl" })
@EnableTransactionManagement
public class VertxSpringApplication {

	private static final String CONFIG_FILE_DEFAULT_PATH = "config.json";

	private Vertx vertx;

	private JsonObject config;

	@Autowired
	private RestVerticle restVerticle;

	@Autowired
	private ServiceVerticle serviceVerticle;

	public VertxSpringApplication() {
		vertx = Vertx.vertx();
	}

	@PostConstruct
	public void deployVerticles() {
		doWithConfiguration(config -> {
			vertx.deployVerticle(serviceVerticle, new DeploymentOptions().setConfig(config));
			vertx.deployVerticle(restVerticle, new DeploymentOptions().setConfig(config));
		});
	}

	@Bean
	public InvocationHandler invocationHandler() {
		return new VertxInvocationHandler(vertx);
	}

	private void doWithConfiguration(Handler<JsonObject> handler) {
		if (config != null) {
			handler.handle(config);
		} else {
			loadConfiguration(CONFIG_FILE_DEFAULT_PATH, vertx, ar -> {
				if (ar.succeeded()) {
					handler.handle(ar.result());
				}
			});
		}
	}

	private void loadConfiguration(String filePath, Vertx vertx, Handler<AsyncResult<JsonObject>> handler) {
		ConfigStoreOptions file = new ConfigStoreOptions().setType("file").setFormat("json")
				.setConfig(new JsonObject().put("path", filePath));
		ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(file);
		ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

		retriever.getConfig(config -> {
			handler.handle(config);
		});
	}

	public static void main(String[] args) {
		SpringApplication.run(VertxSpringApplication.class, args);
	}
}
