package database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
	private static final String createUserTableSQL =	
			"CREATE TABLE users ("
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "username TEXT UNIQUE NOT NULL," 	
			+ "isAdmin BOOLEAN NOT NULL);";
	private static final String insertUserSQL = 
			"INSERT INTO users (username, isAdmin) VALUES (?, ?);";
	private static final String deleteUserSQL =
			"DELETE FROM users "
			+ "WHERE username LIKE ? ;";
	private static final String selectUserSQL =
			"SELECT * FROM users "
			+ "WHERE username LIKE ?;";
	
	private static final String createFileTableSQL =
			"CREATE TABLE files ("
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "path TEXT UNIQUE NOT NULL,"
			+ "date DATE NOT NULL,"
			+ "user_id INTEGER,"
			+ "FOREIGN KEY(user_id) REFERENCES users(id));";
	private static final String insertFileSQL = 
			"INSERT INTO files (path, date, user_id) VALUES (?, ?, ?);";
	private static final String deleteFileSQL =
			"DELETE FROM files "
			+ "WHERE path LIKE ? AND user_id = ?;";
	
	private final String dbPath;
	private Connection dbConnection;
	
	public Database(String dbPath) throws IOException, ClassNotFoundException, SQLException {
		this.dbPath = dbPath;
		File f = new File("./" + this.dbPath);
		if(!f.exists()) {
			System.out.println("Database not found. Creating a new one...");
			f.createNewFile();
			this.init();
		}
	}
	
	private void init() throws ClassNotFoundException, SQLException {
		this.open();
		PreparedStatement stmt = this.dbConnection.prepareStatement(createUserTableSQL);
		stmt.execute();
		stmt = this.dbConnection.prepareStatement(createFileTableSQL);
		stmt.execute();
	}
	
	public Connection open() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		String dbURL = "jdbc:sqlite:" + this.dbPath;
		this.dbConnection = DriverManager.getConnection(dbURL);
		return this.dbConnection;
	}
	
	public void close() throws SQLException {
		this.dbConnection.close();
	}
	
	public boolean insertUser(String username) throws SQLException {
		try {
			ResultSet rs = searchUser(username);
			if(rs == null) {
				System.out.println("Search user failed");
			} else {
				rs.next();
				System.out.println("NEXT");
				System.out.println(rs.getString(2));
			}
		} catch (SQLException e) {
			PreparedStatement stmt = this.dbConnection.prepareStatement(insertUserSQL);
			stmt.setString(1, username);
			stmt.setBoolean(2, false);
			return stmt.execute();
		}
		//In case user already exists
		return false;
	}
	
	public boolean deleteUser(String username) throws SQLException {
		PreparedStatement stmt = this.dbConnection.prepareStatement(deleteUserSQL);
		stmt.setString(1, username);
		return stmt.execute();
	}
	
	/**
	 * @param username
	 * @return ResultSet pointing to the first row of results
	 * @throws SQLException
	 */
	public ResultSet searchUser(String username) throws SQLException {
		PreparedStatement stmt = this.dbConnection.prepareStatement(selectUserSQL);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		return rs;
	}
	
	public boolean insertFile(String path, Date date, String username) throws SQLException {
		ResultSet rs = searchUser(username);
		int user_id = rs.getInt("id");
		
		PreparedStatement stmt = this.dbConnection.prepareStatement(insertFileSQL);
		stmt.setString(1, path);
		stmt.setDate(2, date);
		stmt.setInt(3, user_id);
		return stmt.execute();
	}
	
	public boolean deleteFile(String path, String username) throws SQLException {
		ResultSet rs = searchUser(username);
		int user_id = rs.getInt("id");
		
		PreparedStatement stmt = this.dbConnection.prepareStatement(deleteFileSQL);
		stmt.setString(1, path);
		stmt.setInt(2, user_id);
		return stmt.execute();
	}
	
	public boolean updateFile(String path, Date date, String username) throws SQLException{
		boolean delete = this.deleteFile(path, username);
		Date data = new Date(System.currentTimeMillis());
		boolean insert = this.insertFile(path, data, username);
		return delete && insert;
	}
	
	public ResultSet searchFile() {
		return null;
	}
	
	//TODO 	OU usamos SQL diretamente
	//		OU usamos preparedStatements, e depois temos de dar set aos "?"
	//VER: http://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html
	
	//Para correr java -classpath ".:sqlite-jdbc-3.16.1.jar" Client
	
	
}
