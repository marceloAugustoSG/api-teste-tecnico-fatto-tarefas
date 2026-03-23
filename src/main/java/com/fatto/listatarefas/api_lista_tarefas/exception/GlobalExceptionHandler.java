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

	private static final URI TIPO_PROBLEMA_GENERICO = URI.create("about:blank");

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ProblemDetail recursoNaoEncontrado(RecursoNaoEncontradoException ex) {
		return problem(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage());
	}

	@ExceptionHandler(NomeTarefaDuplicadoException.class)
	public ProblemDetail nomeDuplicado(NomeTarefaDuplicadoException ex) {
		return problem(HttpStatus.CONFLICT, "Nome de tarefa duplicado", ex.getMessage());
	}

	@ExceptionHandler(OrdemNaoPodeSerAlteradaException.class)
	public ProblemDetail ordemInvalida(OrdemNaoPodeSerAlteradaException ex) {
		return problem(HttpStatus.BAD_REQUEST, "Ordem não pode ser alterada", ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail validacao(MethodArgumentNotValidException ex) {
		String mensagem = ex.getBindingResult().getFieldErrors().stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage())
				.collect(Collectors.joining("; "));
		return problem(HttpStatus.BAD_REQUEST, "Dados inválidos", mensagem);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ProblemDetail integridade(DataIntegrityViolationException ex) {
		return problem(
				HttpStatus.CONFLICT,
				"Conflito de dados",
				"Violação de regra de integridade dos dados (por exemplo, nome ou ordem duplicados).");
	}

	private static ProblemDetail problem(HttpStatus status, String title, String detail) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
		pd.setTitle(title);
		pd.setType(TIPO_PROBLEMA_GENERICO);
		return pd;
	}
}
