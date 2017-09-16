package test.project1;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.vertx.core.*;
import io.vertx.core.http.*;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.BodyHandler;

import io.vertx.ext.mongo.MongoClient;

public class Server extends AbstractVerticle{

	private Router router;
	
	//temp data struct
	private Map<Integer, Word> words = new LinkedHashMap<>();

	@Override
	public void start(Future<Void> fut) throws Exception{
		System.out.println("##################in the program , bitch!########");
		MongoClient client = MongoClient.createShared(vertx, config);
		///TODO:get all words from file
		this.createSomeData();
		
		router = Router.router(vertx);
		
//		router.route("/analyze").handler(routingContext -> {
//			HttpServerResponse response = routingContext.response();
//			response.putHeader("content-type", "text/html")
//				.end("<h1>Hello from eclipse!</h1>");
//		});

		vertx.createHttpServer().requestHandler(router::accept)
			.listen(
				config().getInteger("http.port", 8080),
				result -> {
					if (result.succeeded()) {
						fut.complete();
					} else {
						fut.fail(result.cause());
					}
				});
		System.out.println("calling  GET !");
//		router.route("/analyze").handler(BodyHandler.create());
//		router.post("/analyze").handler(this::getAll);
		router.route("/analyze").handler(BodyHandler.create());
		router.post("/analyze").handler(this::addWord);
		
//		this.saveToFile("textttt test");
	}
	
	//temp:
	private void getAll(RoutingContext routingContext) {
		System.out.println("inside getAll");
		  routingContext.response()
	      .putHeader("content-type", "application/json; charset=utf-8")
	      .end(Json.encodePrettily(words.values()));
		 
	}
	
	private void createSomeData() {
		System.out.println("creating some sample data");
		  Word word1 = new Word("abc");
		  words.put(0, word1);
		  Word word2 = new Word("whiskey");
		  words.put(1, word2);
	}
	
	private void addWord(RoutingContext routingContext) {
//		System.out.println("in the addWord !" + routingContext.getBodyAsString());'
		
		String word = routingContext.request().getParam("text");
		System.out.println("new word: " + word);
		Word w = new Word(word);
		words.put(2, w);
		
		routingContext.response()
			.putHeader("content-type", "application/json; charset=utf-8")
			.end(Json.encodePrettily(words.values()));
		
	}
	
	//TODO: consider mongoDB
	private void saveToFile(String text) {
		System.out.println("in the savetofile !");
		String fileName = "text.txt";
		File file = new File(fileName);
	}
	
	///TODO: a method to return 
	//
	
}
