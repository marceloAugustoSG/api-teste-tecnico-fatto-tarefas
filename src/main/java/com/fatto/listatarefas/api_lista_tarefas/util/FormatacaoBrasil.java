package com.fatto.listatarefas.api_lista_tarefas.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class FormatacaoBrasil {

	private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu");

	private FormatacaoBrasil() {
	}

	public static String formatarData(LocalDate data) {
		return data == null ? null : DATA.format(data);
	}

	public static String formatarValorMonetario(BigDecimal valor) {
		if (valor == null) {
			return null;
		}
		BigDecimal normalizado = valor.setScale(2, RoundingMode.HALF_UP);
		NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
		fmt.setRoundingMode(RoundingMode.HALF_UP);
		return fmt.format(normalizado);
	}
}
