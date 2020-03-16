package com.huayun.controller;


import com.huayun.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;

@RestController
@RequestMapping("file")
public class FileController {
    @Autowired
    private FileService fileService;

    @RequestMapping(value = "createBucket", method = RequestMethod.POST)
    public String createBucket(@RequestParam String rootDir, @RequestParam String bucketName) {
        return fileService.createBucket(rootDir, bucketName);
    }

    /**
     * 列出指定路径下的所有文件
     *
     * @param path
     * @return
     */
    @RequestMapping(value = "allfiles", method = RequestMethod.GET)
    public ArrayList getAllFiles(@RequestParam String path) {
        return fileService.getAllFiles(path);
    }

    /**
     * 模拟单文件上传功能
     *
     * @param path
     * @param remotePath
     * @param file
     * @return
     */

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public String upload(@RequestParam String path, @RequestParam String remotePath, @RequestParam MultipartFile file) {
        return fileService.upload(path, remotePath, file);

    }

    /**
     * 模拟多文件上传功能
     *
     * @param path
     * @param remotePath
     * @param files
     * @return
     */
    @RequestMapping(value = "uploads", method = RequestMethod.POST)
    public String uploads(@RequestParam String path, @RequestParam String remotePath, @RequestParam MultipartFile[] files) {
        return fileService.uploads(path, remotePath, files);
    }

    /**
     * 模拟单文件对象的下载操作
     *
     * @param path
     * @param remotePath 文件下载对象的全路径
     *                   //     * @param localPath
     * @return
     */
    @RequestMapping(value = "download", method = RequestMethod.GET)
    public String download(HttpServletResponse response, @RequestParam String path, @RequestParam String remotePath) {
        String downloadTips = "";
        String downloadPath = path + remotePath;
        File file = new File(downloadPath);
        if (file.exists()) {
            // 配置文件下载
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/force-download");
            String filename = downloadPath.substring(downloadPath.lastIndexOf("/") + 1);
            try {
                // 解决下载文件时文件名乱码问题
                response.addHeader("Content-Disposition",
                        "attachment;filename*=UTF-8''" + URLEncoder.encode(filename, "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 实现文件下载
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return downloadTips;
    }

    /**
     * 模拟多文件下载功能
     *
     * @param response
     * @param path
     * @param remoteObjects
     * @return
     */
    @RequestMapping(value = "downloads", method = RequestMethod.GET)
    public String downloads(HttpServletResponse response, @RequestParam String path, @RequestParam ArrayList<String> remoteObjects) {
        String downloadTips = "";
        for (String remotePath : remoteObjects) {
            downloadTips = download(response, path, remotePath);
        }
        return downloadTips;
    }
}
