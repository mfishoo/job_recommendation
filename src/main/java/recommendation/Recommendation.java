package recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.MySQLConnection;
import entity.Item1;
import external.GitHubClient1;

// 1, given a user, get all the events (ids) this user has favorited.

// 2, given all these events, get their keywords and sort by count.

// 3, given these categories, use Github Job API with keyword, then filter out user favorited events.

public class Recommendation {
	
	public List<Item1> recommendItems(String userId, double lat, double lon){
		
		System.out.println("inside recommendation items");
		
		
		// 1. get all favorited itemids in history table
		MySQLConnection connection = new MySQLConnection();
		Set<String> favoritedItemIds = connection.getFavoriteItemsIds(userId);
		System.out.println("got fav item ids");
		
		// 2. get all keywords
		Map<String, Integer> allKeywords = new HashMap<>();
		for(String itemId : favoritedItemIds) {
			Set<String> keywords = connection.getKeywords(itemId);
			for(String keyword : keywords) {
				allKeywords.put(keyword, allKeywords.getOrDefault(keyword, 0) + 1);
			}
		}
		connection.close();
		System.out.println("got keywords, total nums: " + allKeywords.size());
		
		// 3. sort by count
		List<Entry<String, Integer>> keywordList = new ArrayList<>(allKeywords.entrySet());
		Collections.sort(keywordList, (Entry<String, Integer> e1, Entry<String, Integer> e2) ->{
			return Integer.compare(e2.getValue(), e1.getValue());
		});
		
		// 4. cut down search list with top 3
		if(keywordList.size() > 3) {
			keywordList = keywordList.subList(0, 3);
		}
		System.out.println("search keywords: " + keywordList.toString());
		
		// 5. search based on keywords
		Set<String> visitedItemIds = new HashSet<>(); // sync with recommenditem since rc items can't dedup
		List<Item1> recommendedItems = new ArrayList<>();
		GitHubClient1 client = new GitHubClient1();
		
		int count = 0;
		for(Entry<String, Integer> keyword : keywordList) {
			List<Item1> items = client.search(lat, lon, keyword.getKey());
			count += items.size();
			
			for(Item1 item : items) {
				if(!favoritedItemIds.contains(item.getItemId()) && !visitedItemIds.contains(item.getItemId())) {
					recommendedItems.add(item);
					visitedItemIds.add(item.getItemId());
				}
			}
		}
		
		System.out.println("got search results with keywords, total results: " + count);
		return recommendedItems;
	}

}
