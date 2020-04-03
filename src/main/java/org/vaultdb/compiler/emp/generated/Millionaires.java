package org.vaultdb.compiler.emp.generated;

import java.util.Scanner;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Namespace;
import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.StdString;
import org.vaultdb.util.Utilities;







@Platform(include={"Millionaires.h"},
		compiler = "cpp11")





@Namespace("Millionaires")
public class Millionaires {

	String aliceHost = "";
	int netWorth;
	int party;
	int port;


	public Millionaires(int aParty, int aPort, String anIpAddress, int aNetWorth) {
		party = aParty;
		port = aPort;

		aliceHost = anIpAddress;
		netWorth =  aNetWorth;
	}



	// JNI wrapper, points to native methods in C++ program
	public static class MillionairesClass extends Pointer {

		static {

			Loader.load();
		}

		public MillionairesClass() {
			allocate();
		}

		private native void allocate();


		public native void configure(int party, int port, @StdString String aliceHost);

		public native  boolean  run(int netWorth);

		public native void cleanup();


	}







	// usage: millionaires <party> <host>  <port> 
	public static void main(String[] args) {
		System.out.println("Args count: " + args.length);

		// 0 = alice, 1 = bob
		int party = Integer.parseInt(args[0]);
		String aliceHost = args[1]; // alice hostname / ip address
		int port = Integer.parseInt(args[2]);

		System.out.print("Net worth: ");
		Scanner scanner = new Scanner(System.in);
		int netWorth = scanner.nextInt();
		scanner.close();



		MillionairesClass m = new MillionairesClass();
		m.configure(party, port, aliceHost);
		boolean aliceRicher = m.run(netWorth);

		m.cleanup();
		m.close();

		String result = aliceRicher ? "Yes!" : "No!";
		System.out.println("Is Alice richer than Bob? " + result);



	}
}
