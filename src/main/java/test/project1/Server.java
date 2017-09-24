package test.project1;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.*;
import io.vertx.core.json.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.BodyHandler;

public class Server extends AbstractVerticle {

	private Router router;
	private final String FILENAME = System.getProperty("user.dir") + "/testing.json";

	@Override
	public void start(Future<Void> fut) throws Exception {
		router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080),
				result -> {
					if (result.succeeded()) {
						fut.complete();
					} else {
						fut.fail(result.cause());
					}
				});
		
		router.route("/analyze").handler(BodyHandler.create());
		router.post("/analyze").handler(this::solution);
	}

	private void solution(RoutingContext routingContext) {
		String text = routingContext.request().getParam("text");
		
		JsonObject jsonResponse = new JsonObject();
		Word newWord = new Word(text);

		SortedSet<Word> treeValues = new TreeSet<Word>(Comparator.comparing(Word::getTotalCharVal));
		SortedSet<Word> treeLexical = new TreeSet<Word>(Comparator.comparing(Word::getText));

		List<Word> words = this.getAll();
		
		treeValues.addAll(words);
		treeLexical.addAll(words);

		Word closestValue = new Word();
		Word closestLexical = new Word();
		if (!treeValues.isEmpty()) {
			String resultValues = this.getClosestTotalVal(treeValues, newWord);
			String resultLexical = this.getClosestLexical(treeLexical, newWord);

			closestValue.setText(resultValues);
			closestLexical.setText(resultLexical);		

		}
		
		jsonResponse.put("value", closestValue.getText());
		jsonResponse.put("lexical", closestLexical.getText());
		
		if(!treeLexical.contains(newWord)) {
			try {
				this.saveToFile(newWord);
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}

		routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(jsonResponse));
	}
	
	private String getClosestTotalVal(SortedSet<Word> set, Word word) {
		Iterator<Word> it = set.iterator();

		Word wMinDist = (Word) it.next();
		int minDist = Math.abs(wMinDist.getTotalCharVal() - word.getTotalCharVal());

		while(it.hasNext()) {
			Word w = (Word)it.next();
			int dist = Math.abs(w.getTotalCharVal() - word.getTotalCharVal());
			if(minDist > dist) {
				minDist = dist;
				wMinDist = w;
			}
		}
		
		return wMinDist.getText();
	}
	
	private String getClosestLexical(SortedSet<Word> set, Word word) {
		Iterator<Word> it = set.iterator();

		Word wMinDist = (Word) it.next();
		int minDist = Math.abs(wMinDist.getText().compareTo(word.getText()));

		while(it.hasNext()) {
			Word w = (Word)it.next();
			int dist = Math.abs(w.getText().compareTo(word.getText()));
			if(minDist > dist) {
				minDist = dist;
				wMinDist = w;
			}
		}
		
		return wMinDist.getText();
	}

	private List<Word> getAll() {
		File file = new File(FILENAME);
		ObjectMapper mapper = new ObjectMapper();
		List<Word> words = new ArrayList<>();

		if (file.exists()) {
			try {
				words = mapper.readValue(file, new TypeReference<ArrayList<Word>>() {
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return words;
	}

	private void saveToFile(Word word) throws IOException {
		File file = new File(FILENAME);		
		ObjectMapper mapper = new ObjectMapper();

		if (!file.exists()) {
			List<Word> words = new ArrayList<>();
			words.add(word);			
			mapper.writeValue(new File(FILENAME), words);
		} else {
			RandomAccessFile raf = new RandomAccessFile(FILENAME, "rw");
			long pos = raf.length();
			while (raf.length() > 0) {
				pos--;
				raf.seek(pos);
				if (raf.readByte() == ']') {
					raf.seek(pos);
					break;
				}
			}
			String newWord = mapper.writeValueAsString(word);
			raf.writeBytes("," + newWord + "]");
			raf.close();
		}
	}
}