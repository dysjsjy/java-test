package com.dysjsjy.迭代器;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

// 关键词：Iterable, File, cursor, iterator, UserFileIterator, Files.readAllLines, hasNext, next,

public class UserFile implements Iterable<User> {

    private final File file;

    int cursor = 0;

    public UserFile(File file) {
        this.file = file;
    }

    @Override
    public Iterator<User> iterator() {
        return new UserFileIterator();
    }

    class UserFileIterator implements Iterator<User> {

        List<User> userList = loadUsersFromFile();

        private List<User> loadUsersFromFile() {
            try {
                return Files.readAllLines(file.toPath()).stream().map((line) -> {
                    String midString = line.substring(1, line.length() - 1);
                    String[] split = midString.split(",");
                    return new User(split[0], split[1]);
                }).collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public boolean hasNext() {
            return cursor != userList.size();
        }

        @Override
        public User next() {
            if (cursor >= userList.size()) {
                throw new NoSuchElementException();
            }
            int currentIndex = cursor;
            cursor++;
            return userList.get(currentIndex);
        }
    }
}
