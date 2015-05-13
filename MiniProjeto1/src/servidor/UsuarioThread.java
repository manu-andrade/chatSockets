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
		usuarioOUT.println("[Conexão com o Servidor de Chat realizada com sucesso] \nDigite seu nome para se conectar ao grupo:");

		@SuppressWarnings("resource")
		Scanner s = new Scanner(this.usuarioIN);

		//		Recebe a primeira mensagem, que é o nome do usuário
		String nome = s.nextLine();

		//		Adiciona o usuário na lista do Servidor
		String idUsuario = servidor.addUsuario(nome, socket.getInetAddress().getHostAddress(), socket.getPort(), usuarioOUT);

		usuarioOUT.println("\nBem vindo ao CHAT CHAT CHAT LINE "+nome+"! Comandos:\n"
				+"send -all - Envia mensagem para todos os participantes do grupo\n"
				+"send -user nomeUsuario - Envia mensagem para usuario específico\n"
				+"list - Listar todos os participantes do grupo\n"
				+"rename nomeNovo - Altera seu username\n"
				+"bye - Sair do grupo\n\n");

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
				nome_usuario = arrayMsg[0];
			}

			if(msg2.contains("list")){
				flag = "list";
			}
			
			if (msg2.contains("rename")){
				flag = "rename";
				novo_usuario = msg2.split(" ")[1];
			}

			switch (flag) {

			//			Remove o usuário do Servidor e fecha o Socket
			case "bye":
				try {
					servidor.removerUsuario(socket, idUsuario, nome);
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

				//			Printa todos os usuários para o Cliente
			case "list":
				usuarioOUT.println(servidor.listarUsuarios());
				break;

			case "send -all":
				servidor.sendMessage(idUsuario, msg);
				break;

			case "send -user":
				servidor.sendPrivateMessage(idUsuario, nome_usuario, msg);
				break;
			
			case "rename":
				try {
					servidor.renameUser(idUsuario, novo_usuario);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
				//			Se não for um comando... envia uma mensagem para todos os usuários
			default:
				usuarioOUT.println("Digite um comando Válido!");
				break;
			}
		}
	}
}