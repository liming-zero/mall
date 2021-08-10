package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.config.ElasticSerachConfig;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.feign.ProductFeignService;
import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.AttrResponseVo;
import com.atguigu.gulimall.search.vo.BrandVo;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Resource
    private RestHighLevelClient highLevelClient;

    @Autowired
    private ProductFeignService productFeignService;

    /**
     * 检索所有的参数
     * 返回检索的结果
     *
     * @param param
     * @return SearchResult包含页面需要的所有信息
     */
    @Override
    public SearchResult search(SearchParam param) {
        /**
         * 动态构建出查询需要的DSL语句
         */
        SearchResult result = null;
        //1.创建检索请求
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            //2.执行检索请求
            SearchResponse response = highLevelClient.search(searchRequest, ElasticSerachConfig.COMMON_OPTIONS);
            result = buildSearchResult(response, param);
            //3.分析响应数据，封装成我们需要的格式
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 构建结果数据
     *
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {
        SearchResult result = new SearchResult();
        //1.返回所有查询到的商品
        SearchHits hits = response.getHits();
        List<SkuEsModel> esModels = new ArrayList<>();
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                //封装高亮的属性
                if (!StringUtils.isEmpty(param.getKeyword())) {
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String highlight = skuTitle.getFragments()[0].string();
                    skuEsModel.setSkuTitle(highlight);
                }
                esModels.add(skuEsModel);
            }
        }
        result.setProducts(esModels);

        //2.当前商品涉及到的所有分类信息
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            //得到分类id
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));
            //得到分类名
            ParsedStringTerms catalogName_agg = bucket.getAggregations().get("catalogName_agg");
            String catalogName = catalogName_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            catalogVos.add(catalogVo);
        }
        result.setCatalogVos(catalogVos);

        //3.当前商品涉及到的所有属性信息
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attrId_agg = attr_agg.getAggregations().get("attrId_agg");
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        for (Terms.Bucket bucket : attrId_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //1）、得到属性的id
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);
            //2）、得到属性的名字
            ParsedStringTerms attrName_agg = bucket.getAggregations().get("attrName_agg");
            String attrName = attrName_agg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            //3）、得到属性的值,值有多个，需要遍历
            ParsedStringTerms attrValue_agg = bucket.getAggregations().get("attrValue_agg");
            List<String> attrValues = new ArrayList<>();
            for (Terms.Bucket attrValue_aggBucket : attrValue_agg.getBuckets()) {
                String attrValue = attrValue_aggBucket.getKeyAsString();
                attrValues.add(attrValue);
            }
            attrVo.setAttrValue(attrValues);

            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);

        //4.当前商品涉及到的所有品牌信息
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //得到品牌id
            Long keyAsString = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(keyAsString);
            //得到品牌名，子聚合下面
            ParsedStringTerms brandName_agg = bucket.getAggregations().get("brandName_agg");
            String brandName = brandName_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            //得到品牌图片
            ParsedStringTerms brandImg_agg = bucket.getAggregations().get("brandImg_agg");
            String brandImg = brandImg_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        //5.分页信息
        result.setPageNum(param.getPageNum());
        //总记录数
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        //总页码 总记录数/每页记录数
        long totalPages = total % EsConstant.PRODUCT_PAGE_SIZE == 0 ? total / EsConstant.PRODUCT_PAGE_SIZE : (total / EsConstant.PRODUCT_PAGE_SIZE + 1);
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);
        result.setTotalPages(totalPages);

        //6.追加面包屑导航功能
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            List<SearchResult.navVo> navVoList = param.getAttrs().stream().map(attr -> {
                SearchResult.navVo navVo = new SearchResult.navVo();
                //1.分析每个attrs传过来的属性值  attrs=2_5寸:6寸
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);

                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                result.getAttrIds().add(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    navVo.setNavName(data.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }

                //2.取消了这个面包屑以后，我们要跳转到哪个地方，将请求地址的当前url置空
                String replace = replaceQueryString(param, attr,"attrs");
                navVo.setLink("http://search.gulimall.com/list.html?" + replace);

                return navVo;
            }).collect(Collectors.toList());

            result.setNavs(navVoList);
        }

        //7.品牌，分类
        if (param.getBrandId() != null && param.getBrandId().size() > 0){
            List<SearchResult.navVo> navs = result.getNavs();
            SearchResult.navVo navVo = new SearchResult.navVo();
            navVo.setNavName("品牌");
            //TODO 远程调用查询品牌信息
            R r = productFeignService.brandsInfo(param.getBrandId());
            if (r.getCode() == 0){
                List<BrandVo> brands = r.getData("brand", new TypeReference<List<BrandVo>>() {
                });
                StringBuffer buffer = new StringBuffer();
                String replace = "";
                for(BrandVo brand : brands){
                    String brandName = brand.getBrandName();
                    replace = replaceQueryString(param, brand.getBrandId() + "","brandId");
                    buffer.append(brandName + ";");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.gulimall.com/list.html?" + replace);
            }
            navs.add(navVo);
        }

        //TODO 分类，不需要导航取消需求

        return result;
    }

    private String replaceQueryString(SearchParam param, String value,String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "utf-8");
            encode.replace("+","%20");  //浏览器对空格的编码和java不一样
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String replace = param.get_queryString().replace("&"+key+"=" + encode, "");
        return replace;
    }

    /**
     * 准备检索请求
     * 模糊匹配，过滤(按照属性，分类，品牌，价格区间，库存)，排序，分页，高亮，聚合分析
     *
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();    //指定检索条件的
        //1.======================模糊匹配，过滤(按照属性，分类，品牌，价格区间，库存)======================
        BoolQueryBuilder boolQuery = new BoolQueryBuilder(); //构建布尔query
        //1.1、must模糊匹配
        if (!StringUtils.isEmpty(param.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        //1.2、bool - filter
        if (param.getCatalog3Id() != null) {    //按照三级分类id查询
            boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        if (param.getBrandId() != null && param.getBrandId().size() > 0) {   //按照品牌id查询
            boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {       //按照所有指定的属性进行查询
            List<String> attrs = param.getAttrs();
            for (String attr : attrs) {
                //attrs=1_5寸:8寸&attrs=2_8G:16G
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                String attrId = s[0];   //检索的属性id
                String[] attrValues = s[1].split(":");  //这个属性检索用的值
                boolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                boolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                //每一个属性必须都得生成一个nestedQuery查询
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", boolQueryBuilder, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }

        if (param.getHasStock() != null) {
            boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));   //按照是否有库存查询
        }
        if (!StringUtils.isEmpty(param.getSkuPrice())) {         //按照价格区间进行检索
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String skuPrice = param.getSkuPrice();
            String[] s = skuPrice.split("_");
            if (s.length == 2) {
                //区间
                rangeQuery.gte(s[0]).lte(s[1]);
            } else if (s.length == 1) {
                if (param.getSkuPrice().startsWith("_")) { //_500 小于500
                    rangeQuery.lte(s[0]);
                }
                if (param.getSkuPrice().endsWith("_")) {   //500_ 大于500
                    rangeQuery.gte(s[0]);
                }
            }
            boolQuery.filter(rangeQuery);
        }

        //把以前的条件都拿来封装(查询条件)
        searchSourceBuilder.query(boolQuery);

        //2.========================排序，分页，高亮============================
        //2.1排序
        if (!StringUtils.isEmpty(param.getSort())) {
            /**
             * 排序条件
             * sort=saleCount_asc/desc
             * sort=skuPrice_asc/desc
             * sort=hotScore_asc/desc
             */
            String sort = param.getSort();
            String[] s = sort.split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(s[0], order);
        }

        //2.2分页 pageSize:5
        //pageNum:1 from:0  size:5  [0,1,2,3,4]
        //pageNum:2 from:5  size:5
        //from = (pageNum-1) * pageSize 分页
        searchSourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGE_SIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);

        //2.3高亮
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        //3.===============聚合分析=================
        //3.1品牌聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg");
        brandAgg.field("brandId").size(50);
        //品牌聚合的子聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brandName_agg").field("brandName").size(1));    //品牌名称
        brandAgg.subAggregation(AggregationBuilders.terms("brandImg_agg").field("brandImg").size(1));      //品牌图片
        searchSourceBuilder.aggregation(brandAgg);

        //3.2分类聚合 catalog_agg
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg");
        catalogAgg.field("catalogId").size(2);
        //分类聚合的子聚合
        catalogAgg.subAggregation(AggregationBuilders.terms("catalogName_agg").field("catalogName").size(1));
        searchSourceBuilder.aggregation(catalogAgg);

        //3.3属性聚合
        NestedAggregationBuilder attrAggNested = AggregationBuilders.nested("attr_agg", "attrs");
        //聚合出当前所有的attrId
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrId_agg").field("attrs.attrId").size(20);
        //attrId的下面还有两个子聚合
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrName_agg").field("attrs.attrName").size(1));    //当前attrId对应的属性名
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValue_agg").field("attrs.attrValue").size(50)); //当前attrId对应的属性值
        attrAggNested.subAggregation(attrIdAgg);
        searchSourceBuilder.aggregation(attrAggNested);

        //打印检索的json格式数据
        System.out.println(searchSourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
        return searchRequest;
    }
}
