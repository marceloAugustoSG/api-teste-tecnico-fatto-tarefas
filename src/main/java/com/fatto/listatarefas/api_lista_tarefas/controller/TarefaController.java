package com.fatto.listatarefas.api_lista_tarefas.controller;

import com.fatto.listatarefas.api_lista_tarefas.dto.ListaTarefasResponse;
import com.fatto.listatarefas.api_lista_tarefas.dto.TarefaCreateRequest;
import com.fatto.listatarefas.api_lista_tarefas.dto.TarefaResponse;
import com.fatto.listatarefas.api_lista_tarefas.dto.TarefaUpdateRequest;
import com.fatto.listatarefas.api_lista_tarefas.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

	private final TarefaService tarefaService;

	public TarefaController(TarefaService tarefaService) {
		this.tarefaService = tarefaService;
	}

	@GetMapping
	public ListaTarefasResponse listar() {
		return tarefaService.listar();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TarefaResponse criar(@Valid @RequestBody TarefaCreateRequest request) {
		return tarefaService.criar(request);
	}

	@PutMapping("/{id}")
	public TarefaResponse atualizar(
			@PathVariable Long id,
			@Valid @RequestBody TarefaUpdateRequest request) {
		return tarefaService.atualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Long id) {
		tarefaService.excluir(id);
	}

	@PostMapping("/{id}/subir")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void subir(@PathVariable Long id) {
		tarefaService.subir(id);
	}

	@PostMapping("/{id}/descer")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void descer(@PathVariable Long id) {
		tarefaService.descer(id);
	}
}
