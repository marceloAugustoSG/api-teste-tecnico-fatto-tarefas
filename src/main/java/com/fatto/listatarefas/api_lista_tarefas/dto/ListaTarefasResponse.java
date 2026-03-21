package com.fatto.listatarefas.api_lista_tarefas.dto;

import java.util.List;

public record ListaTarefasResponse(
		List<TarefaResponse> tarefas,
		String somaCustos
) {
}
