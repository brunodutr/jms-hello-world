package br.com.bdutra.controller;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.bdutra.annotations.JMSListener;
import br.com.bdutra.infraestruture.jms.qualifiers.JMSController;
import br.com.bdutra.model.Pessoa;
import br.com.bdutra.service.ServicoPessoa;

@JMSController
@ApplicationScoped
public class PessoaController {

	private static final String QUEUE_SAIDA2 = "java:/jms/queue/Saida2";
	private static final String QUEUE_SAIDA3 = "java:/jms/queue/Saida3";

	@Inject
	private ServicoPessoa servicoPessoa;

	@JMSListener(destination = QUEUE_SAIDA2, destinationResponse = QUEUE_SAIDA3, consumes = APPLICATION_JSON)
	public String criar(Pessoa pessoa) {
		return servicoPessoa.criar(pessoa);
	}

	@JMSListener(destination = QUEUE_SAIDA3)
	public void destruir(String texto) {
		servicoPessoa.destruir(texto);
	}
}
