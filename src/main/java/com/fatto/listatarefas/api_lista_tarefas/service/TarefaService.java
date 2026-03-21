package com.fatto.listatarefas.api_lista_tarefas.service;

import com.fatto.listatarefas.api_lista_tarefas.dto.ListaTarefasResponse;
import com.fatto.listatarefas.api_lista_tarefas.dto.TarefaCreateRequest;
import com.fatto.listatarefas.api_lista_tarefas.dto.TarefaResponse;
import com.fatto.listatarefas.api_lista_tarefas.dto.TarefaUpdateRequest;
import com.fatto.listatarefas.api_lista_tarefas.entity.Tarefa;
import com.fatto.listatarefas.api_lista_tarefas.exception.NomeTarefaDuplicadoException;
import com.fatto.listatarefas.api_lista_tarefas.exception.OrdemNaoPodeSerAlteradaException;
import com.fatto.listatarefas.api_lista_tarefas.exception.RecursoNaoEncontradoException;
import com.fatto.listatarefas.api_lista_tarefas.repository.TarefaRepository;
import com.fatto.listatarefas.api_lista_tarefas.util.FormatacaoBrasil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TarefaService {

	private static final BigDecimal LIMITE_ALTO_CUSTO = new BigDecimal("1000.00");

	private final TarefaRepository repository;

	public TarefaService(TarefaRepository repository) {
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public ListaTarefasResponse listar() {
		List<Tarefa> entidades = repository.findAllByOrderByOrdemApresentacaoAsc();
		List<TarefaResponse> itens = entidades.stream().map(this::paraResposta).toList();
		BigDecimal soma = entidades.stream()
				.map(Tarefa::getCusto)
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(2, RoundingMode.HALF_UP);
		return new ListaTarefasResponse(itens, FormatacaoBrasil.formatarValorMonetario(soma));
	}

	@Transactional
	public TarefaResponse criar(TarefaCreateRequest request) {
		String nome = normalizarNome(request.nome());
		if (repository.existsByNome(nome)) {
			throw new NomeTarefaDuplicadoException();
		}
		Tarefa tarefa = new Tarefa();
		tarefa.setNome(nome);
		tarefa.setCusto(normalizarCusto(request.custo()));
		tarefa.setDataLimite(request.dataLimite());
		tarefa.setOrdemApresentacao(repository.findMaxOrdemApresentacao() + 1);
		return paraResposta(repository.save(tarefa));
	}

	@Transactional
	public TarefaResponse atualizar(Long id, TarefaUpdateRequest request) {
		Tarefa tarefa = buscarPorId(id);
		String nome = normalizarNome(request.nome());
		if (repository.existsByNomeAndIdNot(nome, id)) {
			throw new NomeTarefaDuplicadoException();
		}
		tarefa.setNome(nome);
		tarefa.setCusto(normalizarCusto(request.custo()));
		tarefa.setDataLimite(request.dataLimite());
		return paraResposta(repository.save(tarefa));
	}

	@Transactional
	public void excluir(Long id) {
		Tarefa tarefa = buscarPorId(id);
		repository.delete(tarefa);
	}

	@Transactional
	public void subir(Long id) {
		Tarefa atual = buscarPorId(id);
		Tarefa anterior = repository
				.findTopByOrdemApresentacaoLessThanOrderByOrdemApresentacaoDesc(atual.getOrdemApresentacao())
				.orElseThrow(() -> new OrdemNaoPodeSerAlteradaException("A primeira tarefa não pode subir."));
		trocarOrdem(atual, anterior);
	}

	@Transactional
	public void descer(Long id) {
		Tarefa atual = buscarPorId(id);
		Tarefa proxima = repository
				.findTopByOrdemApresentacaoGreaterThanOrderByOrdemApresentacaoAsc(atual.getOrdemApresentacao())
				.orElseThrow(() -> new OrdemNaoPodeSerAlteradaException("A última tarefa não pode descer."));
		trocarOrdem(atual, proxima);
	}

	private void trocarOrdem(Tarefa a, Tarefa b) {
		int ordemA = a.getOrdemApresentacao();
		int ordemB = b.getOrdemApresentacao();
		a.setOrdemApresentacao(-ordemA);
		repository.saveAndFlush(a);
		b.setOrdemApresentacao(ordemA);
		repository.saveAndFlush(b);
		a.setOrdemApresentacao(ordemB);
		repository.save(a);
	}

	private Tarefa buscarPorId(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa não encontrada."));
	}

	private TarefaResponse paraResposta(Tarefa t) {
		BigDecimal custo = t.getCusto();
		boolean alto = custo.compareTo(LIMITE_ALTO_CUSTO) >= 0;
		return new TarefaResponse(
				t.getId(),
				t.getNome(),
				FormatacaoBrasil.formatarValorMonetario(custo),
				FormatacaoBrasil.formatarData(t.getDataLimite()),
				t.getOrdemApresentacao(),
				alto
		);
	}

	private static String normalizarNome(String nome) {
		return nome == null ? "" : nome.trim();
	}

	private static BigDecimal normalizarCusto(BigDecimal custo) {
		return custo.setScale(2, RoundingMode.HALF_UP);
	}
}
