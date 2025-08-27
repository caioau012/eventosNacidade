package model.dao;

import java.util.List;

import model.entities.Event;

public interface EventDao {
	void insert (Event obj);
	void update (Event obj);
	void deleteById(Integer id);
	Event findById(Integer id);
	List<Event> findAll();
	List<Event> findPastEvents();
	List<Event> findCurrentEvents();

}
