package com.fatto.listatarefas.api_lista_tarefas;

import com.fatto.listatarefas.api_lista_tarefas.support.TarefaJsonFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TarefaApiIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("GET /api/tarefas — lista vazia e soma em formato moeda BR")
	void listarVazio() throws Exception {
		mockMvc.perform(get("/api/tarefas").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tarefas").isArray())
				.andExpect(jsonPath("$.tarefas.length()").value(0))
				.andExpect(jsonPath("$.somaCustos").value(allOf(startsWith("R$"), endsWith("0,00"))));
	}

	@Test
	@DisplayName("Fluxo CRUD + reordenação")
	void fluxoCriarListarEditarExcluirReordenar() throws Exception {
		mockMvc.perform(post("/api/tarefas")
						.contentType(MediaType.APPLICATION_JSON)
						.content(TarefaJsonFactory.criar("Primeira", 100, "2026-06-01")))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.nome").value("Primeira"))
				.andExpect(jsonPath("$.ordemApresentacao").value(1));

		mockMvc.perform(post("/api/tarefas")
						.contentType(MediaType.APPLICATION_JSON)
						.content(TarefaJsonFactory.criar("Segunda", 200, "2026-07-01")))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.ordemApresentacao").value(2));

		mockMvc.perform(get("/api/tarefas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tarefas.length()").value(2))
				.andExpect(jsonPath("$.tarefas[0].nome").value("Primeira"));

		mockMvc.perform(post("/api/tarefas")
						.contentType(MediaType.APPLICATION_JSON)
						.content(TarefaJsonFactory.criar("Primeira", 50, "2026-08-01")))
				.andExpect(status().isConflict());

		mockMvc.perform(put("/api/tarefas/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(TarefaJsonFactory.atualizar("Primeira editada", 100, "2026-06-01")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nome").value("Primeira editada"));

		mockMvc.perform(post("/api/tarefas/2/subir"))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/tarefas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tarefas[0].nome").value("Segunda"))
				.andExpect(jsonPath("$.tarefas[1].nome").value("Primeira editada"));

		mockMvc.perform(delete("/api/tarefas/1"))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/tarefas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tarefas.length()").value(1));
	}

	@Test
	@DisplayName("DELETE — tarefa inexistente retorna 404")
	void excluirNaoEncontrado() throws Exception {
		mockMvc.perform(delete("/api/tarefas/99999"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("POST /subir — primeira tarefa retorna 400")
	void subirPrimeira() throws Exception {
		mockMvc.perform(post("/api/tarefas")
						.contentType(MediaType.APPLICATION_JSON)
						.content(TarefaJsonFactory.criar("Só uma", 10, "2026-01-01")))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/tarefas/1/subir"))
				.andExpect(status().isBadRequest());
	}
}
