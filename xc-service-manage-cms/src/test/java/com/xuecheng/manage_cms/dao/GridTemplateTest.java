package com.xuecheng.manage_cms.dao;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GridTemplateTest {

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;



    //保存文件到gridFS中
    @Test
    public void testRestTempalte() throws FileNotFoundException {
        //定义file
        File file = new File("d:/index_banner.ftl");
        //定义流
        FileInputStream inputStream = new FileInputStream(file);
        ObjectId store = gridFsTemplate.store(inputStream, "index_banner.ftl");
        System.out.println(store);
    }


    //因为为该项目未开发课程模板管理，所以这里使用junit来上传文件到GridFS中
    @Test
    public void testStore2() throws FileNotFoundException {
        //定义file
        File file = new File("D:\\staticWorkSpace\\static\\template\\course.ftl");
        //定义流
        FileInputStream inputStream = new FileInputStream(file);
        ObjectId store = gridFsTemplate.store(inputStream, "课程详情模板文件.ftl");
        System.out.println(store);
    }



    //取出文件
    @Test
    public void testFsBucket() throws IOException {
        //根据id查询文件对象
        //GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("5d7a079ddfe4ae4bb897420e")));
        GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("5a7719d76abb5042987eec3b")));
        //打开一个下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFsFile.getObjectId());
        //创建Resource对象，获取流
        GridFsResource gridFsResource = new GridFsResource(gridFsFile,gridFSDownloadStream);
        String content  = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(content);


    }
}
