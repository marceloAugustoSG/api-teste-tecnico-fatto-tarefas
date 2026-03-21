package com.fatto.listatarefas.api_lista_tarefas.repository;

import com.fatto.listatarefas.api_lista_tarefas.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

	boolean existsByNome(String nome);

	boolean existsByNomeAndIdNot(String nome, Long id);

	List<Tarefa> findAllByOrderByOrdemApresentacaoAsc();

	Optional<Tarefa> findTopByOrdemApresentacaoLessThanOrderByOrdemApresentacaoDesc(Integer ordem);

	Optional<Tarefa> findTopByOrdemApresentacaoGreaterThanOrderByOrdemApresentacaoAsc(Integer ordem);

	@Query("SELECT COALESCE(MAX(t.ordemApresentacao), 0) FROM Tarefa t")
	int findMaxOrdemApresentacao();
}
