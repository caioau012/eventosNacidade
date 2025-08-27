package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.EventDao;
import model.entities.Event;
import model.entities.EventType;

public class EventDaoJDBC implements EventDao{
	   
    private Connection conn;

    public EventDaoJDBC(Connection conn){
        this.conn=conn;
    }

    @Override
    public Event findById(Integer id){
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = conn.prepareStatement("SELECT * FROM event WHERE Id = ?");
            st.setInt(1, id);
            rs = st.executeQuery();
            if(rs.next()){
                Event obj = new Event();
                obj.setId(rs.getInt("Id"));
                obj.setEventName(rs.getString("Name"));
                obj.setType(EventType.valueOf(rs.getString("EventType")));
                obj.setAddress(rs.getString("Address"));
                obj.setDescription(rs.getString("Description"));
                obj.setDate(rs.getTimestamp("Date").toLocalDateTime());
                return obj;
            }
            return null;
        }
        catch(SQLException e){
            throw new DbException(e.getMessage());
        }
        finally{
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Event> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = conn.prepareStatement("SELECT * FROM event ORDER BY Date");
            rs = st.executeQuery();
           
            List<Event> list = new ArrayList<>();

            while (rs.next()){
                Event obj = new Event();
                obj.setId(rs.getInt("Id"));
                obj.setEventName(rs.getString("Name"));
                obj.setType(EventType.valueOf(rs.getString("EventType")));
                obj.setAddress(rs.getString("Address"));
                obj.setDescription(rs.getString("Description"));
                obj.setDate(rs.getTimestamp("Date").toLocalDateTime());
                list.add(obj);
            }
            return list;
        }
        catch (SQLException e){
            throw new DbException (e.getMessage());
        }
        finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public void insert(Event obj){
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = conn.prepareStatement("INSERT INTO event (Name, Address, Description, EventType, Date) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            st.setString(1, obj.getEventName());
            st.setString(2, obj.getAddress());
            st.setString(3, obj.getDescription());
            st.setString(4, obj.getType().toString());
            st.setTimestamp(5, Timestamp.valueOf(obj.getDate()));

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0){
                rs = st.getGeneratedKeys();
                if (rs.next()){
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
            }
            else{
                throw new DbException("Unexpected error! No rows affected");  
            }
        }
        catch (SQLException e){
            throw new DbException(e.getMessage());
        }
        finally{
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public void update(Event obj){
        PreparedStatement st = null;
        try{
            st = conn.prepareStatement("UPDATE event SET Name = ? Address = ? Description = ? EventType = ? Date = ? WHERE Id = ?");

            st.setString(1, obj.getEventName());
            st.setString(2, obj.getAddress());
            st.setString(3, obj.getDescription());
            st.setString(4, obj.getType().toString());
            st.setTimestamp(5, Timestamp.valueOf(obj.getDate()));

            st.executeUpdate();
        }
        catch(SQLException e){
            throw new DbException(e.getMessage());
        }
        finally{
            DB.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id){
        PreparedStatement st = null;
        try{
            st = conn.prepareStatement("DELETE FROM evet WHERE Id = ?");

            st.setInt(1, id);

            st.executeUpdate();
        }
        catch(SQLException e){
            throw new DbIntegrityException(e.getMessage());
        }
        finally{
            DB.closeStatement(st);
        }
    }

    @Override
    public List<Event> findPastEvents(){
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = conn.prepareStatement("SELECT * FROM event WHERE event_date < CURRENT_DATE ORDER BY event_date DESC");
            rs=st.executeQuery();
            List<Event> list = new ArrayList<>();

            while (rs.next()){
                Event obj = instantiateEvent(rs);
                list.add(obj);
            }
            return list;
        }
        catch (SQLException e){
            throw new DbException(e.getMessage());
        }
        finally{
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }
   
    @Override
    public List<Event> findCurrentEvents(){
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = conn.prepareStatement("SELECT * FROM event WHERE event_date = CURRENT_DATE ORDER BY event_date");
            rs = st.executeQuery();
            List<Event> list = new ArrayList<>();

            while (rs.next()){
                Event obj = instantiateEvent(rs);
                list.add(obj);
            }
            return list;
        }
        catch(SQLException e){
            throw new DbException(e.getMessage());
        }
        finally{
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    public Event instantiateEvent(ResultSet rs) throws SQLException{
        Event obj = new Event();
        obj.setId(rs.getInt("Id"));
        obj.setEventName(rs.getString("Name"));
        obj.setAddress(rs.getString("Address"));
        obj.setDescription(rs.getString("Description"));
        obj.setDate(rs.getTimestamp("Date").toLocalDateTime());
        obj.setType(EventType.valueOf(rs.getString("EventType")));
        return obj;
    }

}


