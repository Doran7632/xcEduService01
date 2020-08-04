package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePub;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Administrator.
 * 为了添加索引库
 */
public interface CoursePubRepository extends JpaRepository<CoursePub,String> {


}
