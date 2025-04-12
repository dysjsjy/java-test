package com.dysjsjy.spring;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationContext {

    public ApplicationContext(String packageName) throws IOException {
        initContext(packageName);
    }

    private Map<String, Object> ioc = new HashMap<>();

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    public void initContext(String packageName) throws IOException {
        scanPackage(packageName).stream().filter(this::scanCreate).map(this::wrapper).forEach(this::createBean);
    }

    protected boolean scanCreate(Class<?> type) {
        return type.isAnnotationPresent(Component.class);
    }

    protected void createBean(BeanDefinition beanDefinition) {
        String name = beanDefinition.getName();
        if (ioc.containsKey(name)) {
            return;
        }
        doCreateBean(beanDefinition);
    }

    private void doCreateBean(BeanDefinition beanDefinition) {
        Constructor<?> constructor = beanDefinition.getConstructor();
        Object bean = null;
        try {
            bean = constructor.newInstance();
            Method postconstructMethod = beanDefinition.getPostconstructMethod();
            if (postconstructMethod != null) {
                postconstructMethod.invoke(bean);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ioc.put(beanDefinition.getName(), bean);
    }

    protected BeanDefinition wrapper(Class<?> type) {
        BeanDefinition beanDefinition = new BeanDefinition(type);
        if (beanDefinitionMap.containsKey(beanDefinition.getName())) {
            throw new RuntimeException("bean名字重复");
        }
        beanDefinitionMap.put(beanDefinition.getName(), beanDefinition);

        return beanDefinition;
    }

    public List<Class<?>> scanPackage(String packageName) throws IOException {
        List<Class<?>> classList = new ArrayList<>();
        URL resource = this.getClass().getClassLoader().getResource(packageName.replace(".", File.separator));
        if (resource != null) {
            // 对路径进行解码处理
            String decodedPath = URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8);
            // 去除路径开头的 /
            if (decodedPath.startsWith("/")) {
                decodedPath = decodedPath.substring(1);
            }
            Path path = Path.of(decodedPath);
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path absolutePath = file.toAbsolutePath();
                    if (absolutePath.toString().endsWith(".class")) {
                        String replaceStr = absolutePath.toString().replace(File.separator, ".");
                        int packageIndex = replaceStr.indexOf(packageName);
                        String className = replaceStr.substring(packageIndex, replaceStr.length() - ".class".length());
                        try {
                            classList.add(Class.forName(className));
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return classList;
    }

    public Object getBean(String name) {
        return this.ioc.get(name);
    }

    public <T> T getBean(Class<T> beanType) {
        return this.ioc.values().stream()
                .filter(bean -> beanType.isAssignableFrom(bean.getClass()))
                .map(bean -> (T) bean)
                .findAny()
                .orElse(null);
    }

    public <T> List<T> getBeans(Class<T> beanType) {
        return this.ioc.values().stream()
                .filter(bean -> beanType.isAssignableFrom(bean.getClass()))
                .map(bean -> (T) bean)
                .toList();
    }


}
