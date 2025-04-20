package com.dysjsjy.spring.sub;

import tech.insight.spring.Autowired;
import tech.insight.spring.Component;
import tech.insight.spring.PostConstruct;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Component(name = "mydog")
public class Dog {


    @Autowired
    Cat cat;

    @Autowired
    Dog dog;

    @PostConstruct
    public void init() {
        System.out.println("Dog 创建完成了 里面有一只猫" + cat);
        System.out.println("Dog 创建完成了 里面有一只狗" + dog);
    }
}
