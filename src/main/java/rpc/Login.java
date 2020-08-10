package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import db.MySQLConnection;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// params: true -> create new session if no cur session, false -> no create new and return null
		HttpSession session = request.getSession(false);
		JSONObject obj = new JSONObject();
		if(session != null) {
			MySQLConnection connection = new MySQLConnection();
			String userId = session.getAttribute("user_id").toString();
			obj.put("staus", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
			connection.close();
		}else {
			obj.put("status", "Invaild Session");
			response.setStatus(403);
		}
		RpcHelper.writeJsonObject(response, obj);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("inside login");
		JSONObject input = RpcHelper.readJSONObject(request);
		String userId = input.getString("user_id");
		String password = input.getString("password");
		System.out.println("input received: userId: " + userId + "pw: " + password);
		
		
		MySQLConnection connection = new MySQLConnection();
		JSONObject obj = new JSONObject();
		if(connection.verifyLogin(userId, password)) {
			HttpSession session = request.getSession();
			session.setAttribute("user_id", userId);
			session.setMaxInactiveInterval(3600);
			obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
			System.out.println("login success from db in backend");
		}else {
			obj.put("status", "User doesn't exist");
			response.setStatus(401);
			System.out.println("login failed from db in backend");
		}
		connection.close();
		RpcHelper.writeJsonObject(response, obj);
	}

}
