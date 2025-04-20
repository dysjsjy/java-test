package com.dysjsjy.redis.DataType.ZSet;

import java.util.*;

public class SkipListZSet {
    private static final int MAX_LEVEL = 16;
    private static final double P = 0.5;

    static class Node {
        String member;
        double score;
        Node[] forward;

        Node(String member, double score, int level) {
            this.member = member;
            this.score = score;
            this.forward = new Node[level];
        }
    }

    private final Node head = new Node(null, 0, MAX_LEVEL);
    private final Map<String, Node> dict = new HashMap<>();
    private int level = 1;
    private final Random random = new Random();

    private int randomLevel() {
        int lvl = 1;
        while (random.nextDouble() < P && lvl < MAX_LEVEL) {
            lvl++;
        }
        return lvl;
    }

    public void add(String member, double score) {
        Node[] update = new Node[MAX_LEVEL];
        Node x = head;
        for (int i = level - 1; i >= 0; i--) {
            while (x.forward[i] != null && compare(x.forward[i], score, member) < 0) {
                x = x.forward[i];
            }
            update[i] = x;
        }

        Node existing = dict.get(member);
        if (existing != null) {
            // 先删除再插入
            remove(member);
        }

        int newLevel = randomLevel();
        if (newLevel > level) {
            for (int i = level; i < newLevel; i++) {
                update[i] = head;
            }
            level = newLevel;
        }

        Node newNode = new Node(member, score, newLevel);
        for (int i = 0; i < newLevel; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }
        dict.put(member, newNode);
    }

    public void remove(String member) {
        Node[] update = new Node[MAX_LEVEL];
        Node x = head;
        for (int i = level - 1; i >= 0; i--) {
            while (x.forward[i] != null && !x.forward[i].member.equals(member)) {
                if (compare(x.forward[i], dict.get(member).score, member) < 0) {
                    x = x.forward[i];
                } else break;
            }
            update[i] = x;
        }

        Node target = dict.get(member);
        if (target == null) return;

        for (int i = 0; i < level; i++) {
            if (update[i].forward[i] != target) break;
            update[i].forward[i] = target.forward[i];
        }

        // 更新 level
        while (level > 1 && head.forward[level - 1] == null) {
            level--;
        }

        dict.remove(member);
    }

    public Double score(String member) {
        Node node = dict.get(member);
        return node == null ? null : node.score;
    }

    public Integer getRank(String member) {
        Node node = dict.get(member);
        if (node == null) return null;

        int rank = 0;
        Node x = head;
        for (int i = level - 1; i >= 0; i--) {
            while (x.forward[i] != null && compare(x.forward[i], node.score, member) < 0) {
                rank += 1; // 这里只是简化版，不统计层间跨度
                x = x.forward[i];
            }
        }
        return rank;
    }

    public List<String> range(int start, int end) {
        List<String> res = new ArrayList<>();
        int rank = 0;
        Node x = head.forward[0];
        while (x != null && rank <= end) {
            if (rank >= start) {
                res.add(x.member);
            }
            x = x.forward[0];
            rank++;
        }
        return res;
    }

    public void printAll() {
        Node x = head.forward[0];
        while (x != null) {
            System.out.println("Member: " + x.member + ", Score: " + x.score);
            x = x.forward[0];
        }
    }

    private int compare(Node node, double score, String member) {
        if (Double.compare(node.score, score) != 0) {
            return Double.compare(node.score, score);
        }
        return node.member.compareTo(member);
    }

    public static void main(String[] args) {
        SkipListZSet zset = new SkipListZSet();
        zset.add("alice", 100);
        zset.add("bob", 120);
        zset.add("carol", 110);
        zset.add("dave", 120);

        zset.printAll();
        System.out.println("Rank of bob: " + zset.getRank("bob"));
        System.out.println("Range 1~3: " + zset.range(1, 3));
        System.out.println("Score of carol: " + zset.score("carol"));

        zset.remove("carol");
        System.out.println("After remove:");
        zset.printAll();
    }
}
