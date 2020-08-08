package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item1;

public class RpcHelper {
	// writes a JSONArray to http response
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException {
		response.setContentType("application/json");
		response.getWriter().print(array);

	}

	// writes a JSONObject to http response
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {
		response.setContentType("application/json");
		response.getWriter().print(obj);
	}

	// parses a JSONObject from http request
	public static JSONObject readJSONObject(HttpServletRequest request) throws IOException {
		BufferedReader reader = new BufferedReader(request.getReader());
		StringBuilder requestBody = new StringBuilder();
		String line = null;

		while ((line = reader.readLine()) != null) {
			requestBody.append(line);
		}
		return new JSONObject(requestBody.toString());
	}

	// convert a JSON object to Item object
	public static Item1 parseFavoriteItem(JSONObject favoriteItem) {

		Set<String> keywords = new HashSet<>();
		JSONArray array = favoriteItem.getJSONArray("keywords");
		for (int i = 0; i < array.length(); i++) {
			keywords.add(array.getString(i));
		}

		Item1 item = Item1.builder().itemId(favoriteItem.getString("item_id")).name(favoriteItem.getString("name"))
				.address(favoriteItem.getString("address")).url(favoriteItem.getString("url"))
				.imageUrl(favoriteItem.getString("image_url")).keywords(keywords).build();

		return item;
	}
}
