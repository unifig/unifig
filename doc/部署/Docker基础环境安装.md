# 1.Mysql安装

### 下载镜像文件

```
docker pull mysql:5.7
```

### 创建实例并启动

```
docker run -p 3306:3306 --name mysql \

-v /mydata/mysql/log:/var/log/mysql \

-v /mydata/mysql/data:/var/lib/mysql \

-v /mydata/mysql/conf:/etc/mysql \

-e MYSQL_ROOT_PASSWORD=123456  \

-d mysql:5.7
```

> 参数说明
- -p 3306:3306：将容器的3306端口映射到主机的3306端口
- -v /mydata/mysql/conf:/etc/mysql：将配置文件夹挂在到主机
- -v /mydata/mysql/log:/var/log/mysql：将日志文件夹挂载到主机
- -v /mydata/mysql/data:/var/lib/mysql/：将配置文件夹挂载到主机
- -e MYSQL_ROOT_PASSWORD=123456：初始化root用户的密码

------

# 2.Redis安装

下载镜像文件

```
docker pull redis:3.2
```

创建实例并启动

``````
docker run  --name myredis -p 16379:6379 -d redis:3.2  --appendonly yes
``````

------

# 3.Mongo安装

下载镜像文件

```
docker pull mongo:3.2
创建实例并运行
docker run -p 27017:27017 -v /opt/mongo/db:/data/db -d mongo:3.2
```

------

# 4.Elasticsearch安装

### 下载镜像文件

```
docker pull elasticsearch:2.4
```

### 创建实例并运行

```
docker run -p 9200:9200 -p 9300:9300 --name elasticsearch \

-v /mydata/elasticsearch/plugins:/usr/share/elasticsearch/plugins \

-v /mydata/elasticsearch/data:/usr/share/elasticsearch/data \

-d elasticsearch:2.4
```

### 测试
访问会返回版本信息：http://192.168.1.66:9200/
### 安装目录位置

```
/usr/share/elasticsearch
```

### 安装head插件
1. 进入docker内部bash:docker exec -it elasticsearch /bin/bash
2. 安装插件：plugin install mobz/elasticsearch-head
3. 测试：http://192.168.1.66:9200/_plugin/head/
### 安装中文分词器IKAnalyzer
1. 下载中文分词器：https://github.com/medcl/elasticsearch-analysis-ik/releases?after=v5.6.4
2. 上传后拷贝到容器中：docker container cp elasticsearch-analysis-ik-1.10.6.tar.gz elasticsearch:/usr/share/elasticsearch/plugins
3. 进行解压操作：tar -xvf elasticsearch-analysis-ik-1.10.6.tar.gz
4. 重新启动容器：docker restart elasticsearch
5. 测试：
  POST:http://192.168.1.66:9200/_analyze
  JSON:{"analyzer":"ik","text":"联想是全球最大的笔记本厂商"}