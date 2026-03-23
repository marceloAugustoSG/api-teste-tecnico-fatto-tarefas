package com.fatto.listatarefas.api_lista_tarefas.support;

import com.fatto.listatarefas.api_lista_tarefas.entity.Tarefa;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Factory de entidades {@link Tarefa} para testes (Object Mother).
 */
public final class TarefaEntityFactory {

	private TarefaEntityFactory() {
	}

	/** Tarefa id=1, custo baixo, ordem 1 — cenário de listagem no teste de serviço. */
	public static Tarefa primeiraListaComCustoBaixo() {
		return comId(1L, "A", new BigDecimal("100.00"), LocalDate.of(2025, 6, 1), 1);
	}

	/** Tarefa id=2, custo alto (≥ 1000), ordem 2. */
	public static Tarefa segundaListaComAltoCusto() {
		return comId(2L, "B", new BigDecimal("1500.00"), LocalDate.of(2025, 7, 1), 2);
	}

	public static Tarefa comId(Long id, String nome, BigDecimal custo, LocalDate dataLimite, int ordem) {
		Tarefa t = persistivel(nome, ordem);
		t.setId(id);
		t.setCusto(custo);
		t.setDataLimite(dataLimite);
		return t;
	}

	/** Persistência em {@link com.fatto.listatarefas.api_lista_tarefas.repository.TarefaRepositoryTest} (sem id). */
	public static Tarefa persistivel(String nome, int ordem) {
		Tarefa t = new Tarefa();
		t.setNome(nome);
		t.setCusto(BigDecimal.TEN);
		t.setDataLimite(LocalDate.of(2026, 1, 1));
		t.setOrdemApresentacao(ordem);
		return t;
	}
}
