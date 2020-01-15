package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.Response.CommonCode;
import com.xuecheng.framework.model.Response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.CoursePicRepository;
import com.xuecheng.manage_course.dao.TeachplanMapper;
import com.xuecheng.manage_course.dao.TeachplanRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    CoursePicRepository coursePicRepository;


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
}
