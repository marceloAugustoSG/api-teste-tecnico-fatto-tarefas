package com.fatto.listatarefas.api_lista_tarefas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TarefaUpdateRequest(
		@NotBlank(message = "O nome da tarefa é obrigatório.")
		String nome,
		@NotNull(message = "O custo é obrigatório.")
		@DecimalMin(value = "0.0", inclusive = true, message = "O custo deve ser maior ou igual a zero.")
		BigDecimal custo,
		@NotNull(message = "A data limite é obrigatória.")
		LocalDate dataLimite
) {
}
