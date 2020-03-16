package com.huayun.controller;


import com.huayun.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("file")
public class FileController {
    @Autowired
    private FileService fileService;


    private static final int BUFFER_SIZE = 2 * 1024;


    /**
     * 模拟新建文件夹
     *
     * @param rootDir
     * @param bucketName
     * @return
     */
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
     * 模拟单文件对象删除
     *
     * @param bucketName
     * @param objectName
     * @return
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public String delete(@RequestParam String bucketName, @RequestParam String objectName) {
        return fileService.delete(bucketName, objectName);
    }

    /**
     * 模拟多文件对象批量删除
     *
     * @param bucketName
     * @param objects
     * @return
     */
    @RequestMapping(value = "deletefiles", method = RequestMethod.POST)
    public String deleteFiles(@RequestParam String bucketName, @RequestParam String[] objects) {
        return fileService.deletefiles(bucketName, objects);
    }

    /**
     * 多文件下载，打包出压缩包
     *
     * @param response
     * @param bucketName    指定文件夹
     * @param remoteObjects 文件对象列表
     * @param localPath     压缩包下载的路径
     * @throws RuntimeException
     * @throws FileNotFoundException
     */
    @RequestMapping(value = "/downloadZip", method = RequestMethod.GET)
    public void downloadZip(HttpServletResponse response,
                            @RequestParam String bucketName,
                            @RequestParam String[] remoteObjects,
                            @RequestParam String localPath
    ) throws RuntimeException, FileNotFoundException {
        //设置最终输出zip文件的目录+文件名
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日HH时mm分");
        String zipFileName = formatter.format(new Date()) + ".zip";
        FileOutputStream out = new FileOutputStream(new File(localPath + zipFileName));
        long start = System.currentTimeMillis();
        List<File> srcFiles = new ArrayList<>();
        for (String s : remoteObjects) {
            File file = new File(bucketName + s);
            srcFiles.add(file);
        }
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
