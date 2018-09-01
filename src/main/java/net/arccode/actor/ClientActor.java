package net.arccode.actor;

import akka.actor.UntypedActor;
import com.typesafe.config.ConfigFactory;
import net.arccode.protocol.remote.LocationProtocol;
import net.arccode.protocol.remote.UserProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import scala.collection.JavaConverters.*;

/**
 * 使用远程服务查询区域信息
 *
 * @author http://arccode.net
 * @since 2018-04-17
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientActor extends UntypedActor {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onReceive(Object message) throws Throwable {


        if (message instanceof String) {

            log.info("接收到[{}]指令: {}, 发送方 - {}, 接收方 - {}",
                    "根据区域code查询其下一级别的区域",
                    message,
                    getSender().path(),
                    getSelf().path());

            // 获取远程actor path
            String path = ConfigFactory.defaultApplication().getString("remote.actor" +
                    ".selectLocationsByParentCode");
            // 发送消息
            getContext().actorSelection(path).tell(new LocationProtocol
                            .ExecuteSelectLocationsByParentCode("0"),
                    getSelf());

        } else if (message instanceof LocationProtocol.SelectLocationsByParentCodeMsg) {
            log.info("接收到[{}]指令(响应): {}, 发送方 - {}, 接收方 - {}",
                    "根据区域code查询其下一级别的区域",
                    message,
                    getSender().path(),
                    getSelf().path());

            // TODO 处理业务逻辑
            List<LocationProtocol.Location> locations = scala.collection.JavaConversions
                    .seqAsJavaList(((LocationProtocol.SelectLocationsByParentCodeMsg) message)
                            .subLocations());
            for (LocationProtocol.Location item : locations) {
                log.info("{}", item);
            }

        } else if (message instanceof UserProtocol.AddUserMsg) {
            // 新增用户响应
            log.info("接收到[{}]指令(响应): {}, 发送方 - {}, 接收方 - {}",
                    "新增用户",
                    message,
                    getSender().path(),
                    getSelf().path());
        } else {
            unhandled(message);
        }
    }
}
