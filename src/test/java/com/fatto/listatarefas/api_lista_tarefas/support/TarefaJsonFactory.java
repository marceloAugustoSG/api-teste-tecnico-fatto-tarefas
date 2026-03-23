package com.fatto.listatarefas.api_lista_tarefas.support;

import com.fatto.listatarefas.api_lista_tarefas.dto.TarefaCreateRequest;
import com.fatto.listatarefas.api_lista_tarefas.dto.TarefaUpdateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Serializa DTOs para JSON usado em {@link org.springframework.test.web.servlet.MockMvc}.
 */
public final class TarefaJsonFactory {

	private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

	private TarefaJsonFactory() {
	}

	public static String criar(TarefaCreateRequest request) {
		return write(request);
	}

	public static String atualizar(TarefaUpdateRequest request) {
		return write(request);
	}

	public static String criar(String nome, int custoInt, String dataLimiteIso) {
		return criar(TarefaDtoFactory.criar(nome, BigDecimal.valueOf(custoInt), LocalDate.parse(dataLimiteIso)));
	}

	public static String atualizar(String nome, int custoInt, String dataLimiteIso) {
		return atualizar(TarefaDtoFactory.atualizar(nome, BigDecimal.valueOf(custoInt), LocalDate.parse(dataLimiteIso)));
	}

	private static String write(Object value) {
		try {
			return MAPPER.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}
}
