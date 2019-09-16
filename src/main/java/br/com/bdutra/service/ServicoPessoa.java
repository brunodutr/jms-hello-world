package br.com.bdutra.service;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;

import br.com.bdutra.model.Pessoa;

@ApplicationScoped
public class ServicoPessoa {

	public String criar(Pessoa pessoa) {
		System.out.println("Criando pessoa: " + pessoa.getNome());
		return String.format("%s (%s)", pessoa.getNome(), LocalDate.now());
	}
	
	public void destruir(String texto) {
		System.out.println("Destruir pessoa: " + texto);
	}
}