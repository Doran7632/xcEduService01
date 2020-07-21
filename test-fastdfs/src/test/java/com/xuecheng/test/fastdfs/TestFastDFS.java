package com.xuecheng.test.fastdfs;

import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {

    //上传文件，
    @Test
    public void Upload(){
        try{
            //加载properties文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义一个tracker
            TrackerClient trackerClient = new TrackerClient();
            //获取tracker连接
            TrackerServer connection = trackerClient.getConnection();
            //获取stroage
            StorageServer storeStorage = trackerClient.getStoreStorage(connection);
            //创建stroageClient1.
            StorageClient1 stroageClient1 = new StorageClient1(connection,storeStorage);
            //上传
            String filepath = "d:/logo.png";
            String fileId = stroageClient1.upload_file1(filepath, "png", null);
            System.out.println(fileId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //下载文件
    @Test
    public void downloadFi(){
        try{
            //加载properties文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义一个tracker
            TrackerClient trackerClient = new TrackerClient();
            //获取tracker连接
            TrackerServer connection = trackerClient.getConnection();
            //获取stroage
            StorageServer storeStorage = trackerClient.getStoreStorage(connection);
            //创建stroageClient1.
            StorageClient1 stroageClient1 = new StorageClient1(connection,storeStorage);
            //下载
            String fileid = "group1/M00/00/00/wKhRgF8WffOAJ2fIAAEuxCKOi1E148.png";
            byte[] bytes = stroageClient1.download_file1(fileid);
            //使用输出流来保存文件
            FileOutputStream fileOutputStream = new FileOutputStream(new File("f:/log.png"));
            fileOutputStream.write(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
