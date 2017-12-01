package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author: Ji YongGuang.
 * @date: 23:38 2017/11/29.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 文件上传功能
     *
     * @param file 上传的文件
     * @param path 上传的路径
     * @return
     */
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        // 扩展名
        // substring 方法指定位置进行切割，抛弃索引的前半部，
        // lastIndexOf 方法指定从该字符串的尾部开始倒序查询。返回该字符第一次出现的索引 ab.jpg为2
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 这里不算多此一举，既+1排除掉"."后又加上"."与扩展品进行拼接。
        // 因为字段意义不同，什么字段就应该是什么样子，也方便后面调用使用，不用再考虑处理的事情
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始文件上传，上传的文件名:{},上传的路径:{},新文件名:{}", fileName, path, uploadFileName);

        // 声明 目录
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            // 对于该路径，赋予可写的权限
            fileDir.setWritable(true);
            // mkdirs 会把该文件所在的绝对路径全部传上去，比如a文件夹下的b文件，上传b文件的时候a文件夹也会被创建。而mkdir只会传b
            fileDir.mkdirs();
        }
        // 如果当前文件未存在 new File() 操作如果传入的path="" 则fileDiir不存在
        File targetFile = new File(path, uploadFileName);
        try {
            // 将接收到的文件[传输]到给定的目标文件
            file.transferTo(targetFile);
            // 运行到这里代表，文件上传成功
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            // 运行到这里代表，已经上传到FTP服务器上
            targetFile.delete();
            // 运行到这里代表，upload文件夹下的文件已经删除，upload空文件夹可以保留
        } catch (IOException e) {
            logger.error("上传文件异常!", e);
            return null;
        }
        // 断点跟一下，看看这个文件名是怎么出来的
        return targetFile.getName();
    }

}
