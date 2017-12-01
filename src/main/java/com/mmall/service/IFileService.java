package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author: Ji YongGuang.
 * @date: 23:38 2017/11/29.
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
