<%--
  Created by IntelliJ IDEA.
  User: JiYongGuang
  Date: 2017/11/4
  Time: 1:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome</title>
</head>
<body>
<h1>SpringMVC文件上传</h1>
<form name="springmvc" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file">
    <input type="submit" name="springmvc上传文件">
</form>
<h1>富文本图片上传</h1>
<form name="springmvc" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file">
    <input type="submit" name="富文本图片上传">
</form>
</body>
</html>
