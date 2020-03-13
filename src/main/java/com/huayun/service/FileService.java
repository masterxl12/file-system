package com.huayun.service;

import com.huayun.utils.FileInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

@Component
public class FileService {

    private String 文件上传失败;

    /**
     * 获取指定文件夹下的文件
     *
     * @param path String 文件路径
     * @return
     */
    public ArrayList<FileInfo> getAllFiles(String path) {
        File file = new File(path);
        File[] tempList = file.listFiles();
        ArrayList<FileInfo> fileNameList = new ArrayList<>();

        for (int i = 0; i < tempList.length; i++) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setName(tempList[i].getName());
            fileInfo.setCreateTime(FormatDate(tempList[i]));
            if (!(tempList[i].isHidden())) {
                if (tempList[i].isFile()) { // 判断是文件
                    fileInfo.setSize(FormetFileSize(tempList[i].length()));
                    fileInfo.setDir(false);
                }
                if (tempList[i].isDirectory()) { // 判断是文件夹
                    fileInfo.setSize(null);
                    fileInfo.setDir(true);
                }
            }
            fileNameList.add(fileInfo);
        }
        //      getAllFiles(tempList[i].getPath());
        return fileNameList;
    }

    /**
     * 模拟单文件上传
     *
     * @param path       模拟文件夹(桶名)
     * @param remotePath 远程的文件路径
     * @param file       本地的文件
     * @return
     */
    public String upload(String path, String remotePath, MultipartFile file) {
        String uploadTips = "";
        // File f = new File(path + remotePath);
        String fileName = file.getOriginalFilename();
        String uploadPath = path + remotePath + "/" + fileName;
        File target = new File(uploadPath);
        if (!target.getParentFile().exists()) { // 判断远程文件夹对象是否存在
            target.getParentFile().mkdirs();
        }
        try {
            file.transferTo(target);
            uploadTips = "上传成功";
        } catch (IOException e) {
            e.printStackTrace();
            uploadTips = "error，文件上传失败";
        }
        return uploadTips;
    }

    /**
     * 模拟多文件上传
     *
     * @param path
     * @param remotePath
     * @param files
     * @return
     */
    public String uploads(String path, String remotePath, MultipartFile[] files) {
        String uploadTips = "";
        // File f = new File(path + remotePath);
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String uploadPath = path + remotePath + "/" + fileName;
            File target = new File(uploadPath);
            if (!target.getParentFile().exists()) { // 判断远程文件夹对象是否存在
                target.getParentFile().mkdirs();
            }
            try {
                file.transferTo(target);
                uploadTips += "OK," + fileName + "上传成功" + "\n";
            } catch (IOException e) {
                e.printStackTrace();
                uploadTips = "error，文件上传失败";
            }
        }
        return uploadTips;
    }


    // 转换文件大小
    public String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    // 日期时间转换
    public String FormatDate(File file) {
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = df.format(new Date(file.lastModified()));
        return dateTime;
    }

    public static void main(String[] args) {
        FileService fileService = new FileService();
        String path = "/Users/masterxl/Desktop/";
        String remotePath = "testfile";
//        System.out.println(fileService.upload(path, remotePath));
    }
}

