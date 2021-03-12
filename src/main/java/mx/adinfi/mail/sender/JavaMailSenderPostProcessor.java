package mx.adinfi.mail.sender;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class JavaMailSenderPostProcessor implements BeanPostProcessor {

    private boolean authRequired = false;

    public boolean isAuthRequired() {
        return authRequired;
    }

    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
        if (bean instanceof JavaMailSenderImpl) {
            JavaMailSenderImpl sender = (JavaMailSenderImpl) bean;

            String username = sender.getUsername();
            sender.setUsername(!isAuthRequired() || username == null || username.length() < 1 ? null : username);

            String password = sender.getPassword();
            sender.setPassword(!isAuthRequired() || password == null || password.length() < 1 ? null : password);
        }
        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        return bean;
    }

    public void setAuthRequired(boolean authRequired) {
        this.authRequired = authRequired;
    }

}
