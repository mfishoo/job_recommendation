package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Item1 {
	private String itemId;
	private String name;
	private String address;
	private String imageUrl;
	private String url;
	private Set<String> keywords;
	// private boolean favorite - when read from db history table, will add this
	// field to the object for frontend

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("item_id", itemId);
		obj.put("name", name);
		obj.put("address", address);
		obj.put("keywords", new JSONArray(keywords));
		obj.put("image_url", imageUrl);
		obj.put("url", url);
		return obj;
	}
}
