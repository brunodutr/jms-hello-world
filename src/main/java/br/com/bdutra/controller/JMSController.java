package br.com.bdutra.controller;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Produces(APPLICATION_JSON)
@Path("/jms")
public class JMSController {

	@Inject
	@JMSConnectionFactory("java:/jms/ArtemisRemote")
	private JMSContext context;
	
	@Resource(lookup = "java:/jms/queue/Saida1")
	private Queue queue;

	@GET
	public String get() {
		JMSConsumer consumer = context.createConsumer(queue);
		return consumer.receiveBody(String.class);
	}
	
	@PUT
	public void put(String texto) {
		JMSProducer producer = context.createProducer();
		producer.send(queue, texto);
	}
}
