package com.test.verticle;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.verticle.handler.RequestHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

@Service
public class RestVerticle extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(RestVerticle.class);

	@Autowired
	private List<RequestHandler> requestHandlers = new ArrayList<>();

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		Router router = Router.router(vertx);

		configureRoutes(router);

		final Integer serverPort = context.config().getInteger("server.port", 8765);
		logger.info("Starting http server on port {}", serverPort);
		vertx.createHttpServer().requestHandler(router::accept).listen(serverPort);

		startFuture.succeeded();
	}

	private void configureRoutes(Router router) {
		// configure static resources
		router.route("/web/*").handler(StaticHandler.create("web"));

		// configure eventbus
		router.route("/eventbus/*").handler(eventBusHandler());

		// configure routes
		for (RequestHandler requestHandler : requestHandlers) {
			logger.info("Configuring route {}:{}", requestHandler.getMethod(), requestHandler.getPath());
			router.route(requestHandler.getMethod(), requestHandler.getPath()).handler(requestHandler.handle());
		}
	}

	private SockJSHandler eventBusHandler() {
		BridgeOptions options = new BridgeOptions().addOutboundPermitted(new PermittedOptions().setAddressRegex(".*"));
		return SockJSHandler.create(vertx).bridge(options, event -> {
			if (event.type() == BridgeEventType.SOCKET_CREATED) {
				logger.info("A socket was created");
			}
			event.complete(true);
		});
	}

	public void setRequestHandlers(List<RequestHandler> requestHandlers) {
		this.requestHandlers = requestHandlers;
	}

}
