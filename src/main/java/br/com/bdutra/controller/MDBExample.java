package br.com.bdutra.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.fasterxml.jackson.databind.ObjectMapper;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "connectionFactoryLookup", propertyValue = "java:/jms/ArtemisRemote"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/Saida1"),
		@ActivationConfigProperty(propertyName = "connectorClassName", propertyValue = "org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory") 
})
public class MDBExample implements MessageListener {

	@Resource
	private MessageDrivenContext mdbContext;

	@Override
	public void onMessage(Message message) {
		String texto;
		try {
			texto = message.getBody(String.class);
			System.out.println(texto);
		} catch (JMSException e) {
			e.printStackTrace();
			mdbContext.setRollbackOnly();
		}
	}

}
