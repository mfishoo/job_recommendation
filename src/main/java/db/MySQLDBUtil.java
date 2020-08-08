package db;

public class MySQLDBUtil {
	// db2
	private static final String INSTANCE = "job-recommendation.cxmqbaeboy9z.us-east-2.rds.amazonaws.com";
	private static final String PORT_NUM = "3306";
	public static final String DB_NAME = "job_recommendation";
	private static final String USERNAME = "mfishoo";
	private static final String PASSWORD = "job-recommendation";
	public static final String URL = "jdbc:mysql://" + INSTANCE + ":" + PORT_NUM + "/" + DB_NAME + "?user=" + USERNAME
			+ "&password=" + PASSWORD + "&autoReconnect=true&serverTimezone=UTC";

	// db1
//	private static final String INSTANCE = "laiproject-instance.cxmqbaeboy9z.us-east-2.rds.amazonaws.com";
//	private static final String PORT_NUM = "3306";
//	public static final String DB_NAME = "laiproject";
//	private static final String USERNAME = "admin";
//	private static final String PASSWORD = "laiprojectadmin";
//	public static final String URL = "jdbc:mysql://" + INSTANCE + ":" + PORT_NUM + "/" + DB_NAME + "?user=" + USERNAME
//			+ "&password=" + PASSWORD + "&autoReconnect=true&serverTimezone=UTC";

}
