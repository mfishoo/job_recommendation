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
 * Servlet implementation class Register
 */
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }



	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("inside register");
		JSONObject input = RpcHelper.readJSONObject(request);
		String userId = input.getString("user_id");
		String password = input.getString("password");
		String firstname = input.getString("first_name");
		String lastname = input.getString("last_name");

		MySQLConnection connection = new MySQLConnection();
		JSONObject obj = new JSONObject();
		if (connection.addUser(userId, password, firstname, lastname)) {
			
			// if success login directly
			HttpSession session = request.getSession();
			session.setAttribute("user_id", userId);
			session.setMaxInactiveInterval(3600);
			obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
			System.out.println("register success from db in backend");

		} else {
			obj.put("status", "User Already Exists");
		}
		connection.close();
		RpcHelper.writeJsonObject(response, obj);
	}

}
