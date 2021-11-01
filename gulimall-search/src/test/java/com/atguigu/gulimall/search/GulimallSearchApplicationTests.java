package com.atguigu.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.search.config.ElasticSerachConfig;
import lombok.Data;
import net.minidev.json.JSONValue;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class GulimallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    /**
     * 创建检索，按照年龄聚合，并且请求这些年龄段的这些人的平均薪资
     */
    @Test
    void searchData() throws IOException {
        //1.创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("bank");
        //2.指定DSL，检索条件SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        //2.1)、按照年龄的值分布进行聚合
        TermsAggregationBuilder ageAgg =
                AggregationBuilders.terms("ageAgg").field("age").size(10);//使用工具类构造聚合条件
        searchSourceBuilder.aggregation(ageAgg);
        //2.2)、计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);

        System.out.println("检索条件" + searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        //3.执行检索
        SearchResponse searchResponse = client.search(searchRequest, ElasticSerachConfig.COMMON_OPTIONS);

        //4.分析结果SearchResponse
        System.out.println(searchResponse.toString());
        //获取所有查到的数据
        SearchHits hits = searchResponse.getHits();
        SearchHit[] serachHits = hits.getHits();
        for (SearchHit hit : serachHits) {
//            index " : " bank ",
//            "_type" :"account",
//            "_id" :"970",
//            "_score" :5.4032025,
//            "_source" :{
//            hit.getIndex();hit.getType();hit.getId();
            String source = hit.getSourceAsString();
            Account account = JSON.parseObject(source, Account.class);   //使用fastJSON转换为对象
            System.out.println("account:" + account);
        }

        //4.2)、获取这次检索到的分析信息
        Aggregations aggregations = searchResponse.getAggregations();
        List<Aggregation> aggList = aggregations.asList();   //将聚合的信息转换为List
        for (Aggregation aggregation : aggList){
            System.out.println("当前聚合的名称" + aggregation.getName());
            System.out.println("当前聚合的类型" + aggregation.getType());
        }

        Terms ageAggregation = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAggregation.getBuckets()){
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄" + keyAsString);
        }

        Avg bAvg = aggregations.get("balanceAvg");
        System.out.println("平均薪资" + bAvg.getValue());

    }

    /**
     * 测试存储数据到ES中
     */
    @Test
    void indexData() throws IOException {
        IndexRequest request = new IndexRequest("users");
        request.id("1");
        User user = new User();
        user.setUserName("zhangsan");
        user.setUserAge(23);
        user.setGender("男");
        String jsonString = JSONValue.toJSONString(user);
        request.source(jsonString, XContentType.JSON);  //要保存的内容 和 内容类型

        //执行操作
        IndexResponse index = client.index(request, ElasticSerachConfig.COMMON_OPTIONS);
        //提取有用的响应数据
        System.out.println(index);
    }

    @Test
    void contextLoads() {
        System.out.println("RestHighLevelClient===============>" + client);
    }

}

@Data
class User {
    private String userName;
    private Integer userAge;
    private String gender;
}

@Data
class Account {
    private int account_number;
    private int balance;
    private String firstname;
    private String lastname;
    private int age;
    private String gender;
    private String address;
    private String employer;
    private String email;
    private String city;
    private String state;
}
