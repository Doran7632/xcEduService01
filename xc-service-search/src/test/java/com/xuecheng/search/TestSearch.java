package com.xuecheng.search;


import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
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

    /*
    * multi_match全文检索，如:条件（name:spring）在name字段中能查询到，在description字段中能查询到就需要用这个查询
    * 搜索出来的结果会有得分，举例：条件为（name:spring css），另一条索引数据为(name:spring开发框架，description字段有两个“spring”)，另一条为（name:Bootstrap框架，description字段有五个css）
    * 那么根据现有条件查询出来的结果，第二条数据排在前列，因为css字段有五个。
    * 但是我们将name字段的权重加大（name^10），那么第一条的name匹配spring，权重又加大，所以排在了前列
    * */
    @Test
    public void testSearchByMultiMatch() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //搜索方式,这里的minimumShouldMatch表示，我需要查询包含“spring开发框架”的数据，这里条件被分为了3个词，那么3*0.8=2.4  然后向上取整为2，
        //表示3个词至少两个词在文档中存在再搜索出来
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring css","name","description")
                .minimumShouldMatch("80%")
                .field("name",10));
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


    /*布尔查询，满足一个或多个字段匹配查询，又满足一个字段精确查询
    * must 表示必须，两个条件必须满足
    * should 表示或者，可以满足一个条件
    * must_not 表示都不满足
    * */
    @Test
    public void testSearchByBool() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //定义MultiMatchQueryBuilder
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("80%")
                .field("name", 10);

        //定义termQuery
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");

        //定义boolQuery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQueryBuilder);
        boolQueryBuilder.must(multiMatchQueryBuilder);
        //搜索方式
        searchSourceBuilder.query(boolQueryBuilder);
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


    /**
     * 过滤器查询，比较常用，性能很高
     * 过滤器查询是根据结果项进行过滤的，比方说我查询根据name查询出来两个数据，那么我过滤studymodel为201001的数据保留
     * 就可以使用过滤器
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testSearchByFilter() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //定义MultiMatchQueryBuilder
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("80%")
                .field("name", 10);

        //定义termQuery

        //定义boolQuery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));//表示大于60，小于100
        //搜索方式
        searchSourceBuilder.query(boolQueryBuilder);
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


    /**
     * 排序
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testSearchBySort() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //定义boolQuery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));//表示大于60，小于100
        //搜索方式
        searchSourceBuilder.query(boolQueryBuilder);
        //添加排序
        searchSourceBuilder.sort("studymodel", SortOrder.DESC);
        searchSourceBuilder.sort("price", SortOrder.ASC);
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

    /**
     * 高亮显示
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testSearchByHighlight() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置搜索条件
        //定义MultiMatchQueryBuilder
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("开发框架", "name", "description")
                .minimumShouldMatch("80%")
                .field("name", 10);
        //定义boolQuery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        //定义过滤器
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));//表示大于60，小于100
        //搜索方式
        searchSourceBuilder.query(boolQueryBuilder);

        //添加排序
        searchSourceBuilder.sort("studymodel", SortOrder.DESC);
        searchSourceBuilder.sort("price", SortOrder.ASC);
        //设置源字段过滤，第一个参数表示包含哪些字段，第二个表示不包含
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});

        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<tag>");
        highlightBuilder.postTags("</tag>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);


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

                //获取高亮字段
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if(highlightFields != null){
                    HighlightField nameLightField = highlightFields.get("name");
                    if(nameLightField != null){
                        Text[] fragments = nameLightField.getFragments();
                        StringBuffer stringBuffer = new StringBuffer();
                        for (Text text : fragments) {
                            stringBuffer.append(text);
                        }
                        name = stringBuffer.toString();
                    }
                }

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
