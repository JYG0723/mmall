package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author: Ji YongGuang.
 * @date: 23:38 2017/11/29.
 */
public interface IFileService {

    /**
     * 文件上传功能
     *
     * @param file 上传的文件
     * @param path 上传的路径
     * @return 文件服务器上生成的目标文件名
     */
    String upload(MultipartFile file, String path);
}
