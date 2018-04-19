package net.arccode;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import com.typesafe.config.ConfigFactory;
import junit.framework.Assert;
import net.arccode.actor.GreetingActor;
import net.arccode.foundation.AppConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static net.arccode.foundation.SpringExtension.SPRING_EXTENSION_PROVIDER;
import static akka.pattern.Patterns.ask;

/**
 * spring akka 本地actor集成测试
 *
 * @author http://arccode.net
 * @since 2018-04-17
 */
@ContextConfiguration(classes = AppConfiguration.class)
public class SpringAkkaIntegrationTest extends AbstractJUnit4SpringContextTests {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ActorSystem system;

    /**
     * 发送消息给actor 并获取响应 - 本地
     *
     * @throws Exception
     */
    @Test
    public void sendAndReplyLocal() throws Exception {

        // 获取actor引用
        ActorRef greeter = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system).props
                ("greetingActor"), "greeter");

        // 设置超时时间
        FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);

        // 使用ask模式发送消息
        Future<Object> resultFuture = ask(greeter, new GreetingActor.Greet("AC"), timeout);

        // 阻塞式获取结果
        Object result = Await.result(resultFuture, duration);

        Assert.assertEquals("Hello World ~ AC", result);
    }

    /**
     * 发送消息给actor 并获取响应 - 远程(tell 模式)
     *
     * @throws Exception
     */
    @Test
    public void sendAndReplyRemote() throws Exception {

        // 获取actor引用
        ActorRef client = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system).props
                ("clientActor"), "client");

        String path = ConfigFactory.defaultApplication().getString("remote.actor" +
                ".selectLocationsByParentCode");

        client.tell("tella", null);

        Thread.sleep(1000);
    }
}
