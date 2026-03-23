package com.fatto.listatarefas.api_lista_tarefas.controller;

import com.fatto.listatarefas.api_lista_tarefas.dto.ListaTarefasResponse;
import com.fatto.listatarefas.api_lista_tarefas.service.TarefaService;
import com.fatto.listatarefas.api_lista_tarefas.support.TarefaDtoFactory;
import com.fatto.listatarefas.api_lista_tarefas.support.TarefaJsonFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TarefaController.class)
class TarefaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TarefaService tarefaService;

	@Test
	void getListar_delegaAoServico() throws Exception {
		when(tarefaService.listar()).thenReturn(new ListaTarefasResponse(List.of(), "R$ 0,00"));

		mockMvc.perform(get("/api/tarefas").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tarefas").isArray())
				.andExpect(jsonPath("$.somaCustos").value("R$ 0,00"));

		verify(tarefaService).listar();
	}

	@Test
	void postCriar_corpoValido_retorna201() throws Exception {
		when(tarefaService.criar(any())).thenReturn(TarefaDtoFactory.respostaExemplo());

		mockMvc.perform(post("/api/tarefas")
						.contentType(MediaType.APPLICATION_JSON)
						.content(TarefaJsonFactory.criar("T", 10, "2026-01-01")))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.nome").value("T"));
	}

	@Test
	void postCriar_corpoInvalido_retorna400() throws Exception {
		mockMvc.perform(post("/api/tarefas")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nome\":\"\",\"custo\":-1,\"dataLimite\":null}"))
				.andExpect(status().isBadRequest());

		verify(tarefaService, never()).criar(any());
	}

	@Test
	void putAtualizar_delega() throws Exception {
		when(tarefaService.atualizar(eq(5L), any())).thenReturn(TarefaDtoFactory.respostaAtualizada());

		mockMvc.perform(put("/api/tarefas/5")
						.contentType(MediaType.APPLICATION_JSON)
						.content(TarefaJsonFactory.atualizar("X", 1, "2026-02-02")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nome").value("X"));
	}

	@Test
	void deleteExcluir_retorna204() throws Exception {
		doNothing().when(tarefaService).excluir(3L);

		mockMvc.perform(delete("/api/tarefas/3"))
				.andExpect(status().isNoContent());

		verify(tarefaService).excluir(3L);
	}

	@Test
	void postSubir_retorna204() throws Exception {
		doNothing().when(tarefaService).subir(1L);

		mockMvc.perform(post("/api/tarefas/1/subir"))
				.andExpect(status().isNoContent());
	}

	@Test
	void postDescer_retorna204() throws Exception {
		doNothing().when(tarefaService).descer(2L);

		mockMvc.perform(post("/api/tarefas/2/descer"))
				.andExpect(status().isNoContent());
	}
}
