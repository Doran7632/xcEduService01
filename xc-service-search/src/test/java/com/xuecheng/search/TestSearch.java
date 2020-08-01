package com.xuecheng.search;


import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {

    //该客户端是高等级客户端
    @Autowired
    RestHighLevelClient highLevelClient;

    //该客户端是低等级客户端
    @Autowired
    RestClient restClient;

    @Test
    public void testSearch() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //搜索方式
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置源字段过滤，第一个参数表示包含哪些字段，第二个表示不包含
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});

        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = highLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");

        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //前面由于设置过滤，所以这里查询不出来description
            String description = (String) sourceAsMap.get("description");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }
    }

    //分页查询
    @Test
    public void testSearchByPage() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
            int page = 1;
            int size = 1;
            int from = (page -1 )*size;
            searchSourceBuilder.from(from);//起始记录下标，从0开始
            searchSourceBuilder.size(size);//每页显示的记录数
        //搜索方式
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置源字段过滤，第一个参数表示包含哪些字段，第二个表示不包含
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});

        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = highLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");

        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //前面由于设置过滤，所以这里查询不出来description
            String description = (String) sourceAsMap.get("description");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }
    }

    //Term精确查询，将查询条件精确匹配，如：条件为(name:spring)，那么就可能查询出(name:spring开发框架)的数据,“spring开发框架”分词了，
    //条件中的spring和分词后spring的精确匹配,这里的精确匹配表示的是不对条件分词，但是能对索引分词
    @Test
    public void testSearchByTerm() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        int page = 1;
        int size = 1;
        int from = (page -1 )*size;
        searchSourceBuilder.from(from);//起始记录下标，从0开始
        searchSourceBuilder.size(size);//每页显示的记录数
        //搜索方式
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置源字段过滤，第一个参数表示包含哪些字段，第二个表示不包含
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});

        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = highLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");

        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //前面由于设置过滤，所以这里查询不出来description
            String description = (String) sourceAsMap.get("description");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }
    }

    //term查询id
    @Test
    public void testSearchByTermIds() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //搜索方式,注意：查询id的话，termQuery方法需要改成termsQuery
        String[] ids = new String[]{"1","2"};
        searchSourceBuilder.query(QueryBuilders.termsQuery("_ids",ids));
        //设置源字段过滤，第一个参数表示包含哪些字段，第二个表示不包含
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});

        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = highLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");

        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //前面由于设置过滤，所以这里查询不出来description
            String description = (String) sourceAsMap.get("description");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }
    }

    //match全文检索，
    @Test
    public void testSearchByMatch() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //搜索方式,这里的minimumShouldMatch表示，我需要查询包含“spring开发框架”的数据，这里条件被分为了3个词，那么3*0.8=2.4  然后向上取整为2，
        //表示3个词至少两个词在文档中存在再搜索出来
        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发框架").minimumShouldMatch("80%"));
        //设置源字段过滤，第一个参数表示包含哪些字段，第二个表示不包含
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});

        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = highLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");

        for (SearchHit hit : searchHits) {
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //前面由于设置过滤，所以这里查询不出来description
            String description = (String) sourceAsMap.get("description");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(price);
            System.out.println(timestamp);
        }
    }

}
