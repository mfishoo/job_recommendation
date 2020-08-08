package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item1;

public class GitHubClient1 {
	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";

	// search jobs from github API
	public List<Item1> search(double lat, double lon, String keyword) {
		System.out.println("in github search");
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = String.format(URL_TEMPLATE, keyword, lat, lon);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
			if (response.getStatusLine().getStatusCode() != 200) {
				return new ArrayList<>();
			}

			System.out.println("get github search response");
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return new ArrayList<>();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuilder responseBody = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				responseBody.append(line);
			}
			JSONArray array = new JSONArray(responseBody.toString());

			// extract keywords from description, call monkeylearn API
			return getItemList(array);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

//	call monkeylearn API for keywords & parse info to Item class
	private List<Item1> getItemList(JSONArray array) throws JSONException {
		System.out.println("inside monkeylearn call");
		List<Item1> itemList = new ArrayList<>();
		List<String> descriptionList = new ArrayList<>();

		// first loop for keywords
		for (int i = 0; i < array.length(); i++) {
			String description = getStringFieldOrEmpty(array.getJSONObject(i), "description");

			// pre-process decription for monkeylearn keyword
			if (description.equals("") || description.equals("\n")) {
				descriptionList.add(getStringFieldOrEmpty(array.getJSONObject(i), "title"));
			} else {
				descriptionList.add(description);
			}
		}

		// call monkeylearn
		List<List<String>> keywords = MonkeyLearnClient
				.extractKeywords(descriptionList.toArray(new String[descriptionList.size()]));
		System.out.println("get keyword back from monkeylearn");

		// second loop for other info
		for (int i = 0; i < array.length(); i++) {

			JSONObject object = array.getJSONObject(i);

			Item1 itm = Item1.builder().itemId(getStringFieldOrEmpty(object, "id"))
					.name(getStringFieldOrEmpty(object, "title")).address(getStringFieldOrEmpty(object, "location"))
					.url(getStringFieldOrEmpty(object, "url")).imageUrl(getStringFieldOrEmpty(object, "company_logo"))
					.keywords(new HashSet<String>(keywords.get(i))).build();
			itemList.add(itm);

		}
		return itemList;
	}

	private String getStringFieldOrEmpty(JSONObject obj, String field) {
		return obj.isNull(field) ? "" : obj.getString(field);
	}
}
