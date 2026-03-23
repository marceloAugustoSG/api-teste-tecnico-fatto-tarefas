package com.fatto.listatarefas.api_lista_tarefas.support;

import com.fatto.listatarefas.api_lista_tarefas.dto.TarefaCreateRequest;
import com.fatto.listatarefas.api_lista_tarefas.dto.TarefaResponse;
import com.fatto.listatarefas.api_lista_tarefas.dto.TarefaUpdateRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Factory de DTOs de tarefa para testes.
 */
public final class TarefaDtoFactory {

	private TarefaDtoFactory() {
	}

	public static TarefaCreateRequest criar(String nome, BigDecimal custo, LocalDate dataLimite) {
		return new TarefaCreateRequest(nome, custo, dataLimite);
	}

	public static TarefaUpdateRequest atualizar(String nome, BigDecimal custo, LocalDate dataLimite) {
		return new TarefaUpdateRequest(nome, custo, dataLimite);
	}

	public static TarefaResponse respostaExemplo() {
		return new TarefaResponse(1L, "T", "R$ 10,00", "01/01/2026", 1, false);
	}

	public static TarefaResponse respostaAtualizada() {
		return new TarefaResponse(5L, "X", "R$ 1,00", "02/02/2026", 2, false);
	}
}
