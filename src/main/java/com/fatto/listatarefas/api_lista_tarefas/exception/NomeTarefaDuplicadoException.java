package com.fatto.listatarefas.api_lista_tarefas.exception;

public class NomeTarefaDuplicadoException extends RuntimeException {

	public NomeTarefaDuplicadoException() {
		super("Já existe uma tarefa com este nome.");
	}
}
