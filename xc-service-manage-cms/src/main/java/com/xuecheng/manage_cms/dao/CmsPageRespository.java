package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

// 继承的MongoRespository中，第二个泛型为第一个泛型的主键的类型
public interface CmsPageRespository extends MongoRepository<CmsPage,String> {

    //在studio3T中添加了唯一索引（页面名称、站点id、页面webpath组合）。 根据唯一索引查询
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName,String sitdId,String pageWebPath);
}
