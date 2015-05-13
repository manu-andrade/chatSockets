package servidor;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import model.Usuario;

public class Servidor {
	
//	Lista de todos os usuários do Chat
	private ArrayList<Usuario> usuarios;
	private int porta;
	
	public Servidor (int porta) {
		this.porta = porta;
		this.usuarios = new ArrayList<Usuario>();
	}
	
	public void executar() throws IOException {
//		Inicia o servidor na porta específica
		@SuppressWarnings("resource")
		ServerSocket sSocket = new ServerSocket(porta);
		
		System.out.println("[Servidor iniciado na porta: "+porta+"]\n");
		
//		Aceitando infinitos usuários
		while (true) {
			Socket socket = sSocket.accept();
			
//			Thread que executa a comunicação com os clientes
			UsuarioThread ut = new UsuarioThread(socket, this);
			ut.start();
		}
	}
	
	/* Criar um novo usuário */
	public String addUsuario (String nome, String ip, int porta, PrintStream outputUsuario) {
		
		String idUsuario = ip+":"+porta+"/~ "+nome;
		this.usuarios.add(new Usuario(nome, outputUsuario, idUsuario));
		
		String msg = "["+nome+" se conectou ao grupo]";
		sendAll(msg);
		
		return idUsuario;
		
	}
	
	/* Remover um usuário */
	public void removerUsuario (Socket socket, String idUsuario, String nome) throws IOException {
		
		for (Usuario usuario: this.usuarios) {
			if (usuario.getId().equals(idUsuario)) {
				this.usuarios.remove(usuario);
				break;
			}
		}
		
		String msg = "["+nome+" saiu do grupo]";
		sendAll(msg);
		
		socket.close();
		
	}
	
	
	/* Renomear Usuario */
	public void renameUser (String idUsuario, String novoNome) throws IOException{
		
		for (Usuario usuario: this.usuarios) {
			if (usuario.getNome().equals(novoNome)) {
				String msg = "[Nome de usuário já em uso]";
				usuario.getOutput().println(msg);
				//sendAll(msg);
			}
			else if (usuario.getId().equals(idUsuario)) {
				usuario.setNome(novoNome);
				String msg = "[Renomeado com sucesso]";
				usuario.getOutput().println(msg);
				//sendAll(msg);
			}
		}
	}
	
	/* Lista para visualizar todos os usuários */
	public String listarUsuarios() {
		
		String lista = "\nUsuários do grupo: \n\n";
		
		if (this.usuarios.isEmpty())
			return "Nenhum usuário conectado ao grupo.";
		
		for (Usuario usuario: this.usuarios)
			lista += usuario.getNome()+"\n";
		
		return lista;
		
	}
	
	/* Enviar uma nova mensagem (sem ser um comando) */
	public void sendMessage (String idUsuario, String message) {
		
//		Prepara a mensagem
		SimpleDateFormat dateFormat = new SimpleDateFormat ("hh:mm:ss dd/MM/yyyy");
		String msg = idUsuario+": "+message+" - "+dateFormat.format(new Date());
		
//		Envia pra todo mundo
		sendAll(msg);
		
	}
	
	
	public void sendPrivateMessage(String idUsuario, String nome_usuario, String message){
		// Prepara a mensagem
		SimpleDateFormat dateFormat = new SimpleDateFormat ("hh:mm:ss dd/MM/yyyy");
		String msg = idUsuario+" [PRIVADO]: "+message+" - "+dateFormat.format(new Date());
		
		//envia para um usuario especifico
		for (Usuario usuario: this.usuarios){
			if(usuario.getNome().equalsIgnoreCase(nome_usuario)){
				usuario.getOutput().println(msg);
				break;
			}
		}
	}

	
	
	/* Enviar as mensagens para todos os usuários conectados */
	public void sendAll (String msg) {
		
//		Printa a mensagem no Servidor
		System.out.println(msg);
		
//		Printa a mensagem para todos os usuários
		for (Usuario usuario: this.usuarios)
			usuario.getOutput().println(msg);
		
	}

	public static void main(String[] args) throws IOException {
		/********* Mudar para a porta desejada *********/
		new Servidor(1077).executar();
		/****************************************************/
	}
	
}
