package com.fatto.listatarefas.api_lista_tarefas.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ProblemDetail recursoNaoEncontrado(RecursoNaoEncontradoException ex) {
		ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		detail.setTitle("Recurso não encontrado");
		detail.setType(URI.create("about:blank"));
		return detail;
	}

	@ExceptionHandler(NomeTarefaDuplicadoException.class)
	public ProblemDetail nomeDuplicado(NomeTarefaDuplicadoException ex) {
		ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
		detail.setTitle("Nome de tarefa duplicado");
		detail.setType(URI.create("about:blank"));
		return detail;
	}

	@ExceptionHandler(OrdemNaoPodeSerAlteradaException.class)
	public ProblemDetail ordemInvalida(OrdemNaoPodeSerAlteradaException ex) {
		ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
		detail.setTitle("Ordem não pode ser alterada");
		detail.setType(URI.create("about:blank"));
		return detail;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail validacao(MethodArgumentNotValidException ex) {
		String mensagem = ex.getBindingResult().getFieldErrors().stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage())
				.collect(Collectors.joining("; "));
		ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, mensagem);
		detail.setTitle("Dados inválidos");
		detail.setType(URI.create("about:blank"));
		return detail;
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ProblemDetail integridade(DataIntegrityViolationException ex) {
		ProblemDetail detail = ProblemDetail.forStatusAndDetail(
				HttpStatus.CONFLICT,
				"Violação de regra de integridade dos dados (por exemplo, nome ou ordem duplicados).");
		detail.setTitle("Conflito de dados");
		detail.setType(URI.create("about:blank"));
		return detail;
	}
}
