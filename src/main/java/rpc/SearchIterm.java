package rpc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import db.MySQLConnection;
import entity.Item1;
import external.GitHubClient1;

/**
 * Servlet implementation class SearchIterm
 */
public class SearchIterm extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchIterm() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	// search jobs with lat, lon, keyword, need userId to save on db
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Received one search request");

		// 1. get request params
		String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String keyword = request.getParameter("keyword");

		System.out.println("Search params: " + lat + ", " + lon + ", " + keyword);

		// 2. get data from github API, monkeylearn is called inside github clients
		GitHubClient1 client = new GitHubClient1();
		List<Item1> items = client.search(lat, lon, null);
		System.out.println("get items back in /search from external APIs");

		// 3. read user's fav from db
		MySQLConnection connection = new MySQLConnection();
		Set<String> favoritedItemIds = connection.getFavoriteItemsIds(userId);
		connection.close();
		System.out.println("get fav item ids back in /search from db");

		// 4. convert result to JSON,
		// need to check whether the item had been favorited by the user before
		// if yes, set favorite field to true for frontend

		JSONArray array = new JSONArray();
		for (Item1 item : items) {
			JSONObject obj = item.toJSONObject();

			// if the item could be found in db history table(fav), set favorite field to
			// true
			obj.put("favorite", favoritedItemIds.contains(item.getItemId()));
			array.put(obj);
		}
		System.out.println("search result done");
		RpcHelper.writeJsonArray(response, array);

	}

}
