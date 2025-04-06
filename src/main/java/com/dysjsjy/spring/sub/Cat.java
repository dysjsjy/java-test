package com.dysjsjy.spring.sub;

import com.dysjsjy.spring.Component;

@Component
public class Cat {

    @Autowired
    private Dog dog;

    @PostConstruct
    public void init() {
        System.out.println("Cat创建了 cat里面有一个属性" + dog);
    }
}
