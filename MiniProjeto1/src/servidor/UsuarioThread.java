package servidor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
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
				+"/usuarios - Listar todos os participantes do grupo\n"
				+"/sair - Sair do grupo\n\n");

		String[] msg = null;
		String flag = null;
		String msg2 = null;
		String nome_usuario;
		//		Controle de mensagens do Cliente
		while (s.hasNextLine()) {
			msg2 = s.nextLine();
			msg = msg2.split(" ");

			if(msg[0].equals("bye")){
				flag = "bye";
			}

			if(msg[0].equals("send")){
				switch (msg[1]) {
				case "-all":
					servidor.sendMessage(idUsuario, msg2);
					break;
				case "-user":
					nome_usuario = msg[2];
					servidor.sendPrivateMessage(idUsuario, nome_usuario, msg2);
					break;
				default:
					break;
				}

				if(msg[0].equals("list")){
					flag = "list";
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

					//			renomea o usuario			
				case "rename":
					break;

					//			Se não for um comando... envia uma mensagem para todos os usuários
				default:
					usuarioOUT.println("Comando inexistente");
					break;
				}
			}

		}
	}
}
