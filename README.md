xcEduService01
学习的学成在线后台代码
初次导入目前开发到了第9天 课程预览Eureka Feign部分

前9天包含的内容除了增删改查，最难的部分在于页面发布和页面预览

1：页面预览：主要的逻辑是 
    
    1）先根据pageId查询CmsPage，从中获取dataUrl，（即获取填充数据）
    2）在根据pageId查询CmsPage，从中获取templateId，再由templateId查询CmsTemplate表，再根据CmsTemplate表中的TemplateFileId字段查询GridFSFile
       将二进制的GridFSFile通过IOUtils转换为字符串的html页面
    3）使用StringTemplateLoder将填充数据填充到模板中（使用Freemarker技术）。返回字符串的html页面，
    4）通过HttpServletResponse中的outputStream输出到页面上

2：页面发布：主要的逻辑是  

    1）先执行页面静态化，获取已经填充后的html页面字符串
    2）将页面保存到GridFS中，先将html页面字符串存入GridFS中，保存成二进制文件，返回FileId，再更新传入的PageId对应的CmsPage的HtmlFileId字段（将该FileId存入）
    3）发送消息到mq，发送一个pageId，可以登录Rabbitmq控制台查看队列中的数据
    4）再启动cms_client服务监听该队列，获取pageId
    5）再根据PageId查询，获取HtmlFileId，pagePhysicalPath字段，HtmlFileId字段用于查询GridFS中的二进制文件
       获取CmsPage中的pagePhysicalPath字段，该字段表示的是该页面在服务器上的物理路径，
    6）再使用IOUtils的copy方法，将二进制文件替换服务器上的文件，完成页面发布的操作
    
    