package com.fatto.listatarefas.api_lista_tarefas.config;

import com.fatto.listatarefas.api_lista_tarefas.repository.TarefaRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
		prefix = "app.warmup",
		name = "enabled",
		havingValue = "true",
		matchIfMissing = false
)
public class WarmupScheduler {

	private final TarefaRepository repository;

	public WarmupScheduler(TarefaRepository repository) {
		this.repository = repository;
	}

	@Scheduled(
			initialDelayString = "${app.warmup.initial-delay-ms:30000}",
			fixedDelayString = "${app.warmup.fixed-delay-ms:300000}"
	)
	public void warmup() {
		// Mantém conexão e "acorda" o banco/serviço após períodos de inatividade.
		// Chamamos uma query leve (MAX(ordem_apresentacao)).
		try {
			repository.findMaxOrdemApresentacao();
		} catch (Exception ignored) {
			// Warm-up deve falhar silenciosamente para não poluir logs.
		}
	}
}

