package rpc;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import db.MySQLConnection;
import entity.Item1;
import rpc.RpcHelper;

/**
 * Servlet implementation class ItemHistory
 */
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ItemHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("inside /histry GET");
		
		HttpSession session = request.getSession(false);
		if(session == null) {
			response.setStatus(403);
			System.out.println("Invalid session in history get");
			return;
		}
		
		String userId = request.getParameter("user_id");

		// request db
		MySQLConnection connection = new MySQLConnection();
		Set<Item1> items = connection.getFavoriteItems(userId);
		connection.close();
		System.out.println("got db results back in /history");

		// convert db result to JSON
		JSONArray array = new JSONArray();
		for (Item1 item : items) {
			JSONObject obj = item.toJSONObject();
			obj.put("favorite", true);
			array.put(obj);
		}
		System.out.println("/histry GET done");
		RpcHelper.writeJsonArray(response, array);

	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("inside /search delete");
		
		HttpSession session = req.getSession(false);
		if(session == null) {
			resp.setStatus(403);
			System.out.println("Invalid session in histrory delete");
			return;
		}
		

		// 1. read request and get JSON
		// question: why just String s = req.getParameter("user_id");JSONObject obj =
		// new JSONObject(s);
		JSONObject input = RpcHelper.readJSONObject(req);
		String userId = input.getString("user_id");

		// 2. convert JSON to item
		Item1 item = RpcHelper.parseFavoriteItem(input.getJSONObject("favorite"));
		System.out.println("got Item class item");

		// 3. connect db and delete from history (but still in items and keywords)
		MySQLConnection connection = new MySQLConnection();
		connection.unsetFavoriteItems(userId, item.getItemId());
		connection.close();
		System.out.println("db done delete");

		RpcHelper.writeJsonObject(resp, new JSONObject().put("result", "SUCCESS"));

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("inside /history POST");
		
		
		HttpSession session = request.getSession(false);
		if(session == null) {
			response.setStatus(403);
			System.out.println("Invalid session in histrory post");
			return;
		}

		// 1. convert request body to JSON
		JSONObject input = RpcHelper.readJSONObject(request);
		String userId = input.getString("user_id");

		// 2. convert user's fav job to Item
		Item1 item = RpcHelper.parseFavoriteItem(input.getJSONObject("favorite"));
		System.out.println("got favorite item in Item class");

		// 3. connect db and save
		MySQLConnection connection = new MySQLConnection();
		connection.setFavoriteItems(userId, item);
		connection.close();
		System.out.println("back from db");

		// 4. write on response
		RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));

	}

}
