package net.arccode.foundation;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import org.springframework.context.ApplicationContext;

/**
 * 获取actor
 *
 * @author http://arccode.net
 * @since 2018-04-17
 */
public class SpringActorProducer implements IndirectActorProducer {

    private ApplicationContext applicationContext;
    private String beanActorName;

    public SpringActorProducer(ApplicationContext applicationContext, String beanActorName) {
        this.applicationContext = applicationContext;
        this.beanActorName = beanActorName;
    }

    @Override
    public Actor produce() {
        return (Actor) applicationContext.getBean(beanActorName);
    }

    @Override
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext.getType(beanActorName);
    }
}
