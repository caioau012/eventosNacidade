package model.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private String cpf;
	private String email;
	private String university;
	private int age;
	private Set<Event> events = new HashSet<>();
	
	public User() {
	}

	public User(String name, String cpf, String email, String university, int age, Set<Event> events) {
		this.name = name;
		this.cpf = cpf;
		this.email = email;
		this.university = university;
		this.age = age;
		this.events = events;
	}
	
	public User(Integer id, String name, String cpf, String email, String university, int age, Set<Event> events) {
		this.id = id;
		this.name = name;
		this.cpf = cpf;
		this.email = email;
		this.university = university;
		this.age = age;
		this.events = events;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUniversity() {
		return university;
	}

	public void setUniversity(String university) {
		this.university = university;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Set<Event> getEvents() {
		return events;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cpf, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(cpf, other.cpf) && Objects.equals(id, other.id);
	}
	
	
	
	

}
