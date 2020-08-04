package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.Response.CommonCode;
import com.xuecheng.framework.model.Response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private static Logger log = LoggerFactory.getLogger(CourseService.class);
    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    CoursePicRepository coursePicRepository;

    @Autowired
    CourseMarketRepository courseMarketRepository;

    @Autowired
    CoursePubRepository coursePubRepository;

    @Autowired
    CmsPageClient cmsPageClient;

    public TeachplanNode findTeachplan(String courseId){
        return teachplanMapper.selectList(courseId);
    }

    //添加课程计划
    public ResponseResult addTeachplan(Teachplan teachplan) {
        //要处理parentId
        if(teachplan == null ||
                StringUtils.isEmpty(teachplan.getCourseid()) ||
                StringUtils.isEmpty(teachplan.getPname())){
            ExceptionCast.cast(CommonCode.INVAILD_PARAM);
        }
        String courseid = teachplan.getCourseid();
        String parentid = teachplan.getParentid();
        Teachplan parentTeachplan = null;
        if(StringUtils.isEmpty(parentid)){
            //取出根节点
             parentTeachplan = getParentTeachplan(courseid);
        }
        //添加新节点
        Teachplan teachplanNew = new Teachplan();
        BeanUtils.copyProperties(teachplan,teachplanNew);
        teachplanNew.setParentid(parentTeachplan.getId());
        teachplanNew.setCourseid(courseid);
        if("1".equals(parentTeachplan.getGrade())){
            teachplanNew.setGrade("2");
        }else{
            teachplanNew.setGrade("3");
        }
        teachplanRepository.save(teachplanNew);
        return new ResponseResult(CommonCode.SUCCESS);
    }



    private Teachplan getParentTeachplan(String courseId){
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        if(! courseBaseOptional.isPresent()){
            return null;
        }
        CourseBase courseBase = courseBaseOptional.get();
        List<Teachplan> teachplanList = teachplanRepository.findTeachplanByCourseidAndParentid(courseId, "0");
        if(teachplanList == null || teachplanList.size() <= 0){
            //设置改计划为根节点并保存返回
            Teachplan teachplanRoot = new Teachplan();
            teachplanRoot.setCourseid(courseId);
            teachplanRoot.setGrade("1");
            teachplanRoot.setParentid("0");
            teachplanRoot.setPname(courseBase.getName());
            teachplanRoot.setStatus("0");
            teachplanRepository.save(teachplanRoot);
            return teachplanRoot;
        }
        return teachplanList.get(0);
    }

    //添加课程图片
    public ResponseResult addCoursePic(String courseId, String pic) {
        //先查询course
        CoursePic coursePic = null;
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if(optional.isPresent()){
            coursePic = optional.get();
        }
        if(coursePic == null){
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //查询课程图片
    public CoursePic findCoursePic(String courseId) {
        Optional<CoursePic> byId = coursePicRepository.findById(courseId);
        if(byId.isPresent()){
            CoursePic coursePic = byId.get();
            return coursePic;
        }
        return null;
    }


    //删除图片
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        long efftiveRows = coursePicRepository.deleteByCourseid(courseId);
        if(efftiveRows > 0){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 查询课程视图
     * @param courseId
     * @return
     */
    public CourseView getCourseView(String courseId) {
        CourseView courseView = new CourseView();

        //查询课程基础信息
        Optional<CourseBase> optionalCourseBase = courseBaseRepository.findById(courseId);
        if(optionalCourseBase.isPresent()){
            CourseBase courseBase = optionalCourseBase.get();
            courseView.setCourseBase(courseBase);
        }
        //查询课程营销信息
        Optional<CourseMarket> optionalCourseMarket = courseMarketRepository.findById(courseId);
        if(optionalCourseMarket.isPresent()){
            CourseMarket courseMarket = optionalCourseMarket.get();
            courseView.setCourseMarket(courseMarket);
        }

        //查询课程图片信息
        CoursePic coursePic = this.findCoursePic(courseId);
        courseView.setCoursePic(coursePic);

        //查询课程计划信息
        TeachplanNode teachplan = this.findTeachplan(courseId);
        courseView.setTeachplanNode(teachplan);
        return courseView;
    }

    /**
     * 课程预览service
     * @param id
     * @return
     */
    @Transactional
    public CoursePublishResult preview(String id) {
        //1------------------  请求cms服务，调用euekra服务添加页面
        CourseBase courseBaseById = this.findCourseBaseById(id);
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setDataUrl(publish_dataUrlPre + id);
        cmsPage.setPageName(id + ".html");
        cmsPage.setPageAliase(courseBaseById.getName());
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setTemplateId(publish_templateId);
        //远程调用
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if(cmsPage == null){
            //抛出异常
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();

        //2------------------  拼装页面预览的url
        String url = previewUrl + pageId;

        //3------------------  返回CoursePublishResult对象，（包含了页面预览的url）
        return new CoursePublishResult(CommonCode.SUCCESS,url);
    }

    /**
     * 查找课程基本信息
     * @param courseId
     * @return
     */
    private CourseBase findCourseBaseById(String courseId){
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseId);
        if(baseOptional.isPresent()){
            CourseBase courseBase = baseOptional.get();
            return courseBase;
        }
        ExceptionCast.cast(CourseCode.COURSE_NOT_FOUND);
        return null;
    }

    /**
     * 课程发布
     * @param id
     * @return
     */
    public CoursePublishResult publish(String id) {
        //调用cms一键发布接口将课程详情页面发布到服务器
        CourseBase courseBaseById = this.findCourseBaseById(id);
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setDataUrl(publish_dataUrlPre + id);
        cmsPage.setPageName(id + ".html");
        cmsPage.setPageAliase(courseBaseById.getName());
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setTemplateId(publish_templateId);
        CmsPostPageResult pageResult = cmsPageClient.postPageQucik(cmsPage);
        if (!pageResult.isSuccess()){
            //抛出异常
            return new CoursePublishResult(CommonCode.FAIL,null);
        }

        //保存课程的发布状态为 “已发布”
        CourseBase courseBase = saveCoursePubState(id);
        if(courseBase == null) {
            return new CoursePublishResult(CommonCode.FAIL,null);
        }

        /**
         * 保存课程索引信息，这里是课程已经不准备更改了，所以可以想es索引库进行索引，
         * 保存至索引库分为两步
         * 1：创建coursePub对象
         * 2：将coursePub对象保存到数据库
         *
         */
        CoursePub coursePub = createCoursePub(id);

        saveCoursePub(id, coursePub);


        //缓存课程的信息

        //得到url
        String pageUrl = pageResult.getPageUrl();
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }

    //更改课程状态为 “已发布” 为 202002
    private CourseBase saveCoursePubState(String courseId){
        CourseBase courseBaseById = this.findCourseBaseById(courseId);
        courseBaseById.setStatus("202002");
        courseBaseRepository.save(courseBaseById);
        return courseBaseById;
    }

    //创建coursePub对象
    private CoursePub createCoursePub(String id){
        CoursePub coursePub = new CoursePub();
        //根据课程id查询coursebase
        Optional<CourseBase> courseBaseById = courseBaseRepository.findById(id);
        if(courseBaseById.isPresent()){
            CourseBase courseBase = courseBaseById.get();
            BeanUtils.copyProperties(courseBase,coursePub);
        }
        //查询课程图片
        Optional<CoursePic> coursePicById = coursePicRepository.findById(id);
        if(coursePicById.isPresent()){
            CoursePic coursePic = coursePicById.get();
            BeanUtils.copyProperties(coursePic,coursePub);
        }
        //查询课程营销信息
        Optional<CourseMarket> courseMarketById = courseMarketRepository.findById(id);
        if(courseMarketById.isPresent()){
            CourseMarket courseMarket = courseMarketById.get();
            BeanUtils.copyProperties(courseMarket,coursePub);
        }

        //课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        String jsonString = JSON.toJSONString(teachplanNode);
        //将课程信息json串保存到course_pub中
        coursePub.setTeachplan(jsonString);
        return coursePub;
    }

    //保存到数据库
    private CoursePub saveCoursePub(String id,CoursePub coursePub){
        CoursePub coursePubNew = null;
        //根据id查询coursepub
        Optional<CoursePub> coursePubById = coursePubRepository.findById(id);
        if(coursePubById.isPresent()){
            coursePubNew = coursePubById.get();
        }else {
            coursePubNew = new CoursePub();
        }

        BeanUtils.copyProperties(coursePub,coursePubNew);
        coursePubNew.setId(id);
        //时间戳(logstash使用)
        coursePubNew.setTimestamp(new Date());
        //发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        log.debug("++++++" + date);
        coursePubNew.setPubTime(date);
        CoursePub save = coursePubRepository.save(coursePubNew);
        return save;

    }
}
