package com.dysjsjy.spring.sub;

import tech.insight.spring.BeanPostProcessor;
import tech.insight.spring.Component;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object afterInitializeBean(Object bean, String beanName) {
        System.out.println(beanName + "初始化完成");
        return bean;
    }
}
