package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.Response.QueryResponseResult;
import com.xuecheng.framework.model.Response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value="cms页面管理接口",description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {

    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")})
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);



    //新增页面
    @ApiOperation("新增页面")
    public CmsPageResult add(CmsPage cmsPage);


    //根据id查询页面
    @ApiOperation("查询页面")
    public CmsPage findById(String pageId);

    //修改页面
    @ApiOperation("修改页面")
    public CmsPageResult edit(String pageId,CmsPage cmsPage);

    //修改页面
    @ApiOperation("修改页面")
    public ResponseResult edit(String pageId);

    //页面发布接口
    @ApiOperation("页面发布")
    public ResponseResult post(String pageId);

    //保存页面（包含新增页面和更新页面，该接口主要是服务课程预览中新增部分）
    @ApiOperation("新增页面")
    public CmsPageResult save(CmsPage cmsPage);

    //一键发布接口，主要是课程发布时使用
    @ApiOperation("新增页面")
    public CmsPostPageResult postPageQuick(CmsPage cmsPage);

}
