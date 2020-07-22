package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 站点dao
 */
public interface CmsSiteRespository extends MongoRepository<CmsSite,String> {
}
