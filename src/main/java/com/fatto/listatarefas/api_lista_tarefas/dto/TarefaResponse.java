package com.fatto.listatarefas.api_lista_tarefas.dto;

public record TarefaResponse(
		Long id,
		String nome,
		String custo,
		String dataLimite,
		Integer ordemApresentacao,
		boolean altoCusto
) {
}
