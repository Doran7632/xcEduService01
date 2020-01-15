package com.xuecheng.manage_cms.dao;

import com.xuecheng.manage_cms.service.PageService;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PageServiceTest {


    @Autowired
    PageService pageService;

    @Test
    public void testRestTempalte() throws IOException, TemplateException {
        String pageHtml = pageService.getPageHtml("5d76111a0f0ad243b0581a44");
        System.out.println(pageHtml);
    }
}
