package com.dysjsjy.spring.web;

import tech.insight.spring.Component;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Controller
@Component
@RequestMapping("/hello")
public class HelloController {

    @RequestMapping("/a")
    public String hello(@Param("name") String name, @Param("age") Integer age) {
        return String.format("<h1>hello world</h1><br> name: %s  age:%s", name, age);
    }

    @RequestMapping("/json")
    @ResponseBody
    public User json(@Param("name") String name, @Param("age") Integer age) {
        User user = new User();
        user.setAge(age);
        user.setName(name);
        return user;
    }

    @RequestMapping("/html")
    public ModelAndView html(@Param("name") String name, @Param("age") Integer age) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView("index.html");
        modelAndView.getContext().put("name", name);
        modelAndView.getContext().put("age", age.toString());
        return modelAndView;
    }

    @RequestMapping("/aa")
    public String hellao() {
        return "sadf";
    }


}
