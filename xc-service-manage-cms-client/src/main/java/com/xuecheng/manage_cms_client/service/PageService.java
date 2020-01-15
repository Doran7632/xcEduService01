package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRespository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRespository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Service
public class PageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    @Autowired
    CmsPageRespository cmsPageRespository;

    @Autowired
    CmsSiteRespository cmsSiteRespository;


    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    GridFsTemplate gridFsTemplate;


    public void savePageToServerPath(String pageId){
        //根据pageId查询cmsPage，
        CmsPage cmsPageById = getCmsPageById(pageId);

        //得到html的文件id，从cmsPage中获取htmlFileId的内容
        String htmlFileId = cmsPageById.getHtmlFileId();

        //从gridFS中查询html文件
        InputStream inputStream = getFileByFileId(htmlFileId);
        if(inputStream == null){
            LOGGER.error("getFileByFileId InputStream is null,htmlFileId:{}",htmlFileId);
        }

        //查询站点物理路径
        String siteId = cmsPageById.getSiteId();
        CmsSite cmsSiteById = getCmsSiteById(siteId);
        String sitePhysicalPath = cmsSiteById.getSitePhysicalPath();

        //将html文件保存到服务器中的物理路径上
        //物理路径为：站点物理路径 + 页面物理路径 + 页面名称
        String pagePath = sitePhysicalPath + cmsPageById.getPagePhysicalPath() + cmsPageById.getPageName();

        //将html文件放入服务器物理路径上
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //根据文件id查询文件内容，并返回
    public InputStream getFileByFileId(String fileId)  {
        //根据id查询文件对象
        GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开一个下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFsFile.getObjectId());
        GridFsResource gridFsResource = new GridFsResource(gridFsFile,gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //根据id查询cmsSite
    public CmsSite getCmsSiteById(String pageId){
        Optional<CmsSite> cmsSiteOptional  = cmsSiteRespository.findById(pageId);
        if(cmsSiteOptional.isPresent()){
            return cmsSiteOptional.get();
        }
        return null;
    }

    //根据id查询cmsPage
    public CmsPage getCmsPageById(String pageId){
        Optional<CmsPage> cmsPageOptional  = cmsPageRespository.findById(pageId);
        if(cmsPageOptional.isPresent()){
            return cmsPageOptional.get();
        }
        return null;
    }



}
