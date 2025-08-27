package model.dao;

import db.DB;
import model.dao.impl.EventDaoJDBC;
import model.dao.impl.UserDaoJDBC;

public class DaoFactory {
	public static UserDao createUserDao() {
		return new UserDaoJDBC(DB.getConnection());
	}
	
	public static EventDao createEventDao() {
		return new EventDaoJDBC(DB.getConnection());
	}
		
}
