package cse3040_FP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	public boolean checkIdLegality(String Id) {
		char c;
		
		for(int i = 0; i<Id.length(); i++) {
			c = Id.charAt(i);
			if('a' > c || c > 'z') {
				if('0' > c || c > '9') {
					System.out.println("UserID must be a single word with lowercase alphabets and numbers.");
					return false;
				}
			}
		}
		return true;
	}
	
	public void menu(String cmd, DataInputStream in, DataOutputStream out) {
		Scanner scanner = new Scanner(System.in);
		String temp;
		int currentBorrowingBook = 0;
		int containStringBook = 0;
		
		try {
			out.writeUTF(cmd);
		}catch(Exception e) {}
		
		if(cmd.equals("add")) {
			try {
				System.out.print("add-title> ");
				temp = scanner.nextLine().trim();
				if(temp.equals("")) {out.writeUTF("!blanc");return;}
				out.writeUTF(temp);

				System.out.print("add-author> ");
				temp = scanner.nextLine().trim();
				if(temp.equals("")) {out.writeUTF("!blanc");return;}
				out.writeUTF(temp);
				
				System.out.println(in.readUTF());
			}catch(IOException e) {}
		}
		
		else if(cmd.equals("borrow")) {
			try {
				System.out.print("borrow-title> ");
				temp = scanner.nextLine().trim();
				if(temp.equals("")) {out.writeUTF("!blanc");return;}
				out.writeUTF(temp);
			
				System.out.println(in.readUTF());
			}catch(IOException e) {}
		}
		
		else if(cmd.equals("return")) {
			try {
				System.out.print("return-title> ");
				temp = scanner.nextLine().trim();
				if(temp.equals("")) {out.writeUTF("!blanc");return;}
				out.writeUTF(temp);
			
				System.out.println(in.readUTF());
			}catch(IOException e) {}
		}
		
		else if(cmd.equals("info")) {
			try {
				System.out.println(in.readUTF());
				currentBorrowingBook = Integer.parseInt(in.readUTF());
				
				for(int i = 0; i < currentBorrowingBook; i++)
					System.out.println(in.readUTF());
				
			}catch(IOException e) {}
		}
		
		else if(cmd.equals("search")) {
			
			while(true) {
				try {
				System.out.print("search-string> ");
				temp = scanner.nextLine();
				if(temp.equals("")) {out.writeUTF("!blanc");return;}
				
				else if(temp.length() < 3) {
					System.out.println("Search string must be longer than 2 characters.");
					continue;
				}
				
				else {
					out.writeUTF(temp);
					break;
				}
				}catch(IOException e) {}
			}
			
			try {
				System.out.println(in.readUTF());
				containStringBook = Integer.parseInt(in.readUTF());
				
				for(int i = 0; i < containStringBook; i++)
					System.out.println(in.readUTF());
			}catch(IOException e) {}
		}
		
		else if(cmd.equals("")) {return;}
		
		else {
			System.out.println("[available commands]");
			System.out.println("add: add a new book to the list of books.");
			System.out.println("borrow: borrow a book from the library.");
			System.out.println("return: return a book to the library.");
			System.out.println("info: show list of books I am currently borrowing.");
			System.out.println("search: search for books.");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		if(args.length != 2) {
			System.out.println("Please give the IP address and port number as an arguments.");
			System.exit(0);
		}
		
		Client client = new Client();
		String userID = null;
		String command = null;
		boolean R6 = false;
		Scanner scan = new Scanner(System.in);
		
		try {
			String serverIp = args[0];
			int portNum = Integer.parseInt(args[1]);
			Socket socket = new Socket(serverIp, portNum);
			
			DataOutputStream out;
			DataInputStream in;
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			
			while(!R6) {
				System.out.print("Enter userID>> ");
				userID = scan.nextLine();
				R6 = client.checkIdLegality(userID);
			}
			System.out.println("Hello " + userID +'!');
			out.writeUTF(userID);
			
			while(true) {
				System.out.print(userID + ">> ");
				command = scan.nextLine().trim();
				client.menu(command, in, out);
			}
			
		}catch(Exception e) {
			System.out.println("\nConnection establishment failed.");
			System.exit(0);
		}
		
	}

}
