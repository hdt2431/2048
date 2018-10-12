package com.hdt.a2048.Model;


public class Gamer {

    private int id;
    private String name;
    private int score;
    private String time;

    public Gamer(int id, String name, int score, String time) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.time = time;
    }

    //获取游戏者id
    public int getId() {
        return id;
    }

    //设置游戏者id
    public void setId(int id) {
        this.id = id;
    }

    //获取游戏者姓名
    public String getName() {
        return name;
    }

    //设置游戏者姓名
    public void setName(String name) {
        this.name = name;
    }

    //获取游戏者分数
    public int getScore() {
        return score;
    }

    //设置游戏者分数
    public void setScore(int score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
