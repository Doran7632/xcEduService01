package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.Response.CommonCode;
import com.xuecheng.framework.model.Response.QueryResponseResult;
import com.xuecheng.framework.model.Response.QueryResult;
import com.xuecheng.framework.model.Response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRespository;
import com.xuecheng.manage_cms.dao.CmsPageRespository;
import com.xuecheng.manage_cms.dao.CmsSiteRespository;
import com.xuecheng.manage_cms.dao.CmsTemplateRespository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {


    @Autowired
    CmsPageRespository cmsPageRespository;

    @Autowired
    CmsSiteRespository cmsSiteRespository;

    @Autowired
    CmsConfigRespository cmsConfigRespository;

    @Autowired
    CmsTemplateRespository cmsTemplateRespository;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {

        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }

        //自定义条件查询
        //定义条件匹配器,模糊查询 页面别名
        ExampleMatcher exampleMather = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        //条件值对象
        CmsPage cmsPage = new CmsPage();
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())) {
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //定义example对象，用于条件查询使用
        Example<CmsPage> cmsPageExample = Example.of(cmsPage, exampleMather);

        //此处是为了符合用户习惯，分页开始是从1开始。而spring data分页开始是从0开始
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRespository.findAll(cmsPageExample, pageable);
        QueryResult<CmsPage> queryResult = new QueryResult();
        queryResult.setList(all.getContent()); //数据
        queryResult.setTotal(all.getTotalElements()); //数据总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }


    //新增页面
    public CmsPageResult add(CmsPage cmsPage) {
        if(cmsPage == null){
            //抛出异常，数据非法
        }
        //校验唯一索引的唯一性
        CmsPage cmsPage1 = cmsPageRespository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
                    /*if (cmsPage1 == null) {
                        cmsPage.setPageId(null);//为了防止主键传入。主键是由数据库自增来的
                        cmsPageRespository.save(cmsPage);
                        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
                    }
                    return new CmsPageResult(CommonCode.FAIL, null);*/
        //将异常首先抛出，令代码变得规范
        if(cmsPage1 !=null){
            //抛出异常，表示 页面已经存在
            //throw new CustomException(CommonCode.FAIL);
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);//封装一下
        }
        cmsPage.setPageId(null);//为了防止主键传入。主键是由数据库自增来的
        cmsPageRespository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
    }


    //根据id查询
    public CmsPage findById(String pageId) {
        Optional<CmsPage> optional = cmsPageRespository.findById(pageId);
        if (optional.isPresent()) {
            CmsPage cmsPage = optional.get();
            return cmsPage;
        }
        return null;
    }


    //修改
    public CmsPageResult update(String pageId, CmsPage cmsPage) {
        CmsPage cmsPage1 = this.findById(pageId);
        if (cmsPage1 != null) {
            //修改
            cmsPage1.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            cmsPage1.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            cmsPage1.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            cmsPage1.setPageName(cmsPage.getPageName());
            //更新访问路径
            cmsPage1.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            cmsPage1.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //修改dataUrl
            cmsPage1.setDataUrl(cmsPage.getDataUrl());
            cmsPageRespository.save(cmsPage1);
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage1);
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    //删除
    public ResponseResult del(String pageId) {
        Optional<CmsPage> optional = cmsPageRespository.findById(pageId);
        if(optional.isPresent()){
            cmsPageRespository.deleteById(pageId);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


    //根据id查询cms配置表
    public CmsConfig getConfigById(String id){
        Optional<CmsConfig> cmsConfig = cmsConfigRespository.findById(id);
        if(cmsConfig.isPresent()){
            return cmsConfig.get();
        }
        return null;
    }


    /**
     * 页面静态化方法
     *
     * 静态化程序获取页面的dataUrl
     * 远程请求dataUrl获取数据模型
     * 获取页面的模板信息
     * 执行页面静态化
     *
     * @param pageId
     * @return
     */
    public String getPageHtml(String pageId)  {

        //获取数据模型
        Map modelByPageId = getModelByPageId(pageId);

        //获取模板信息
        String template = null;
        try {
            template = getTemplateByPageId(pageId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String content = null;
        try {
            content = generateHtml(template, modelByPageId);
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        //将填充好的页面html字符串返回
        return content;
    }

    //执行静态化
    private String generateHtml(String template, Map model) throws  TemplateException {
        //使用字符串静态化的方式来生成页面
        Configuration configuration = new Configuration(Configuration.getVersion());

        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("templateS",template);
        //在配置中设置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板的内容
        Template tempalteS = null;
        try {
            tempalteS = configuration.getTemplate("templateS","utf-8");
            String content  = FreeMarkerTemplateUtils.processTemplateIntoString(tempalteS, model);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTemplateByPageId(String pageId) throws IOException {
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage == null ){
            //页面不存在异常
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        String templateId = cmsPage.getTemplateId();
        if(StringUtils.isEmpty(templateId)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        Optional<CmsTemplate> OptionalTemplate = cmsTemplateRespository.findById(templateId);
        if(OptionalTemplate.isPresent()){
            //返回模板信息
            CmsTemplate cmsTemplate = OptionalTemplate.get();
            //根据id查询文件对象
            GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(cmsTemplate.getTemplateFileId())));
            //打开一个下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFsFile.getObjectId());
            //创建Resource对象，获取流
            GridFsResource gridFsResource = new GridFsResource(gridFsFile,gridFSDownloadStream);
            String content  = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
            return content;
        }
        return null;
    }



    //获取页面的DataUrl请求来获取数据模型,在CmsPage中有DataUrl字段来获取
    private Map getModelByPageId(String pageId){
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage == null ){
            //页面不存在异常
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        //根据DataUrl调用接口查询页面需要填充的数据
        String dataUrl = cmsPage.getDataUrl();
        if(StringUtils.isEmpty(dataUrl)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }


    //页面发布
    public ResponseResult post(String pageId) {
        //执行页面静态化，
        String pageHtml = this.getPageHtml(pageId);
        // 将页面保存至gridFS中
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        //发送消息至mq中
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //向mq发送消息
    private void sendPostPage(String pageId){
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.INVAILD_PARAM);
        }

        //创建消息对象
        Map<String,String> map = new HashMap<>();
        map.put("pageId",pageId);
        //转换为JSON串
        String pageString = JSON.toJSONString(map);
        //siteId作为routingKey
        String siteId = cmsPage.getSiteId();
        //发送给mq
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,pageString);
    }

    //保存html到GridFS中
    private CmsPage saveHtml(String pageId,String htmlContent){
        CmsPage cmsPage = this.findById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.INVAILD_PARAM);
        }
        //将html文件内容转换为inputStream
        InputStream inputStream = null;
        ObjectId store = null;
        try {
            inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            //将html文件内容保存到gridFS中
            store = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将html文件id更新到cmsPage中
        cmsPage.setHtmlFileId(store.toHexString());
        cmsPageRespository.save(cmsPage);
        return cmsPage;
    }


    /**
     * 保存页面，有页面则更新，没有页面则新增
     * @param cmsPage
     * @return
     */
    public CmsPageResult save(CmsPage cmsPage) {
        //判断页面是否存在，使用唯一索引来进行判断，在mongodDB中cms_page表中添加pageName ，pageWebPath，siteId这三个字段的唯一索引
        CmsPage one = cmsPageRespository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(one!=null){
            //进行更新
            return this.update(one.getPageId(),cmsPage);
        }
        return this.add(cmsPage);
    }

    /**
     * 一键发布接口实现
     * @param cmsPage
     * @return
     */
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //将页面信息存储到cms_page中
        CmsPageResult save = this.save(cmsPage);
        if(!save.isSuccess()){
            //抛出异常
            ExceptionCast.cast(CommonCode.FAIL);
        }
        CmsPage cmsPage1 = save.getCmsPage();
        String pageId = cmsPage1.getPageId();
        //执行页面发布(先静态化，保存GridFS,发送mq )
        ResponseResult post = this.post(pageId);
        if(!post.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }

        //拼接url = 站点域名 + 站点webpath + 页面webpath + 页面name
        String siteId = cmsPage1.getSiteId();
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        String pageUrl = cmsSite.getSiteDomain() + cmsSite.getSiteWebPath() + cmsPage1.getPageWebPath() + cmsPage1.getPageName();
        return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
    }

    //根据站点id查询站点
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite> byId = cmsSiteRespository.findById(siteId);
        if(byId.isPresent()){
            return byId.get();
        }
        return null;
    }
}
