/*******************************************************************************
 * Package: com.xuecheng.manage_course.dao
 * Type:    CourseMarketRepository
 * Date:    2020/1/20 9:39
 *
 * Copyright (c) 2020 HUANENG GUICHENG TRUST CORP.,LTD All Rights Reserved.
 *
 * You may not use this file except in compliance with the License.
 *******************************************************************************/
package com.xuecheng.manage_course.dao;


import com.xuecheng.framework.domain.course.CourseMarket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 课程营销的dao
 */
public interface CourseMarketRepository extends JpaRepository<CourseMarket,String> {
}
