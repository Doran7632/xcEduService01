package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

// 继承的MongoRespository中，第二个泛型为第一个泛型的主键的类型
public interface CmsTemplateRespository extends MongoRepository<CmsTemplate,String> {

}
