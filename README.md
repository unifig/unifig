# unifig

#### 介绍
```
unifig,是以基于 SpringCloud 的一个分布式 微服务 平台。 具有 服务发现注册、配置中心、负载均衡、断路器、数据监控 。
当前开发完成b2c商场的主要业务，以及团购逻辑。是二次开发的最佳之选。
``` 

#### 软件架构
```
待补
```
#### 工程结构
``` 
unifig
├── unifig-config -- Spring Cloud 配置server
├── unifig-rc -- Spring Cloud 注册中心
├── unifig-zipkin -- Spring Cloud 链路监控
├── unifig-zuul -- Spring Cloud 网关
├── unifig-modules -- 业务模块
├    ├── unifig-basics -- 公共组件 图片服务
├    ├── unifig-bi -- 统计模块 
├    ├── unifig-common -- 公共包
├    ├── unifig-im -- im netty demo 
├    ├── unifig-mall -- 商城模块 
├    ├── unifig-organ -- 用户模块 
└──  └── unifig-logistics -- 物流
```
#### 配置文件
[配置文件-代码地址](https://gitee.com/unifig/unifig-config-files.git)

#### 后台管理
[后台管理-代码地址](https://gitee.com/unifig/unifig-admin)

| ![后台商品](https://images.gitee.com/uploads/images/2019/1118/164121_99bb49f1_1070396.png "WechatIMG42793.png")|   ![后台订单](https://images.gitee.com/uploads/images/2019/1118/164155_57da9faf_1070396.png "WechatIMG42795.png")|
| --- | --- |
|![后台广告](https://images.gitee.com/uploads/images/2019/1118/164218_beb8bcca_1070396.png "WechatIMG42800.png")|![后台权限](https://images.gitee.com/uploads/images/2019/1118/164237_129ccc1c_1070396.png "WechatIMG42801.png")|


#### 小程序

|   ![小程序-首页商品](https://images.gitee.com/uploads/images/2019/1118/164837_64f56fdd_1070396.png "WechatIMG20346.png")  |   ![小程序-首页团购](https://images.gitee.com/uploads/images/2019/1118/164302_fddbbf89_1070396.png "WechatIMG20343.png")  |
| --- | --- |
|  ![小程序-我的](https://images.gitee.com/uploads/images/2019/1118/164750_531c0ab0_1070396.png "WechatIMG874.png")   |   ![小程序-购物车](https://images.gitee.com/uploads/images/2019/1118/164819_dc3a5f79_1070396.png "WechatIMG20345.png")  |



#### 安装教程
```
详细 - /doc/部署 
安装教程:

1.克隆代码到本地: 

​	后端服务:git clone https://gitee.com/unifig/unifig.git

​	配置文件服务:git clone https://gitee.com/unifig/unifig-config-files.git

2.修改/unifig-config-files/dev  中对应配置文件

3.上传 unifig-config-files 至 [码云](https://gitee.com/) 或 [github ](https://github.com/)

4.idea 导入 unifig项目

5.修改unifig-config 模块配置文件

![修改配置服务 git地址.png](https://upload.cc/i1/2019/11/13/I215zJ.png)


spring.cloud.config.server.git.uri 项目git地址
spring.cloud.config.server.git.username=账号
spring.cloud.config.server.git.password=密码
注意:此账号必须有unifig-config-files 项目权限

6.修改unifig-organ模块小程序配置信息(同理修改unifig-mall)

每个子项目都可以单独运行，都是打包成jar包后，通过使用内置jetty容器执行，有2种方式运行。

1. 在IDEA里面直接运行Application.java的main函数。
2. 另一种方式是执行`mvn clean package`命令后传到linux服务器上面，通过命令`java -Xms64m -Xmx1024m -jar xxx.jar`方式运行

服务启动顺序

1.unifig-rc

2.unifig-config

3.unifig-organ

4.unifig-mall

5.unifig-zuul

```
#### 使用说明
```

1,后台管理的前端项目会陆续开源 （ 已开源 )
1,配置文件项目会陆续开源 （ 已开源 ）
2,微信小程序项目会陆续开源
3,文档会陆续完善
4,后续开发计划(优惠卷小程序支持,社区,活动小程序支持)
5,做ai工具，微信小程序 类似 - 腾讯AI体验中心 

```

#### 组织成员
```


```

