package com.fatto.listatarefas.api_lista_tarefas.service;

import com.fatto.listatarefas.api_lista_tarefas.entity.Tarefa;
import com.fatto.listatarefas.api_lista_tarefas.exception.NomeTarefaDuplicadoException;
import com.fatto.listatarefas.api_lista_tarefas.exception.OrdemNaoPodeSerAlteradaException;
import com.fatto.listatarefas.api_lista_tarefas.exception.RecursoNaoEncontradoException;
import com.fatto.listatarefas.api_lista_tarefas.repository.TarefaRepository;
import com.fatto.listatarefas.api_lista_tarefas.support.TarefaDtoFactory;
import com.fatto.listatarefas.api_lista_tarefas.support.TarefaEntityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

	@Mock
	private TarefaRepository repository;

	@InjectMocks
	private TarefaService service;

	@Test
	void listar_quandoVazio_retornaSomaZero() {
		when(repository.findAllByOrderByOrdemApresentacaoAsc()).thenReturn(List.of());

		var res = service.listar();

		assertThat(res.tarefas()).isEmpty();
		assertThat(res.somaCustos()).contains("0,00");
	}

	@Test
	void listar_ordenaPorOrdemESomaCustos() {
		when(repository.findAllByOrderByOrdemApresentacaoAsc()).thenReturn(List.of(
				TarefaEntityFactory.primeiraListaComCustoBaixo(),
				TarefaEntityFactory.segundaListaComAltoCusto()));

		var res = service.listar();

		assertThat(res.tarefas()).hasSize(2);
		assertThat(res.tarefas().get(0).nome()).isEqualTo("A");
		assertThat(res.tarefas().get(1).altoCusto()).isTrue();
		assertThat(res.somaCustos()).contains("1.600,00");
	}

	@Test
	void criar_quandoNomeDuplicado_lanca() {
		when(repository.existsByNome("X")).thenReturn(true);

		var req = TarefaDtoFactory.criar("X", BigDecimal.TEN, LocalDate.now());

		assertThatThrownBy(() -> service.criar(req)).isInstanceOf(NomeTarefaDuplicadoException.class);
		verify(repository, never()).save(any());
	}

	@Test
	void criar_persisteComProximaOrdem() {
		when(repository.existsByNome("Nova")).thenReturn(false);
		when(repository.findMaxOrdemApresentacao()).thenReturn(3);
		when(repository.save(any(Tarefa.class))).thenAnswer(inv -> {
			Tarefa t = inv.getArgument(0);
			t.setId(99L);
			return t;
		});

		var req = TarefaDtoFactory.criar("Nova", new BigDecimal("50"), LocalDate.of(2026, 1, 15));
		var res = service.criar(req);

		assertThat(res.nome()).isEqualTo("Nova");
		assertThat(res.ordemApresentacao()).isEqualTo(4);

		ArgumentCaptor<Tarefa> cap = ArgumentCaptor.forClass(Tarefa.class);
		verify(repository).save(cap.capture());
		assertThat(cap.getValue().getOrdemApresentacao()).isEqualTo(4);
	}

	@Test
	void atualizar_quandoNomeJaExisteEmOutra_lanca() {
		when(repository.findById(1L)).thenReturn(Optional.of(TarefaEntityFactory.primeiraListaComCustoBaixo()));
		when(repository.existsByNomeAndIdNot("B", 1L)).thenReturn(true);

		var req = TarefaDtoFactory.atualizar("B", BigDecimal.ONE, LocalDate.now());

		assertThatThrownBy(() -> service.atualizar(1L, req)).isInstanceOf(NomeTarefaDuplicadoException.class);
	}

	@Test
	void excluir_quandoNaoExiste_lanca() {
		when(repository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.excluir(99L)).isInstanceOf(RecursoNaoEncontradoException.class);
	}

	@Test
	void subir_quandoPrimeira_lanca() {
		when(repository.findById(1L)).thenReturn(Optional.of(TarefaEntityFactory.primeiraListaComCustoBaixo()));
		when(repository.findTopByOrdemApresentacaoLessThanOrderByOrdemApresentacaoDesc(1))
				.thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.subir(1L)).isInstanceOf(OrdemNaoPodeSerAlteradaException.class)
				.hasMessageContaining("primeira");
	}

	@Test
	void descer_quandoUltima_lanca() {
		when(repository.findById(2L)).thenReturn(Optional.of(TarefaEntityFactory.segundaListaComAltoCusto()));
		when(repository.findTopByOrdemApresentacaoGreaterThanOrderByOrdemApresentacaoAsc(2))
				.thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.descer(2L)).isInstanceOf(OrdemNaoPodeSerAlteradaException.class)
				.hasMessageContaining("última");
	}
}
