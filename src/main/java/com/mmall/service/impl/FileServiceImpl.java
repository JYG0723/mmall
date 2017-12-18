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
     * @return 文件服务器上生成的目标文件名
     */
    @Override
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        // substring 方法指定位置进行切割，抛弃索引的前半部，
        // lastIndexOf 返回指定的子 字符串在字符串中最后一次出现的序号 ab.jpg为2
        // 扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 这里不算多此一举，既+1排除掉"."后又加上"."与扩展品进行拼接。
        // 因为字段意义不同，什么字段就应该是什么样子，也方便后面调用使用，不用再考虑处理的事情
        // 新文件名，防止重名覆盖 A:abc.jgp B:abc.jpg
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始文件上传，上传的文件名:{},上传的路径:{},新文件名:{}", fileName, path, uploadFileName);

        // 声明 目录
        File fileDir = new File(path);
        if (!fileDir.exists()) {// 第一次进来，这么目录不存就创建该目录
            // 对于该路径，赋予可写的权限
            fileDir.setWritable(true);
            // mkdirs 会把该文件所在的绝对路径全部传上去，比如传的是D:\\image\\123.jpg”如果没有image文件夹则会创建一个文件夹出来，然
            // 后将123.jpg上传上去，而mkdir只会传123.jpg。如果image文件夹不存在，那么就会报错
            fileDir.mkdirs();
        }

        // webapp下的upload目录下新建一个name = uploadFileName的空文件，用来装载上传上来的文件
        File targetFile = new File(path, uploadFileName);
        try {
            // 将接收到的文件内容[传输]到给定的目标文件
            file.transferTo(targetFile);
            // 将文件上传到FTP服务器，此时targetFile的内容已被填充
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            // upload文件夹下的文件删除掉，upload空文件夹可以保留
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常!", e);
            return null;
        }
        return targetFile.getName();
    }

}
