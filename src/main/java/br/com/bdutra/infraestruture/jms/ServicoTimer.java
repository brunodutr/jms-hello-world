package br.com.bdutra.infraestruture.jms;

import static java.lang.String.format;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.com.bdutra.annotations.JMSListener;

@ApplicationScoped
public class ServicoTimer {

	private ManagedScheduledExecutorService schedulerService;

	@PostConstruct
	public void postConstruct() {
		System.out.println("Servico timer instanciado com sucesso!");
	}

	public void registrar(Object target, Method method) {

		JMSListener listener = method.getAnnotation(JMSListener.class);

		String destination = listener.destination();
		String[] strings = destination.split("/");
		String destinationName = strings[strings.length - 1];

		schedulerService = lookUp(format("java:jboss/ee/concurrency/scheduler/%s", destinationName));

		System.out.println("Registrando listener");

		ServicoJMS servicoJMS = new ServicoJMS(target, method);
		schedulerService.scheduleAtFixedRate(servicoJMS, 30000, 100, TimeUnit.MILLISECONDS);

	}

	@SuppressWarnings("unchecked")
	public <T> T lookUp(String jndi) {

		try {
			return (T) new InitialContext().lookup(jndi);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;

	}

}
