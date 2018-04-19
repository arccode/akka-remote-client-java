package net.arccode.api;

import net.arccode.protocol.remote.LocationProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用接口
 *
 * @author http://arccode.net
 * @since 2018-04-16
 */
@RestController
public class CommApi {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/comm/ok")
    public String hello() {

        return "OK";
    }
}