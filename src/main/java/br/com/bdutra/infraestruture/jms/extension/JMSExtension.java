package br.com.bdutra.infraestruture.jms.extension;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;

import br.com.bdutra.annotations.JMSListener;
import br.com.bdutra.infraestruture.jms.ServicoTimer;
import br.com.bdutra.infraestruture.jms.qualifiers.JMSController;

public class JMSExtension implements Extension {

	private List<Bean<?>> JMSBeansList = new ArrayList<Bean<?>>();
	
	private ServicoTimer servicoTimer;

	public <T> void collect(@Observes ProcessBean<T> event) {
		if (event.getAnnotated().isAnnotationPresent(JMSController.class)
				&& event.getAnnotated().isAnnotationPresent(ApplicationScoped.class)) {
			JMSBeansList.add(event.getBean());
		}
	}

	public void load(@Observes AfterDeploymentValidation event, BeanManager beanManager) {

		Bean<? extends Object> timerBean = findBean(beanManager, ServicoTimer.class);
		servicoTimer = (ServicoTimer) createBean(beanManager, timerBean);

		for (Bean<?> bean : JMSBeansList) {

			Object createdBean = createBean(beanManager, bean);

			Method[] declaredMethods = bean.getBeanClass().getDeclaredMethods();

			for (Method method : declaredMethods) {

				boolean isJMSListener = method.isAnnotationPresent(JMSListener.class);
				boolean isPublic = Modifier.isPublic(method.getModifiers());

				if (isPublic && isJMSListener) {
					servicoTimer.registrar(createdBean, method);
				}
			}

		}

	}

	private Bean<? extends Object> findBean(BeanManager beanManager, Class<?> targetClass) {
		return beanManager.resolve(beanManager.getBeans(targetClass));
	}

	private Object createBean(BeanManager beanManager, Bean<?> bean) {
		// Nota: toString() eh importante para instanciar o Bean

		Object reference = beanManager.getReference(bean, bean.getBeanClass(),
				beanManager.createCreationalContext(bean));
		reference.toString();
		return reference;

	}

}
