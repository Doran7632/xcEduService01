package com.xuecheng.manage_cms_client.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 监听消息队列，接受页面发送的消息
 */
@Component
public class ConsumerPostPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerPostPage.class);


    @Autowired
    PageService pageService;

    @RabbitListener(queues = "${xuecheng.mq.queue}")
    public void postPage(String msg){
        Map map = JSON.parseObject(msg, Map.class);
        //得到页面中的pageid
        String pageId = (String) map.get("pageId");
        CmsPage cmsPageById = pageService.getCmsPageById(pageId);
        if(cmsPageById == null){
            LOGGER.error("get CmsPage is null,pageId:{}",pageId);
        }
        //调用方法进行保存到服务器上
        pageService.savePageToServerPath(pageId);
    }
}
