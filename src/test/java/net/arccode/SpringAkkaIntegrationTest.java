package net.arccode;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import com.typesafe.config.ConfigFactory;
import junit.framework.Assert;
import net.arccode.actor.GreetingActor;
import net.arccode.foundation.AppConfiguration;
import net.arccode.protocol.remote.ExceptionProtocol;
import net.arccode.protocol.remote.LocationProtocol;
import net.arccode.protocol.remote.UserProtocol;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.UUID;
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

        client.tell("tell", null);

        Thread.sleep(1000);
    }

    /**
     * 发送消息给actor 并获取响应 - 远程(ask 模式)
     *
     * @throws Exception
     */
    @Test
    public void addUser() throws Exception {

        String path = ConfigFactory.defaultApplication().getString("remote.actor" +
                ".addUser");

        ActorSelection client = system.actorSelection(path);

        // 设置超时时间
        FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);

        // 使用ask模式发送消息
        Future<Object> resultFuture = ask(client, new UserProtocol.ExecuteAddUser("13600000028",
                "111111"), timeout);

        // 阻塞式获取结果
        Object result = Await.result(resultFuture, duration);

        if (result instanceof UserProtocol.AddUserMsg) {
            log.info("插入成功 - {}", result);
        } else if (result instanceof ExceptionProtocol.AkkaApiException) {
            log.error("插入异常 - {}", result);

            ExceptionProtocol.AkkaApiException e = (ExceptionProtocol.AkkaApiException) result;

            log.error("code - {}, message - {}, error - {}", e.code(), e.message(), e.error());
        } else {
            log.error("{}", "位置的响应内容");
        }


    }

    /**
     * 发送消息给actor 并获取响应 - 远程(ask 模式)
     *
     * @throws Exception
     */
    @Test
    public void queryUser() throws Exception {

        String path = "akka.tcp://gyenno-service-module-user@userakkadev.gyenno" +
                ".com:10001/user/queryUser";
//        String path = ConfigFactory.defaultApplication().getString("remote.actor" +
//                ".queryUser");

        ActorSelection client = system.actorSelection(path);

        // 设置超时时间
        FiniteDuration duration = FiniteDuration.create(5, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);

        String msgId = UUID.randomUUID().toString().replaceAll("-", "");

        // 使用ask模式发送消息
        Future<Object> resultFuture = ask(client, new com.gyenno.module.user.protocol
                .UserProtocol.ExecuteQueryUser(
                msgId,
                1020,
                "ROLE_DOCTOR"), timeout);

        // 阻塞式获取结果
        Object result = Await.result(resultFuture, duration);

        if (result instanceof com.gyenno.module.user.protocol.UserProtocol.QueryUserMsg) {
            log.info("调用成功 - {}", result);
        } else if (result instanceof ExceptionProtocol.AkkaApiException) {
            log.error("调用异常 - {}", result);

            ExceptionProtocol.AkkaApiException e = (ExceptionProtocol.AkkaApiException) result;

            log.error("code - {}, message - {}, error - {}", e.code(), e.message(), e.error());
        } else {

            log.error("{}, {}", "未知的响应内容", result);
        }


    }

    /**
     * 发送消息给actor 并获取响应 - 远程(ask 模式)
     *
     * @throws Exception
     */
    @Test
    public void parseToken() throws Exception {

        String path = ConfigFactory.defaultApplication().getString("remote.actor.parseToken");

        ActorSelection client = system.actorSelection(path);

        // 设置超时时间
        FiniteDuration duration = FiniteDuration.create(5, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);

        String msgId = UUID.randomUUID().toString().replaceAll("-", "");

        // 使用ask模式发送消息
        Future<Object> resultFuture = ask(client, new com.gyenno.module.user.protocol
                .UserProtocol.ExecuteParseToken(
                msgId,
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                        ".eyJzdWIiOiIxNTgxNDc1MTc3MSIsInVzZXJJZCI6MTAwNywidXNlclR5cGUiOjIsInNpZ25JbkNoYW5uZWwiOjEsInBhc3N3b3JkIjoiRTEwQURDMzk0OUJBNTlBQkJFNTZFMDU3RjIwRjg4M0UifQ.HxJEE0Z1dVP3Yiaih6VWGN2dc9RhtHHNiFjBA6gJsKI"), timeout);

        // 阻塞式获取结果
        Object result = Await.result(resultFuture, duration);

        if (result instanceof com.gyenno.module.user.protocol.UserProtocol.ParseTokenMsg) {
            log.info("调用成功 - {}", result);
        } else if (result instanceof ExceptionProtocol.AkkaApiException) {
            log.error("调用异常 - {}", result);

            ExceptionProtocol.AkkaApiException e = (ExceptionProtocol.AkkaApiException) result;

            log.error("code - {}, message - {}, error - {}", e.code(), e.message(), e.error());
        } else {

            log.error("{}, {}", "未知的响应内容", result);
        }


    }
}
