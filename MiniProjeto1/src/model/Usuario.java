package model;

import java.io.PrintStream;

public class Usuario {
	
	/* OBS: o ID é o conjunto "IP:Porta/~ nome" */
	private String id;
	private String nome;
	private PrintStream output;

	public Usuario(String nome, PrintStream output, String id) {
		this.id = id;
		this.nome = nome;
		this.output = output;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public PrintStream getOutput() {
		return output;
	}

	public void setOutput(PrintStream output) {
		this.output = output;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
