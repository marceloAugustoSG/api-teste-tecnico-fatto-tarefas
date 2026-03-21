package com.fatto.listatarefas.api_lista_tarefas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
		name = "tarefas",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_tarefas_nome", columnNames = "nome"),
				@UniqueConstraint(name = "uk_tarefas_ordem_apresentacao", columnNames = "ordem_apresentacao")
		}
)
public class Tarefa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 500)
	private String nome;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal custo;

	@Column(name = "data_limite", nullable = false)
	private LocalDate dataLimite;

	@Column(name = "ordem_apresentacao", nullable = false)
	private Integer ordemApresentacao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public BigDecimal getCusto() {
		return custo;
	}

	public void setCusto(BigDecimal custo) {
		this.custo = custo;
	}

	public LocalDate getDataLimite() {
		return dataLimite;
	}

	public void setDataLimite(LocalDate dataLimite) {
		this.dataLimite = dataLimite;
	}

	public Integer getOrdemApresentacao() {
		return ordemApresentacao;
	}

	public void setOrdemApresentacao(Integer ordemApresentacao) {
		this.ordemApresentacao = ordemApresentacao;
	}
}
