package com.read.pan.entity;

import java.util.Date;

/**
 * Created by pan on 2016/5/16
 * 书籍.
 */
public class Book {
    private String bookId;

    /**
     * 书籍名称
     */
    private String bookName;

    /**
     * 书籍作者
     */
    private String author;

    /**
     * 书籍大小
     */
    private long size;

    /**
     * 评价数
     */
    private int numOfComment;

    /**
     * 收藏数
     */
    private int numOfCollect;

    /**
     * 星级
     */
    private float star;

    /**
     * 上架时间
     */
    private Date uploadTime;

    /**
     * 标签
     */
    private String tips;

    /**
     * 类别
     */
    private int type;

    /**
     * 书籍封面
     */
    private String cover;
    /**
     * 书籍全路径
     */
    private String path;
    /**
     * 书籍简介
     */
    private String shortIntroduction;
    /**
     * 书籍介绍
     */
    private String introduction;

    /**
     * 书籍MD5
     */
    private String MD5;

    /**
     * 上传用户id
     */
    private String userId;

    /**
     * 是否已经收藏
     */
    private int ifCollect;
    public Book(){}

    public Book(String bookName, String author, long size, String tips, int type,
                String cover, String path, String shortIntroduction,
                String introduction, String MD5, String userId) {
        this.bookName = bookName;
        this.author = author;
        this.size = size;
        this.tips = tips;
        this.type = type;
        this.cover = cover;
        this.path = path;
        this.shortIntroduction = shortIntroduction;
        this.introduction = introduction;
        this.MD5 = MD5;
        this.userId = userId;
        this.numOfCollect=0;
        this.numOfComment=0;
        this.star=0;
        this.uploadTime=new Date(System.currentTimeMillis());
    }

    public int getIfCollect() {
        return ifCollect;
    }

    public void setIfCollect(int ifCollect) {
        this.ifCollect = ifCollect;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getNumOfComment() {
        return numOfComment;
    }

    public void setNumOfComment(int numOfComment) {
        this.numOfComment = numOfComment;
    }

    public int getNumOfCollect() {
        return numOfCollect;
    }

    public void setNumOfCollect(int numOfCollect) {
        this.numOfCollect = numOfCollect;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getShortIntroduction() {
        return shortIntroduction;
    }

    public void setShortIntroduction(String shortIntroduction) {
        this.shortIntroduction = shortIntroduction;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}