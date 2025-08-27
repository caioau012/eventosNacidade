package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import model.dao.DaoFactory;
import model.dao.EventDao;
import model.dao.UserDao;
import model.entities.Event;
import model.entities.EventType;
import model.entities.User;

public class Program {
	
	private static final String FILE_NAME = "events.data";
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
	
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		UserDao userDao = DaoFactory.createUserDao();
		EventDao eventDao = DaoFactory.createEventDao();
		
		loadEventsFromFile(eventDao);
		
		System.out.println("=== SISTEMA DE EVENTOS ===");
		System.out.println("Digite seu nome: ");
		String name = sc.nextLine();
		System.out.println("Digite seu CPF: ");
		String cpf = sc.nextLine();
		System.out.println("Digite seu email: ");
		String email = sc.nextLine();
		System.out.println("Digite sua universidade: ");
		String university = sc.nextLine();
		System.out.println("Digite sua idade: ");
		int age = sc.nextInt();
		sc.nextLine();
		
		User user = new User(name, cpf, email, university, age, new HashSet<>());
		userDao.insert(user);
		
		int option;
		do {
			System.out.println("\n=== MENU ===");
	        System.out.println("1 - Cadastrar evento");
	        System.out.println("2 - Listar eventos disponíveis");
	        System.out.println("3 - Confirmar participação em evento");
	        System.out.println("4 - Cancelar participação");
	        System.out.println("5 - Ver eventos confirmados");
	        System.out.println("6 - Salvar eventos no arquivo");
	        System.out.println("0 - Sair");
	        System.out.print("Opção: ");
	        option = sc.nextInt();
	        sc.nextLine();
	        
	        switch (option) {
	        case 1:
	        	cadastrarEvento(sc, eventDao);
	        	break;
	        case 2:
	        	listarEventos(eventDao);
	        	break;
	        case 3:
	        	confirmarParticipacao(sc, user, eventDao);
	        	break;
	        case 4:
	        	cancelarParticipacao(sc, user);
	        	break;
	        case 5:
	        	listarEventosConfirmados(user);
	        	break;
	        case 6:
	        	saveEventsToFile(eventDao);
	        	break;
	        case 0:
	        	saveEventsToFile(eventDao);
	        	System.out.println("Saindo...");
	        	break;
	        default:
	        	System.out.println("Opção inválida!");
	        }
		}
		while (option !=0);
		
		sc.close();
	}
	
	private static void cadastrarEvento(Scanner sc, EventDao eventDao) {
		System.out.println("Nome do evento: ");
		String name = sc.nextLine();
		System.out.print("Endereço: ");
		String address = sc.nextLine();
		System.out.print("Descrição: ");
		String description = sc.nextLine();
		System.out.println("Categoria (CULTURAL, ESPORTIVO, ACADEMICO, OUTRO): ");
		EventType type = EventType.valueOf(sc.nextLine().toUpperCase());
		System.out.print("Data e hora (yyyy-MM-ddTHH:mm): ");
		String dateTimeStr = sc.nextLine();
		LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, FORMATTER);
		
		Event event = new Event(type, dateTime, name, address, description);
		eventDao.insert(event);
		System.out.println("Evento cadastrado com sucesso!");		
	}
	
	private static void listarEventos(EventDao eventDao) {
		
		List<Event> events = eventDao.findAll();
		if (events.isEmpty()) {
			System.out.println("Nenhum evento cadastrado.");
			return;
		}
		
		events.sort(Comparator.comparing(Event::getDate));
		
		System.out.println("\n=== EVENTOS ===");
		LocalDateTime now = LocalDateTime.now();
		for(Event e :events) {
			String status;
			if(e.getDate().isBefore(now)) {
				status = "[JÁ OCORREU]";
			}
			else if (e.getDate().isAfter(now)) {
				status = "[FUTURO]";
			}
			else {
				status = "[OCORRENDO AGORA]";
			}
			System.out.println(e.getEventName() + " - " + e.getType() + " - " + e.getDate().format(FORMATTER) + " " + status);
		}
	}
	
	private static void confirmarParticipacao(Scanner sc, User user, EventDao eventDao) {
		List<Event> events = eventDao.findAll();
		if (events.isEmpty()) {
			System.out.println("Nenhum evento disponível.");
			return;
		}
		listarEventos(eventDao);
		System.out.println("Digite o nome do evento para confirmar: ");
		String eventName = sc.nextLine();
		
		Event event = events.stream().filter(e -> e.getEventName().equalsIgnoreCase(eventName)).findFirst().orElse(null);
		
		if (event != null) {
			user.getEvents().add(event);
			System.out.println("Participação confirmada!");
		}
		else {
			System.out.println("Evento não encontrado.");
		}
	}
	
	private static void cancelarParticipacao(Scanner sc, User user) {
		if(user.getEvents().isEmpty()) {
			System.out.println("Você não confirmou nenhum evento.");
			return;
		}
		listarEventosConfirmados(user);
		System.out.println("Digite o nome do evento para cancelar: ");
		String eventName = sc.nextLine();
		
		boolean removed = user.getEvents().removeIf(e -> e.getEventName().equalsIgnoreCase(eventName));
		
		if (removed) {
			System.out.println("Participação cancelada.");
		}
		else {
			System.out.println("Evento não encontrado na sua lista.");
		}
	}
	
	private static void listarEventosConfirmados(User user) {
		if (user.getEvents().isEmpty()) {
			System.out.println("Nenhum evento confirmado.");
			return;
		}
		System.out.println("\n=== EVENTOS CONFIRMADOS ===");
		for (Event e : user.getEvents()) {
			System.out.println(e.getEventName() + " - " + e.getDate().format(FORMATTER));
		}
	}
	
	private static void loadEventsFromFile(EventDao eventDao) {
		try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))){
			String line;
			while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                EventType type = EventType.valueOf(fields[0].toUpperCase());
                LocalDateTime dateTime = LocalDateTime.parse(fields[1], FORMATTER);
                String name = fields[2];
                String address = fields[3];
                String description = fields[4];
                

                Event event = new Event(type, dateTime, name, address, description);
                eventDao.insert(event);
			}
            System.out.println("Eventos carregados do arquivo.");
        } 
		catch (FileNotFoundException e) {
            System.out.println("Arquivo de eventos não encontrado. Será criado um novo ao salvar.");
        } 
		catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        }
	}
	
	private static void saveEventsToFile(EventDao eventDao) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Event e : eventDao.findAll()) {
                bw.write(e.getType() + "," + e.getDate().format(FORMATTER) + "," + e.getEventName() + "," +
                        e.getAddress() + "," + e.getDescription());
                bw.newLine();
            }
            System.out.println("Eventos salvos no arquivo.");
        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }
}
