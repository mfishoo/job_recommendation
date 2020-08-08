package entity;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;


public class Item {
	private String itemId;
	private String name;
	private String address;
	private String imageUrl;
	private String url;
	private Set<String> keywords;
	
	private Item(ItemBuilder builder) {
		this.itemId = builder.ItemId;
		this.name = builder.name;
		this.address = builder.address;
		this.imageUrl = builder.imageUrl;
		this.imageUrl = builder.imageUrl;
		this.keywords = builder.keywords;
	}
	
	public String getItemId() {
		return itemId;
	}
	public String getName() {
		return name;
	}
	public String getAddress() {
		return address;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public Set<String> getKeywords() {
		return keywords;
	}
	
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
	
	public static class ItemBuilder{
		private String ItemId;
		private String name;
		private String address;
		private String imageUrl;
		private String url;
		private Set<String> keywords;
		
		
		public void setItemId(String itemId) {
			ItemId = itemId;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public void setKeywords(Set<String> keywords) {
			this.keywords = keywords;
		}
		public Item build() {
			return new Item(this);
		}
	}

}
