package br.com.bdutra.infraestruture.jms;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.bdutra.annotations.JMSListener;

public class ServicoJMS implements Runnable {

	private Context ctx;
	private Object target;
	private Method method;
	private JMSListener listener;
	private ConnectionFactory cf;
	private Destination destination;

	public ServicoJMS(final Object target, final Method method) {

		setContext();

		listener = method.getAnnotation(JMSListener.class);

		this.target = target;
		this.method = method;

		cf = lookUp("java:/jms/ArtemisRemote");
		destination = lookUp(listener.destination());
	}

	@Override
	@Transactional
	public void run() {

		try (JMSContext jmsContext = cf.createContext();
				JMSConsumer consumer = jmsContext.createConsumer(destination)) {

			JMSProducer producer = jmsContext.createProducer();

			String receiveBody = consumer.receiveBodyNoWait(String.class);

			if (receiveBody != null) {
				try {

					Object param = getParam(receiveBody);

					String valueReturned = (String) method.invoke(target, param);

					if (isValidString(listener.destinationResponse())) {

						Destination destinationResponse = lookUp(listener.destinationResponse());

						producer.send(destinationResponse, valueReturned);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	private Object getParam(String receiveBody) {
		Object param = null;

		switch (listener.consumes()) {
		case MediaType.APPLICATION_JSON:
			param = convertToJson(receiveBody);
			break;
		case MediaType.TEXT_PLAIN:
		default:
			param = receiveBody;
			break;
		}
		return param;
	}

	private Object convertToJson(String receiveBody) {
		Parameter parameter = method.getParameters()[0];

		Class<?> parameterClass = parameter.getType();

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(receiveBody, parameterClass);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isValidString(String text) {
		return text != null && !text.isEmpty();
	}

	private void setContext() {
		try {
			this.ctx = new InitialContext();
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public <T> T lookUp(String jndi) {

		try {
			return (T) ctx.lookup(jndi);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;

	}

}
