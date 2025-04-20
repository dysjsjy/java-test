package com.dysjsjy.spring.sub;

import tech.insight.spring.Autowired;
import tech.insight.spring.Component;
import tech.insight.spring.PostConstruct;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Component
public class Cat {

    @Autowired
    private Dog dog;

    @PostConstruct
    public void init() {
        System.out.println("Cat创建了 cat里面有一个属性" + dog);
    }

}
