package com.dysjsjy.迭代器;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        File file = new File("src/main/java/com/dysjsjy/迭代器/demo.user");
        UserFile userFile = new UserFile(file);
        for (User user : userFile) {
            System.out.println(user.toString());
        }
    }

    void f1() {
        List<String> list = new ArrayList<>();

        User user = new User("小明", "20");
        for (Object s : user) {
            System.out.println((String) s);
        }
    }

    void f2() {
        try {
            List<String> list = Files.readAllLines(new File("src/main/java/com/dysjsjy/迭代器/demo.user").toPath());
            for (String s : list) {
                s = s.substring(1, s.length() - 1);
                String[] split = s.split(",");
                System.out.println("user:" + split[0]);
                System.out.println("age:" + split[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
