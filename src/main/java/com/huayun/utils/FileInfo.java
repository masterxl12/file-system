package com.huayun.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

public class FileInfo {
    private String name;
    private String size;
    private boolean isDir;
    private String createTime;
    private File[] child;

    public FileInfo() {
    }

    public FileInfo(String name, String size, boolean isDir, String createTime, File[] child) {
        this.name = name;
        this.size = size;
        this.isDir = isDir;
        this.createTime = createTime;
        this.child = child;
    }

    public File[] getChild(File[] files) {
        return child;
    }

    public void setChild(File[] child) {
        this.child = child;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "{" +
                "name:'" + name + '\'' +
                ", size:'" + size + '\'' +
                ", isDir:" + isDir +
                ", createTime:'" + createTime + '\'' +
                ", child:" + Arrays.toString(child) +
                '}';
    }
}
