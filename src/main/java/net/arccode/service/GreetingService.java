package net.arccode.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 本地业务实现类
 *
 * @author http://arccode.net
 * @since 2018-04-17
 */
@Service
public class GreetingService {

    public String greet(String name) {
        return "Hello World ~ " + name;
    }
}
