package net.arccode.foundation;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static net.arccode.foundation.SpringExtension.SPRING_EXTENSION_PROVIDER;

/**
 * 配置类
 *
 * @author http://arccode.net
 * @since 2018-04-17
 */
@Configuration
@ComponentScan(basePackages = {"net.arccode"})
public class AppConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create("akka-remote-client-java", ConfigFactory.load());
        SPRING_EXTENSION_PROVIDER.get(system).initialize(applicationContext);

        return system;
    }
}
