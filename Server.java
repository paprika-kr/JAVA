package cse3040_FP;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class Book implements Comparable<Book> {
	String title;
	String author;
	boolean isAvailable; 
	String ID;
	
	public Book() {
		this.title = null;
		this.author = null;
		this.ID = "-";
		this.isAvailable = false;
	}
	
	public Book(String t, String a, String i) {
		this.title = t;
		this.author = a;
		if(i.equals("-")) {
			this.isAvailable = true;
			this.ID = "-";
		}
		else {
			this.isAvailable = false;
			this.ID = i;
		}
	}
	
	public int compareTo(Book b) {
		return this.title.compareTo(b.getTitle());
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getAuthor() {
		return this.author;
	}
	
	public boolean getIsAvailable() {
		return this.isAvailable;
	}
	
	public String getID() {
		return this.ID;
	}
	
	public void setID(String i) {
		if(i.equals("-")) {
			this.isAvailable = true;
			this.ID = "-";
		}
		else {
			this.isAvailable = false;
			this.ID = i;
		}
	}
}

public class Server {
	HashMap<String, Book> books;
	List<String> sortedList = new ArrayList<>();

	Server(){
		books = new HashMap<>();
		Collections.synchronizedMap(books);
	}		

	public void readTxt(String input){
		BufferedReader br = null;
		String line;
		String[] split;
		
		try {
			br = new BufferedReader(new FileReader(input));
			} catch(FileNotFoundException e){	
				FileWriter writer = null;
		        try {
		            writer = new FileWriter(input, true);
		            }catch(Exception ee) {}
		        
		        try {
	                if(writer != null) writer.close();
	            } catch(IOException ee) {
	                e.printStackTrace();
	            }
			}

			while(true) {
				try {
					if(br == null) break;
					line = br.readLine();		
					if(line == null) break;
					split = line.split("\t");
					Book temp = new Book(split[0], split[1], split[2]);
					books.put(split[0].toLowerCase(), temp);
					
				} catch(Exception e) {}
			}
			
			try {
				if(br != null) {
					br.close();
					sortedList.addAll(books.keySet());
					}
					
			} catch(IOException e) {
				System.out.println("Input file closing error.");
			}
		}
	
	public void updateTxt() {
		try {
			PrintWriter pw = new PrintWriter("books.txt");
			sortedList.clear();
			sortedList.addAll(books.keySet());
			Collections.sort(sortedList, String.CASE_INSENSITIVE_ORDER);
			for(String key : sortedList) {
				pw.println(books.get(key).getTitle() + "\t" + books.get(key).getAuthor() + "\t" + books.get(key).getID());
			}
			pw.close();
		}catch(IOException e) {}
	}
	
	public void menu(String cmd, DataInputStream in, DataOutputStream out, String ID) {
		String title;
		String author;
		String str;

		int currentBorrowingBook = 0;
		int containStringBook = 0;
		int index = 0;
		
		if(cmd.equals("add")) {
			try {
				title = in.readUTF();
				if(title.equals("!blanc")) return;
				author = in.readUTF();
				if(author.equals("!blanc")) return;
				
				if(books.containsKey(title.toLowerCase())) {
					out.writeUTF("The book already exists in the list.");
				}
				else {
					Book book = new Book(title, author, "-");
					books.put(title.toLowerCase(), book);
					out.writeUTF("A new book added to the list.");
					
					updateTxt();
				}
			}catch(IOException e) {}
		}
		
		else if(cmd.equals("borrow")) {
			try {
				title = in.readUTF().toLowerCase();
				if(title.equals("!blanc")) return;
				
				if(books.containsKey(title)) {
					if(books.get(title).isAvailable) {
						books.get(title).setID(ID);
						out.writeUTF("You borrowed a book. - " + books.get(title).getTitle());
						
						updateTxt();
					}
					else
						out.writeUTF("The book is not available.");
				}
				else 
					out.writeUTF("The book is not available.");
			}catch(IOException e) {}
			
		}
		
		else if(cmd.equals("return")) {
			try {
				title = in.readUTF().toLowerCase();
				if(title.equals("!blanc")) return;
				
				if(books.containsKey(title)) {
					if(books.get(title).getID().equals(ID)) {
						books.get(title).setID("-");
						out.writeUTF("You returned a book. - " + books.get(title).getTitle());
						
						updateTxt();
					}
					else
						out.writeUTF("You did not borrow the book.");
				}
				else 
					out.writeUTF("You did not borrow the book.");
			}catch(IOException e) {}
		}
		
		else if(cmd.equals("info")) {
			try {
				currentBorrowingBook = 0;
				index = 1;
				
				for(String key : sortedList) {
					if(books.get(key).getID().equals(ID)) currentBorrowingBook++;
				}
				
				if(currentBorrowingBook < 2)
					out.writeUTF("You are currently borrowing " + currentBorrowingBook + " book :");
				else out.writeUTF("You are currently borrowing " + currentBorrowingBook + " books :");
				
				out.writeUTF(Integer.toString(currentBorrowingBook));
				
				for(String key : sortedList) {
					if(books.get(key).getID().equals(ID))
						out.writeUTF((index++) + ". " + books.get(key).getTitle() + ", " + books.get(key).getAuthor());
				}
				
			}catch(IOException e) {}
			
		}
		
		else if(cmd.equals("search")) {
			try {
				index = 1;
				str = in.readUTF().toLowerCase();
				if(str.equals("!blanc")) return;
				
				for(String key : sortedList) {
					if(key.contains(str)) containStringBook++;
				}
				
				if(containStringBook < 2)
					out.writeUTF("Your search matched " + containStringBook + " result.");
				else out.writeUTF("Your search matched " + containStringBook + " results.");
				
				out.writeUTF(Integer.toString(containStringBook));
				
				for(String key : sortedList) {
					if(key.contains(str))
						out.writeUTF((index++) + ". " + books.get(key).getTitle() + ", " + books.get(key).getAuthor());
				}
			}catch(IOException e) {}
		}
		
		else {}
	}
	
	class ServerReceiver extends Thread{
		Socket socket;
		DataInputStream in;
		DataOutputStream out;
		String userID;
		ServerReceiver(Socket socket){
			this.socket = socket;
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				userID = in.readUTF();
			}catch(IOException e) {}
			
		}
		
		public void run() {
			String cmd = "";
			while(true) {
				try {
					cmd = in.readUTF();
					menu(cmd, in, out, userID);
				}catch(IOException e) {}
			}
		}
	}
	
	public void start(int port) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		try {
			serverSocket = new ServerSocket(port);
			System.out.println(serverSocket.getInetAddress());
			System.out.println("server has started.");
			while(true) {
				socket = serverSocket.accept();
				ServerReceiver thread = new ServerReceiver(socket);
				thread.start();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if(args.length != 1) {
			System.out.println("Please give the port number as an argument.");
			System.exit(0);
		}
		int portNum = Integer.parseInt(args[0]);
		Server server = new Server();
		server.readTxt("books.txt");
		server.start(portNum);
	}
	
	class BooklistUpdater extends Thread{
		
	}

}
