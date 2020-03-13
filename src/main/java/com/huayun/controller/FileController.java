package com.huayun.controller;


import com.huayun.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RestController
@RequestMapping("file")
public class FileController {
    @Autowired
    private FileService fileService;

    @RequestMapping(value = "allfiles", method = RequestMethod.GET)
    public ArrayList getAllFiles(@RequestParam String path) {
        return fileService.getAllFiles(path);
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public String upload(@RequestParam String path, @RequestParam String remotePath, @RequestParam MultipartFile file) {
        return fileService.upload(path, remotePath, file);

    }

    @RequestMapping(value = "uploads", method = RequestMethod.POST)
    public String uploads(@RequestParam String path, @RequestParam String remotePath, @RequestParam MultipartFile[] files) {
        return fileService.uploads(path, remotePath, files);
    }
}
