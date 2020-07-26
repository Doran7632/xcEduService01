package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "XC-SERVICE-MANAGE-CMS") //指定远程调用的服务名
public interface CmsPageClient {

    //添加页面服务，用于课程预览
    @PostMapping("/cms/page/save")
    public CmsPageResult saveCmsPage(@RequestBody CmsPage cmsPage);

    //一键发布服务，用于课程发布
    @PostMapping("/cms/page/postPageQuick")
    public CmsPostPageResult postPageQucik(@RequestBody CmsPage cmsPage);

}
