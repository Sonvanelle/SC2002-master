package presentation;

import java.util.Scanner;

/**
 * Main driver class that allows the user to choose between the admin and
 * moviegoer modes
 * depending on the user's choice, it calls
 * methods from the appropriate controller class.
 */
public class AppView {
	public static void main(String[] args) {
		int option;
		Scanner sc = new Scanner(System.in);

		do {
			System.out.println(
					"MOBLIMA \n" +
							"------------\n" +
							"1. Movie-Goer \n" +
							"2. Staff \n" +
							"3. Quit \n" +
							"------------");
			System.out.println("Enter option: ");

			while (!sc.hasNextInt()) {
				System.out.println("Please input a number value.");
				sc.next();
			}

			option = sc.nextInt();
			sc.nextLine();

			switch (option) {
				case 1:
					MovieGoerView customerview = new MovieGoerView();
					customerview.printMenu();
					break;

				case 2:
					while (true) {
						System.out.println("Enter admin password:");
						String password = sc.nextLine();
						String adminpass = "test";

						if (password.equals(adminpass)) {
							System.out.println("Logging in... Entering admin mode.");
							AdminView adminview = new AdminView();
							adminview.printMenu();
						}

						else {
							System.out.println(password + " is wrong. Type 1 to try again, 2 to enter as Movie-Goer.");
							int pwoption = sc.nextInt();
							sc.nextLine();
							if (pwoption > 2 | pwoption < 1) {
								System.out.println("Invalid; trying login again.");
								continue;
							} else if (pwoption == 1) {
								continue;
							} else {
								MovieGoerView custview = new MovieGoerView();
								custview.printMenu();
								break;
							}
						}
						break;
					}

			}
		} while (option != 3);

		System.out.println("Exiting...");
	}
}
