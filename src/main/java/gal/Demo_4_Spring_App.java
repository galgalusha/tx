package gal;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.LogManager.getLogManager;

@Component("Demo4_starter_app")
public class Demo_4_Spring_App {

    @Autowired
    Demo_4_Spring_demo actualDemo;

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring-context.xml"});
        BeanFactory factory = context;
        Demo_4_Spring_App app = (Demo_4_Spring_App) factory.getBean("Demo4_starter_app");
        app.run();
    }

    private void run() throws Exception {
        actualDemo.run();
    }

}
