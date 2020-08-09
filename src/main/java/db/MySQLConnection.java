package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import entity.Item1;

public class MySQLConnection {

	private Connection conn;

	// establish connection when new an instance
	public MySQLConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// close connection
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

//	save item on items, keywords, history tables
	public void setFavoriteItems(String userId, Item1 item) {
		System.out.println("inside set Fav items ");
		if (conn == null) {
			System.err.println("db connection failed");
			return;
		}
		// 1. save to items & keywords table, so when delete in histroy table, still
		// have records in items & keywords??
		saveItem(item);

		// 2. save to history (fav jobs)
		String sql = "INSERT INTO history (user_id, item_id) VALUES (?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, item.getItemId());
			statement.executeUpdate();
			System.out.println("saved item in history table");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

// delete records in history
	public void unsetFavoriteItems(String userId, String itemId) {
		System.out.println("inside unset fav");
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}

		String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, itemId);
			statement.executeUpdate();
			System.out.println("delete done in unset fav");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

//	save to items & keywords table
	public void saveItem(Item1 item) {
		System.out.println("inside save item");
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}

		String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?)";

		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item.getItemId());
			statement.setString(2, item.getName());
			statement.setString(3, item.getAddress());
			statement.setString(4, item.getImageUrl());
			statement.setString(5, item.getUrl());
			statement.executeUpdate();

			sql = "INSERT IGNORE INTO keywords VALUES (?, ?)";
			statement = conn.prepareStatement(sql);
			statement.setString(1, item.getItemId());
			for (String keyword : item.getKeywords()) {
				statement.setString(2, keyword);
				statement.executeUpdate();
			}
			System.out.println("saved fav items in item & keyword table");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// get items ids form history table
	public Set<String> getFavoriteItemsIds(String userId) {
		System.out.println("inside get fav item ids");
		if (conn == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}

		Set<String> favoriteItems = new HashSet<>();

		try {
			String sql = "SELECT item_id FROM history WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String itemId = rs.getString("item_id");
				favoriteItems.add(itemId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favoriteItems;
	}

	// read from items table
	public Set<Item1> getFavoriteItems(String userId) {
		System.out.println("inside get fav items");
		if (conn == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}

		Set<Item1> favoriteItems = new HashSet<>();
		Set<String> favoriteItemIds = getFavoriteItemsIds(userId);
		System.out.println("got fav items ids from history table");

		String sql = "SELECT * FROM items WHERE item_id = ?";

		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			for (String itemId : favoriteItemIds) {
				statement.setString(1, itemId);
				ResultSet rs = statement.executeQuery();

				if (rs.next()) {
					Item1 item = Item1.builder().itemId(rs.getString("item_id")).name(rs.getString("name"))
							.address(rs.getString("address")).imageUrl(rs.getString("image_url"))
							.url(rs.getString("url")).keywords(getKeywords(itemId)).build();
					favoriteItems.add(item);
				}
			}
			System.out.println("got db results");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favoriteItems;
	}

	// read from keywords table
	public Set<String> getKeywords(String itemId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return null;
		}
		Set<String> keywords = new HashSet<>();
		String sql = "SELECT keyword from keywords WHERE item_id = ?";

		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, itemId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				keywords.add(rs.getString("keyword"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return keywords;
	}

//	get user fullname
	public String getFullname(String userId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return null;
		}
		String name = "";
		String sql = "SELECT first_name, last_name FROM users WHERE user_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			if(rs.next()) {
				name = rs.getString("first_name")+ " " + rs.getString("last_name");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return name;
	}

	// verify password matches with username
	public boolean verifyLogin(String userId, String password) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		String sql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			ResultSet rs = statement.executeQuery();
			if(rs.next()) {
				return true;
			}
					
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

// add new user to db
	public boolean addUser(String userId, String password, String firstname, String lastname) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		String sql = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			statement.setString(3, firstname);
			statement.setString(4, lastname);
			
			// return 1: add 1 row; 0: add nothing
			return statement.executeUpdate() == 1;
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		MySQLConnection test = new MySQLConnection();
		
	}
}
