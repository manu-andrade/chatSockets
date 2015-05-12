package servidor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class UsuarioThread extends Thread{
	private Socket socket;
	private Servidor servidor;
	private InputStream usuarioIN;
	private PrintStream usuarioOUT;
	
	public UsuarioThread(Socket socket, Servidor servidor) throws IOException {
		this.socket = socket;
		this.servidor = servidor;
		this.usuarioIN = socket.getInputStream();
		this.usuarioOUT = new PrintStream(socket.getOutputStream());
	}
	
	public void run() {
		usuarioOUT.println("[Conex�o com o Servidor de Chat realizada com sucesso] \nDigite seu nome para se conectar ao grupo:");

		@SuppressWarnings("resource")
		Scanner s = new Scanner(this.usuarioIN);
		
//		Recebe a primeira mensagem, que � o nome do usu�rio
		String nome = s.nextLine();
		
//		Adiciona o usu�rio na lista do Servidor
		String idUsuario = servidor.addUsuario(nome, socket.getInetAddress().getHostAddress(), socket.getPort(), usuarioOUT);

		usuarioOUT.println("\nBem vindo ao CHAT CHAT CHAT LINE "+nome+"! Comandos:\n"
						   +"/usuarios - Listar todos os participantes do grupo\n"
						   +"/sair - Sair do grupo\n\n");
		
		String msg = null;
		String flag = null;
		String msg2 = null;
//		Controle de mensagens do Cliente
		while (s.hasNextLine()) {
			msg2 = s.nextLine();
			
			
			if(msg2.contains("bye")){
				flag = "bye";
			}
			
			if(msg2.contains("-all")){
				flag = "send -all";
				msg = msg2.replace("send -all", "");
			}
			
			if(msg2.contains("list")){
				flag = "list";
			}
		
			switch (flag) {
			
//			Remove o usu�rio do Servidor e fecha o Socket
			case "bye":
				try {
					servidor.removerUsuario(socket, idUsuario, nome);
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			
//			Printa todos os usu�rios para o Cliente
			case "list":
				usuarioOUT.println(servidor.listarUsuarios());
				break;
				
			case "send -all":
				servidor.sendMessage(idUsuario, msg);
				break;
				
			case "send -user":
				break;

//			Se n�o for um comando... envia uma mensagem para todos os usu�rios
			default:
				
				break;
			}
		}
		
	}
}
