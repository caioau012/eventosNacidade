package model.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

public class Event implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private EventType type;
	private LocalDateTime localDateTime;
	private String eventName;
	private String address;
	private String description;
	
	public Event() {
	}

	public Event(EventType type, LocalDateTime localDateTime, String eventName, String address, String description) {
		this.type = type;
		this.localDateTime = localDateTime;
		this.eventName = eventName;
		this.address = address;
		this.description = description;
	}
	
	public Event(Integer id, EventType type, LocalDateTime localDateTime, String eventName, String address, String description) {
		this.id = id;
		this.type = type;
		this.localDateTime = localDateTime;
		this.eventName = eventName;
		this.address = address;
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public LocalDateTime getDate() {
		return localDateTime;
	}

	public void setDate(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return Objects.hash(eventName, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		return Objects.equals(eventName, other.eventName) && Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Event [type=" + type + ", date=" + localDateTime + ", eventName=" + eventName + ", address=" + address
				+ ", description=" + description + "]";
	}
}
