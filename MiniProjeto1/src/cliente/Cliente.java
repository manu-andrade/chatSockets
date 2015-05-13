package cliente;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		@SuppressWarnings("resource")
		Scanner s = new Scanner(System.in);
		
		/********* Mudar para o IP da m�quina local *********/
		@SuppressWarnings("resource")
		Socket socket = new Socket("10.34.21.1", 1061);
		/****************************************************/
		
		
//		Thread que tem a fun��o de ler as mensagens enviadas do Servidor
		LeitorThread leitor = new LeitorThread(new DataInputStream (socket.getInputStream()));
		leitor.start();
		
//		Stream de sa�da para o Servidor
		PrintStream servidorOUT = new PrintStream(socket.getOutputStream());
//		OBS: a primeira mensagem serve para definir o nome do usu�rio
		servidorOUT.println(s.nextLine());
		
		String msg = null;
		
//		Sempre que o Cliente digita, a mensagem � enviada para o Servidor
		while (s.hasNextLine()) {
			msg = s.nextLine();
			servidorOUT.println(msg);
			
//			Encerra o cliente caso tenha digitado "/sair"
			if (msg.equals("/sair")) {
				System.out.println("[Voc� foi desconectado do chat]");
				socket.close();
				s.close();
				return;
			}
		}
		
	}

}
