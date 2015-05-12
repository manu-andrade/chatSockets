package cliente;

import java.io.InputStream;
import java.util.Scanner;

public class LeitorThread extends Thread {
	
//	Stream de entrada do Servidor
	private InputStream servidorIN;
	
	public LeitorThread (InputStream servidorIN) {
		this.servidorIN = servidorIN;
	}

	public void run() {
		@SuppressWarnings("resource")
		Scanner s = new Scanner(servidorIN);
		
//		Printa as mensagens do Servidor para o Cliente
		while (s.hasNextLine()) {
			System.out.println(s.nextLine());
		}
	}

}
