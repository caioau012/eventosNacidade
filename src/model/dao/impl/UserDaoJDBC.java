package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.UserDao;
import model.entities.Event;
import model.entities.User;

public class UserDaoJDBC implements UserDao {

	private Connection conn;
	
	public UserDaoJDBC(Connection conn) {
		this.conn=conn;
	}
	
	@Override
	public void insert(User obj) {
		PreparedStatement st = null;
		ResultSet rs = null;
		PreparedStatement stAssoc = null;
		try {
			st = conn.prepareStatement("INSERT INTO users (Name, Cpf, Email, University, Age) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getName());
			st.setString(2, obj.getCpf());
			st.setString(3, obj.getEmail());
			st.setString(4, obj.getUniversity());
			st.setInt(5, obj.getAge());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				rs = st.getGeneratedKeys();
				if (rs.next()) {
					int userId = rs.getInt(1);
					obj.setId(userId);
				}
			}
			else {
				throw new DbException("Unexpected error! No rows affected!");
			}
			if (obj.getEvents() != null && !obj.getEvents().isEmpty()) {
				stAssoc = conn.prepareStatement("INSERT INTO user_event (user_id, event_id) VALUES (?, ?)");	
				for (Event e:obj.getEvents()) {
					stAssoc.setInt(1,  obj.getId());
					stAssoc.setInt(2,  e.getId());
					stAssoc.executeUpdate();
					DB.closeStatement(stAssoc);
				}
			}
		}
		catch (SQLException e) {
			throw new DbException("Unexpected Error! Error: " + e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		
		}
	}

	@Override
	public void update(User obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE users SET Name = ?, Cpf = ?, Email = ?, University = ?, Age = ? WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getCpf());
			st.setString(3, obj.getEmail());
			st.setString(4, obj.getUniversity());
			st.setInt(5, obj.getAge());
			st.setInt(6, obj.getId());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected == 0) {
				throw new DbException("No user found with given ID: " + obj.getId());
			}
		}
		catch (SQLException e) {
			throw new DbException("Error updating user: " + e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM users WHERE Id = ?");
			
			st.setInt(1, id);
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected == 0) {
				throw new DbException("No user found with the given Id: " + id);
			}
		}
		catch (SQLException e) {
			throw new DbException("Error deleting user: " + e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public User findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try{
			st = conn.prepareStatement("SELECT * FROM users WHERE Id = ?");
			st.setInt(1,  id);
			rs = st.executeQuery();
			
			if (rs.next()) {
				User user = instantiateUser(rs);
				return user;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException("Error finding user: " + e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	
	private User instantiateUser(ResultSet rs) throws SQLException{
		User user = new User();
		user.setId(rs.getInt("Id"));
		user.setName(rs.getString("Name"));
		user.setCpf(rs.getString("Cpf"));
		user.setEmail(rs.getString("Email"));
		user.setUniversity(rs.getString("University"));
		user.setAge(rs.getInt("Age"));
		return user;
	}

	@Override
	public List<User> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM users ORDER BY Name");
			rs = st.executeQuery();
			
			List<User> list = new ArrayList<>();
			while (rs.next()) {
				User user = instantiateUser(rs);
				list.add(user);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException ("Error finding users: " + e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
		
	}

	@Override
	public List<User> findByEvent(Event event) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT u.* FROM users u INNER JOIN user_event ue ON u.Id = ue.user_id WHERE ue.event_id = ? ORDER BY u.Name");
			st.setInt(1,  event.getId());
			rs = st.executeQuery();
			
			List<User> list = new ArrayList<>();
			while (rs.next()) {
				User user = instantiateUser(rs);
				list.add(user);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException("Error finding users by event: " + e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}
	
	public List<Event> findEventsByUser(Integer userId){
		PreparedStatement st = null;
		ResultSet rs = null;
		List<Event> list = new ArrayList<>();
		try {
			st = conn.prepareStatement("SELECT e.Id, e.Name, e.Date FROM event e INNER JOIN user_event ue ON e.Id = ue.event_id WHERE ue.user_id = ?");
			st.setInt(1,  userId);
			rs = st.executeQuery();
			while (rs.next()) {
				Event e = new Event();
				e.setId(rs.getInt("Id"));
				e.setEventName(rs.getString("Name"));
				e.setDate(rs.getTimestamp("Date").toLocalDateTime());
				list.add(e);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	
	public void addUserToEvent(Integer userId, Integer eventId) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO user_event (user_id, event_id) VALUES (?, ?)");
			st.setInt(1, userId);
			st.setInt(2, eventId);
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}
	
	public void removeUserFromEvent(Integer userId, Integer eventId) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM user_event WHERE user_id = ? AND event_id = ?");
			st.setInt(1,  userId);
			st.setInt(2, eventId);
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}
}
