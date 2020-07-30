package com.xuecheng.search;


import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {

    @Autowired
    RestHighLevelClient highLevelClient;

    @Autowired
    RestClient restClient;

    @Test
    public void testDeleteIndex() throws IOException {
        //创建删除索引对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
        //操作索引的客户端
        IndicesClient indicesClient = highLevelClient.indices();
        //执行删除索引
        DeleteIndexResponse indexResponse = indicesClient.delete(deleteIndexRequest);
        //得到响应
        boolean acknowledged = indexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }

}
