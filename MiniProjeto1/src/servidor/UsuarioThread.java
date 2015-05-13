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

		usuarioOUT.println("\nBem vindo ao CHAT LINE "+nome+"! Comandos:\n\n"
				+"send -all (msg) : Envia mensagem para todos os participantes do grupo\n"
				+"send -user nomeUsuario (msg) : Envia mensagem para usuario espec�fico\n"
				+"list : Listar todos os participantes do grupo\n"
				+"rename nomeNovo : Altera seu username\n"
				+"bye : Sair do grupo\n\n");

		String msg = null;
		String flag = "";
		String msg2 = null;
		String nome_usuario = null;
		String novo_usuario = null;
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
			
			if(msg2.contains("-user")){
				flag = "send -user";
				msg = msg2.replace("send -user", "");
				String[] arrayMsg = msg.split(" ");
				nome_usuario = arrayMsg[1];
			}

			if(msg2.contains("list")){
				flag = "list";
			}
			
			if (msg2.contains("rename")){
				flag = "rename";
				novo_usuario = msg2.split(" ")[1];
				nome = novo_usuario;
			}

			switch (flag) {

//			    Remove o usu�rio do Servidor e fecha o Socket
			case "bye":
				try {
					servidor.removerUsuario(socket, idUsuario, nome);
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

//   			Printa todos os usu�rios para o Cliente
			case "list":
				usuarioOUT.println(servidor.listarUsuarios());
				break;

//               envia mensagem para todos do grupo
			case "send -all":
				servidor.sendMessage(nome, socket.getInetAddress().getHostAddress(), socket.getPort(), msg);
				break;

//              envia mensagem para um usu�rio espec�fico
			case "send -user":
				try {
					
					servidor.sendPrivateMessage(nome, socket.getInetAddress().getHostAddress(), socket.getPort(), nome_usuario, msg);
					
				} catch (Exception e) {
					System.err.println("N�o foi poss�vel envia mensagem para este usu�rio espec�fico");
				}
				
				break;

//              renomeia um usu�rio j� existente
			case "rename":
				try {
					servidor.renameUser(idUsuario,socket.getInetAddress().getHostAddress(), novo_usuario);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
				//			Se n�o for um comando... envia uma mensagem para todos os usu�rios
			default:
				usuarioOUT.println("[Digite um comando V�lido!]");
				break;
			}
		}
	}
}