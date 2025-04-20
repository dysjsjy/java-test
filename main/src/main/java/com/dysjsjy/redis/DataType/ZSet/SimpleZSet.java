package com.dysjsjy.redis.DataType.ZSet;

import java.util.*;

public class SimpleZSet {
    private static class Node {
        String member;
        double score;

        Node(String member, double score) {
            this.member = member;
            this.score = score;
        }
    }

    // 排序集合：根据 score 升序 + member 字典序
    private final TreeSet<Node> skiplist;
    // 快速查找表：member -> score
    private final Map<String, Node> dict;

    public SimpleZSet() {
        this.dict = new HashMap<>();
        this.skiplist = new TreeSet<>((a, b) -> {
            if (Double.compare(a.score, b.score) != 0) {
                return Double.compare(a.score, b.score);
            }
            return a.member.compareTo(b.member);
        });
    }

    public void add(String member, double score) {
        if (dict.containsKey(member)) {
            Node oldNode = dict.get(member);
            skiplist.remove(oldNode); // 删除旧的
        }
        Node newNode = new Node(member, score);
        dict.put(member, newNode);
        skiplist.add(newNode);
    }

    public void remove(String member) {
        Node node = dict.remove(member);
        if (node != null) {
            skiplist.remove(node);
        }
    }

    public Double score(String member) {
        Node node = dict.get(member);
        return node == null ? null : node.score;
    }

    public Integer getRank(String member) {
        Node node = dict.get(member);
        if (node == null) return null;
        int rank = 0;
        for (Node n : skiplist) {
            if (n.member.equals(member)) return rank;
            rank++;
        }
        return null; // 不存在
    }

    public List<String> range(int startRank, int endRank) {
        List<String> result = new ArrayList<>();
        int i = 0;
        for (Node node : skiplist) {
            if (i >= startRank && i <= endRank) {
                result.add(node.member);
            }
            if (i > endRank) break;
            i++;
        }
        return result;
    }

    public void printAll() {
        for (Node node : skiplist) {
            System.out.println("Member: " + node.member + ", Score: " + node.score);
        }
    }

    public static void main(String[] args) {
        SimpleZSet zset = new SimpleZSet();
        zset.add("alice", 100);
        zset.add("bob", 120);
        zset.add("carol", 110);
        zset.add("dave", 120);

        zset.printAll();
        System.out.println("Rank of bob: " + zset.getRank("bob"));
        System.out.println("Range 0~2: " + zset.range(0, 2));
        System.out.println("Score of alice: " + zset.score("alice"));

        zset.remove("carol");
        zset.printAll();
    }
}

