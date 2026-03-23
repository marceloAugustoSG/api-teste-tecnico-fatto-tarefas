package com.fatto.listatarefas.api_lista_tarefas.repository;

import com.fatto.listatarefas.api_lista_tarefas.entity.Tarefa;
import com.fatto.listatarefas.api_lista_tarefas.support.TarefaEntityFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TarefaRepositoryTest {

	@Autowired
	private TarefaRepository repository;

	@Test
	void findMaxOrdemApresentacao_quandoVazio_retornaZero() {
		assertThat(repository.findMaxOrdemApresentacao()).isZero();
	}

	@Test
	void findMaxOrdemApresentacao_retornaMaiorOrdem() {
		repository.save(TarefaEntityFactory.persistivel("A", 1));
		repository.save(TarefaEntityFactory.persistivel("B", 3));
		repository.save(TarefaEntityFactory.persistivel("C", 2));

		assertThat(repository.findMaxOrdemApresentacao()).isEqualTo(3);
	}

	@Test
	void existsByNome_e_existsByNomeAndIdNot() {
		Tarefa alpha = repository.save(TarefaEntityFactory.persistivel("Alpha", 1));
		Tarefa beta = repository.save(TarefaEntityFactory.persistivel("Beta", 2));

		assertThat(repository.existsByNome("Alpha")).isTrue();
		assertThat(repository.existsByNome("Gamma")).isFalse();

		assertThat(repository.existsByNomeAndIdNot("Alpha", beta.getId())).isTrue();
		assertThat(repository.existsByNomeAndIdNot("Alpha", alpha.getId())).isFalse();
	}
}
