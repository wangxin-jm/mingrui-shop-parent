# Elasticsearch介绍和安装

Elasticsearch是一个基于Apache Lucene(TM)的开源搜索引擎。Lucene是迄今为止最先进、性能、 功能 上来讲最全的搜索引擎库。 但是，Lucene只是一个库。想要使用它，你必须使用Java来作为开发 语言并将其直接集成到你的应用中，（以导入jar包的形式），Lucene非常复杂，你需要深入了解检索 的相关知识来理解它是如何工作的。 Elasticsearch也使用Java开发并使用Lucene作为其核心来实现所有索引和搜索的功能，是一个独立的 web项目。但是它的使用方式是通过简单的RESTful API来隐藏Lucene的复杂性，从而让全文搜索变得 简单。 Elasticsearch不仅仅是Lucene和全文搜索，我们还能这样去描述它： 分布式的实时(快!)文件存储，每个字段都被索引并可被搜索 分布式的实时分析搜索引擎 可以扩展到上百台服务器，处理PB级结构化或非结构化数据

## 简介

Elastic官网：https://www.elastic.co/cn/

Elasticsearch官网：https://www.elastic.co/cn/products/elasticsearch

Elastic有一条完整的产品线：Elasticsearch、Kibana、Logstash(处理分布式微服务日志)等，前面说的 三个就是大家常说的ELK技术栈。

如上所述，Elasticsearch具备以下特点： 分布式，无需人工搭建集群 Restful风格，一切API都遵循Rest原则，容易上手 近实时搜索，数据更新在Elasticsearch中几乎是完全同步的。 Elasticsearch 是一个分布式的搜索引擎，底层基于lucene,主要特点是可以完成全文检索， 支持海量数据pb级别，横向拓展，数据分片，等一系列功能、 良好的查询机制，支持模糊，区间，排序，分组，分页，等常规功能 横向可扩展性：只需要增加一台服务器，做一点儿配置，启动一下ES进程就可以并入集 分片机制提供更好的分布性：同一个索引分成多个分片（sharding），这点类似于HDFS的块机 制；分而治之的方式来提升处理效率，相信大家都不会陌生； 不足： 没有细致的权限管理机制,没有像MySQL那样的分各种用户，每个用户有不同的权限 单台节点部署的话，并发查询效率并不高， 使用场景： 爱奇艺搜电影，京东搜手机，qq搜好友，百度地址各种信息，嘀嘀打车，邮件搜索，微信还有，美团饭 店，旅游景点，可以说，搜索场景无处不在

## 安装和配置

为了模拟真实场景，我们将在linux下安装Elasticsearch。

### **安装es**

#### 将压缩包上传到linux系统中

tar -zxvf elasticsearch-7.5.1-linux-x86_64.tar.gz #解压压缩包 

groupadd esgroup #创建用户组 

useradd esuser -g esgroup -p 123456 #在用户组下新建用户 

cd elasticsearch-7.5.1/config/ #进入配置文件目录 

vi jvm.options #修改JVM参数(默认1G) 

-Xms512m 

-Xmx512m

#### 保存并退出

vi elasticsearch.yml 

cluster.name: my-application #集群名称 

node.name: node-1 #当前节点名称 

path.data: /shenyaqi/es/elasticsearch-7.5.1/data #数据存放目录 

path.logs: /shenyaqi/es/elasticsearch-7.5.1/logs #日志目录 

network.host: 0.0.0.0 #允许任意ip访问 

discovery.seed_hosts: ["119.45.191.248"] #所有节点ip,由于当前环境采用单节点,所以只写当 前节点ip地址 cluster.initial_master_nodes: ["node-1"] #声明master节点,由于当前是单节点环境所以 master节点就是当前节点[此处可以写ip:9200也可以写node.name]

#### 保存并退出

cd ../../ #进入es的上级目录

chown -R esuser:esgroup elasticsearch-7.5.1 #给用户授权

su esuser #切换用户

cd elasticsearch-7.5.1/bin/ #进入bin目录

./elasticsearch 启动es

#### 如果出现上图错误

su root

sysctl -w vm.max_map_count=262144

sysctl -a|grep vm.max_map_count

su esuser

#### 浏览器访问ip:9200

##### 有效果就是安装成功

es安装成功

#### 2.2.2 安装ik分词器

进入es目录下的plugins目录

mkdir ik

cd ik

#### 将elasticsearch-analysis-ik-7.5.1.zip上传到ik目录

unzip elasticsearch-analysis-ik-7.5.1.zip #解压压缩包 # 解压完成之后最好切换回root 用户重新给文件夹授权

rm -rf elasticsearch-analysis-ik-7.5.1.zip #删除压缩包

#### 重启es

cd ../bin/ 

./elasticsearch 

./elasticsearch -d #后台启动

#### 分词器安装成功

### 安装kibana(可视化工具)

#### 什么是kibana

Kibana是一个基于Node.js的Elasticsearch索引库数据统计工具，可以利用Elasticsearch的聚合功能， 生成各种图表，如柱形图，线状图，饼图等。 而且还提供了操作Elasticsearch索引数据的控制台，并且提供了一定的API提示，非常有利于我们学习 Elasticsearch的语法。

#### 安装

因为Kibana依赖于node，我们的虚拟机没有安装node，而window中安装过。所以我们选择在window 下使用kibana。 版本与elasticsearch保持一致

解压即可

#### 配置运行

进入安装目录下的config目录，修改kibana.yml文件：

#### 修改elasticsearch服务器的地址：

elasticsearch.hosts: ["http://81.70.211.185:9200"] #81.70.211.185替换成自己虚拟的 IP地址

#### 进入安装目录下的bin目录：

#### 2.2.4 测试分词器和kibana

浏览器输入ip:5601

GET _search { "query": { "match_all": {} } } POST _analyze { "analyzer": "ik_max_word", "text": "我爱北京天安门" }

####  倒排索引

逻辑结构部分是一个倒排索引表： 1、将要搜索的文档内容分词，所有不重复的词组成分词列表。 2、将搜索的文档最终以Document方式存储起来。 3、每个词和docment都有关联。

####  API

Elasticsearch提供了Rest风格的API，即http请求接口，而且也提供了各种语言的客户端API

#### Rest风格API

 https://www.elastic.co/guide/en/elasticsearch/reference/current/getting-started.html

####  客户端API

  https://www.elastic.co/guide/en/elasticsearch/client/index.html.

#### 操作索引

####  基本概念

elasticsearch也是基于Lucene的全文检索库，本质也是存储数据，很多概念与MySQL类似的。

详细说明： 上图列出了，数据库与es所有之间的所有名词对应关系，尤其注意一栏，index 在关系型数据库当 中是索引的意思，设置后，可提高检索效率，但在es索引库当中everything is indexed 每一列默 认都是索引，突出了，es是搜索引擎效率快

####  创建索引

#### 语法

Elasticsearch采用Rest风格API，因此其API就是一次http请求，你可以用任何工具发起http请求 创建索引的请求格式：

请求方式：PUT

请求路径：/索引库名

请求参数：json格式：

{ 

​		"settings": {

 		"number_of_shards": 1, "number_of_replicas": 0 

​		} 

}

settings：

索引库的设置 number_of_shards：是数据分片数(数据分几块存储[说白了,就是将整个数据集存到不同的位 置])，如果只有一台机器，设置为1 number_of_replicas：数据备份数，如果只有一台机器，设置为0(不进行备份),

#### 测试

注意:使用postmane测试的时候需要设置headers的ContentType的值为application/json

可以看到索引创建成功了。

### 使用kibana创建

kibana的控制台，可以对http请求进行简化，示例： 使用kibana 创建索引库 mrshop3

#### 查看索引设置

GET /索引库名

GET * //查询所有 索引库信息

#### 删除索引

DELETE /索引库名

当然，我们也可以用HEAD请求，查看索引是否存在：

#### 创建mapping

##### 语法

PUT /student {

 	"mappings": { 

​		"properties": { 

​			"name":{

 			"type": "text", "index": true, "store": true, "analyzer": "ik_max_word" 

}, "age":{ "type": "integer"

 }, "birthday":{ "type": "date", "format": "yyyy-MM-dd" 

} 

}

 }, "settings": { "number_of_shards": 1, "number_of_replicas": 0 } }



#### 类型

String类型，又分两种：

 		text：可分词，不可参与聚合 

​			keyword：不可分词，数据会作为完整字段进行匹配，可以参与聚合

Numerical：数值类型，分两类 

基本数据类型：long、interger、short、byte、double、float、half_float 

浮点数的高精度类型：scaled_float 需要指定一个精度因子，比如10或100。elasticsearch会把真实值乘以这个因子后存 储，取出时再还原。

Date：日期类型 

​	elasticsearch可以对日期格式化为字符串存储，但是建议我们存储为毫秒值，存储为long， 节省空间。

####  index

该 index 选项控制是否对字段值建立索引。它接受 true 或 false ，默认为 true 。未索引的字段不可 查询。

#### store

默认情况下，对字段值进行索引以使其可搜索，但不存储它们。这意味着可以查询该字段，但是无法检 索原始字段值。

#### analyzer

指定分词器(我们使用的ik分词器

#### 获取当前索引的setting信息

GET /indexName/_settings

#### 获取当前索引的mapping信息

GET /indexName/_mapping

#### 获取所有的索引mapping信息

GET /_all/_mapping

#### 添加数据

POST /student/_doc/1 

{ 

​		"name":"赵俊浩",

​		 "age":18, 

​		"birthday":"2020-09-02"

 }

####  生成随机id

注意:新增数据时如果使用自定义id那使用put请求,使用随机id使用post请求

POST /student/_doc 

{ "name":"赵俊浩", 

"age":18, 

"birthday":"2020-09-02" }

#### 松散的列设计

事实上Elasticsearch非常智能，你不需要给索引库设置任何mapping映射，

如果在增加数据时有 没有提前定义的属性字段，根据属性值自动创建

POST /student/_doc/3 {

 "name":"赵俊浩222", 

"age":18, 

"birthday":"2020-09-02", 

"sex":"男" }

##### 我们定义mapping的时候没有声明sex字段也可以创建成功

通过id值查询信息

GET /student/_doc/3

#### 修改数据

PUT /student/_doc/1 { "name":"赵俊浩1", "age":18, "birthday":"2020-09-02" }

#### 删除数据

DELETE /student/_doc/1

### 查询

我们从4块来讲查询： 

​		基本查询

 		_source 过滤 

​		结果过滤 

​		高级查询 

​		排序

#### 基本查询

![image-20210311211205840](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311211205840.png)

这里的query代表一个查询对象，里面可以有不同的查询属性

查询类型：

​		例如： match_all ， match ， term ， range 等等	

查询条件：查询条件会根据类型的不同，写法也有差异，后面详细讲解

#### 查询所有（match_all)



![image-20210311211322084](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311211322084.png)

query ：代表查询对象

match_all ：代表查询所有

took：查询花费时间，单位是毫秒

time_out：是否超时

_shards：分片信息

hits：搜索结果总览对象

​	total：搜索到的总条数

​	max_score：所有结果中文档得分的最高分

​	hits：搜索结果的文档对象数组，每个元素是一条搜索到的文档信息

​		_index：索引库

​		_type：文档类型

​		_id：文档id 

​		_score：文档得分 

​		_source：文档的源数据

#### 匹配查询（match）

数据好准备

#### or关系

match 类型查询，会把查询条件进行分词，然后进行查询,多个词条之间是or的关系

![image-20210311211550285](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311211550285.png)

在上面的案例中，分词后手机不仅会查询到华为，而且与手机相关的其他品牌都会查询到，符合 “手机” 这个词条的都能查询到

#### and关系

某些情况下，我们需要更精确查找，我们希望这个关系变成 and ，可以这样做：

![image-20210311211622504](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311211622504.png)

#### or和and之间？

在 or 与 and 间二选一有点过于非黑即白。 如果用户给定的条件分词后有 5 个查询词项，想查找只包 含其中 4 个词的文档，该如何处理？将 operator 操作符参数设置成 and 只会将此文档排除。

有时候这正是我们期望的，但在全文搜索的大多数应用场景下，我们既想包含那些可能相关的文档，同 时又排除那些不太相关的。换句话说，我们想要处于中间某种结果。

match 查询支持 minimum_should_match 最小匹配参数， 这让我们可以指定必须匹配的词项数用来 表示一个文档是否相关。我们可以将其设置为某个具体数字，更常用的做法是将其设置为一个 百分数 ， 因为我们无法控制用户搜索时输入的单词数量：

![image-20210311211704310](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311211704310.png)

#### 多字段查询(multi_match) multipart/form-data

multi_match 与 match 类似，不同的是它指定在多个字段中查询这个关键字

![image-20210311211731442](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311211731442.png)



#### 词条匹配(term)

term 查询被用于精确值 匹配，这些精确值可能是数字、时间、布尔或者那些未分词的字符串

![image-20210311211757943](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311211757943.png)

#### 多词条精确匹配(terms)

terms 查询和 term 查询一样，但它允许你指定多值进行匹配。如果这个字段包含了指定值中的任何一 个值，那么这个文档满足条件

![image-20210311211819818](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311211819818.png)

####  结果过滤

默认情况下，elasticsearch在搜索的结果中，会把文档中保存在 _source 的所有字段都返回。 如果我们只想获取其中的部分字段，我们可以添加 _source 的过滤

#### 直接指定返回字段

![image-20210311211847287](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311211847287.png)

#### 指定includes和excludes

如果要查询的字段或者 不要字段过多，可以通过 包含includes， 或者 排除excludes，

 includes：来指定想要显示的字段 

excludes：来指定不想要显示的字段

二者都是可选的。

![image-20210311211949139](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311211949139.png)

#### 高级查询

##### 布尔组合（bool

bool 把各种其它查询通过 must （与）、 must_not （非）、 should （或）的方式进行组合

![image-20210311212019684](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311212019684.png)

![image-20210311212031017](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311212031017.png)

![image-20210311212042236](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311212042236.png)

####  范围查询(range)

range 区间查询，值 >= 条件 <= 值

例如：2000-4999之间的价格

![image-20210311212114660](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311212114660.png)

![image-20210311212127441](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311212127441.png)

#### 模糊查询(fuzzy)

我们新增一个商品：

![image-20210311212149299](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311212149299.png)

fuzzy 查询是 term 查询的模糊等价。它允许用户搜索词条与实际词条的拼写出现偏差，但是偏差的 编辑距离不得超过2：

![image-20210311212205842](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311212205842.png)

我们可以通过 fuzziness 来指定允许的编辑容错距离：

![image-20210311212218641](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311212218641.png)

#### 过滤(filter)

条件查询中进行过滤 所有的查询都会影响到文档的评分及排名。如果我们需要在查询结果中进行过滤，并且不希望过滤条件 影响评分，那么就不要把过滤条件作为查询条件来用。而是使用 filter 方式： 查询条件会影响文档数据的成绩优先级，如果有的条件 不希望影响成绩优先级，那么用fliter方法

注意： filter 中还可以再次进行 bool 组合条件过滤。

两次结果筛选到的数据是一样的，但是成绩是不一样的，不使用filter会加重成绩得分

如果一次查询只有过滤，没有查询条件，不希望进行评分，我们可以使用 constant_score 取代只有 filter 语句的 bool 查询。在性能上是完全相同的，但对于提高查询简洁性和清晰度有很大帮助。

![image-20210311212321842](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311212321842.png)

#### 排序

##### 单字段排序

指定排序方式 

sort 可以让我们按照不同的字段进行排序，并且通过 order 指定排序的方式

##### 多字段排序

多个组合成一个字段进行排序

使用 title分词查询，匹配的结果首先按照评分排序，然后按照价格排序：

#### 聚合aggregations

聚合可以让我们极其方便的实现对数据的统计、分析。例如：

每个月手机的销量？ 

5000价格以上手机的平均价格？ 

每种品牌下的手机有几种？

##### 基本概念

###### 桶（bucket） 

桶的作用，是按照某种方式对数据进行分组，每一组数据在ES中称为一个 桶 ，例如我们根据国籍对人 划分，可以得到 中国桶 、 英国桶 ， 日本桶 ……或者我们按照年龄段对人进行划分： 0~10,10~20,20~30,30~40等。

Elasticsearch中提供的划分桶的方式有很多： 

Date Histogram Aggregation：根据日期阶梯分组，例如给定阶梯为周，会自动每周分为一组 

Histogram Aggregation：根据数值阶梯分组，与日期类似 

Terms Aggregation：根据词条内容分组，词条内容完全匹配的为一组 

Range Aggregation：数值和日期的范围分组，指定开始和结束，然后按段分组

综上所述，我们发现bucket aggregations 只负责对数据进行分组，并不进行计算，因此往往bucket中 往往会嵌套另一种聚合：metrics aggregations即度量

###### 度量（metrics）

分组完成以后，我们一般会对组中的数据进行聚合运算，例如求平均值、最大、最小、求和等，这些在 ES中称为 度量

比较常用的一些度量聚合方式：

Avg Aggregation：求平均值 

Max Aggregation：求最大值 

Min Aggregation：求最小值 

Percentiles Aggregation：求百分比 

Stats Aggregation：同时返回

avg、max、min、sum、count等 

Sum Aggregation：求和 

Top hits Aggregation：求前几 

Value Count Aggregation：求总数

#### 聚合为桶

首先，我们按照 汽车的颜色 color来 划分 桶 等同与sql中group的概念

![image-20210311212659567](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311212659567.png)

size：查询的数据条数，0代表一条也不展示

aggs：声明这是一个聚合查询，是aggregations的缩写

gro_color：给这次聚合起一个名字，任意。

terms：划分桶的方式，这里是根据词条划分

field：划分桶的字段

Size:默认取10条数据，现在只有4个颜色，所以说全部取出，也可以删掉

hits：查询结果

aggregations：聚合的结果

gro_color：我们定义的聚合分组名称

buckets：查找到的桶，每个不同的color字段值都会形成一个桶

key：这个桶对应的color字段的值

doc_count：这个桶中的文档数量

#### 桶内度量

前面的例子告诉我们每个桶里面的文档数量，这很有用。 但通常，我们的应用需要提供更复杂的文档度 量。 例如，每种颜色下车辆的最高价格， 因此，我们需要告诉Elasticsearch 使用哪个字段 ， 使用何种度量方式 进行运算，这些信息要嵌套在 桶 内， 度量 的运算会基于 桶 内的文档进行 现在，我们为刚刚的聚合结果添加 求价格最高值的度量： 等同于 sql中 聚合函数的概念

#### 桶内嵌套桶

刚刚的案例中，我们在桶内嵌套度量运算。事实上桶不仅可以嵌套运算， 还可以再嵌套其它桶。也就是 说在每个分组中，再分更多组。 比如：我们想统计每种颜色的汽车中，分别属于哪个制造商，按照 make 字段再进行分桶

原来的color桶和max计算我们不变 make：在嵌套的aggs下新添一个桶，叫做brand terms：桶的划分类型依然是词条 filed：这里根据make字段进行划分

#### 划分桶的其它方式

前面讲了，划分桶的方式有很多，例如： Date Histogram Aggregation：根据日期阶梯分组，例如给定阶梯为周，会自动每周分为一组 Histogram Aggregation：根据数值阶梯分组，与日期类似 Terms Aggregation：根据词条内容分组，词条内容完全匹配的为一组 Range Aggregation：数值和日期的范围分组，指定开始和结束，然后按段分组 上方的案例中，我们采用的是Terms Aggregation，词条划分桶。 接下来，我们再学习几个比较实用的：

#### 阶梯分桶Histogram

###### 原理

histogram是把数值类型的字段，按照一定的阶梯大小进行分组。你需要指定一个阶梯值（interval）来 划分阶梯大小。 把汽车价格区间作为分组条件，看下哪个价格区间的汽车数量，或者最高价等

###### 操作一下：

比如，我们对汽车的价格进行分组，指定间隔interval为100000：

#### 范围分桶range.

范围分桶与阶梯分桶类似，也是把数字按照阶段进行分组，只不过range方式需要你自己指定每一组的 起始和结束大小。

### Spring Data Elasticsearch

Elasticsearch提供的Java客户端有一些不太方便的地方：

​		很多地方需要拼接Json字符串，在java中拼接字符串有多恐怖你应该懂的 

​		需要自己把对象序列化为json存储 

​		查询到结果也需要自己反序列化为对象

#### 简介

Spring Data Elasticsearch是Spring Data项目下的一个子模块。 查看 Spring Data的官网：https://spring.io/projects/spring-data

Spring Data的任务是为数据访问提供一个熟悉且一致的，基于Spring的编程模型，同时仍保留基础数 据存储的特殊特征。

它使使用数据访问技术，关系和非关系数据库，map-reduce框架以及基于云的数据服务变得容易。这 是一个总括项目，其中包含许多特定于给定数据库的子项目。这些项目是与这些令人兴奋的技术背后的 许多公司和开发人员共同开发的。

![image-20210311213214123](C:\Users\Angelina\AppData\Roaming\Typora\typora-user-images\image-20210311213214123.png)

用于Elasticsearch的Spring Data是Spring Data项目的一部分，该项目旨在为新数据存储提供熟悉且一 致的基于Spring的编程模型，同时保留特定于存储的功能。

Spring Data Elasticsearch项目提供了与Elasticsearch搜索引擎的集成。Spring Data Elasticsearch的 关键功能区域是以POJO为中心的模型，用于与Elastichsearch文档进行交互并轻松编写存储库样式数据 访问层。

Spring配置支持使用基于Java的 @Configuration 类或ES客户端实例的XML名称空间。 ElasticsearchTemplate 帮助程序类，可提高执行常规ES操作的效率。包括文档和POJO之间的 集成对象映射。 与Spring的转换服务集成的功能丰富的对象映射 基于注释的映射元数据，但可扩展以支持其他元数据格式 Repository 接口的自动实现，包括对自定义查找器方法的支持。 CDI对存储库的支持

#### ESDemo

##### 新建项目

#####  pom.xml

```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.3.1.RELEASE</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
        </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

##### application.yml

```
spring:
  elasticsearch:
    rest:
      uris: 81.70.211.185:9200
```

##### 启动类

```java
package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 2 * @ClassName RunTestEsApplication
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/3/3
 * 6 * @Version V1.0
 * 7
 **/
@SpringBootApplication
public class RunTestEsApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunTestEsApplication.class);
    }
}
```

#### entity.

```
package com.baidu.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 2 * @ClassName GoodsEntity
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/3/3
 * 6 * @Version V1.0
 * 7
 **/
//声明当前类是一个文档(indexName="索引名称", shards="索引的分片数",replicas="索引的副本数")
@Document(indexName = "goods2",shards = 1,replicas = 0)
public class GoodsEntity {


    @Id
    private Long id;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title; //标题
    @Field(type = FieldType.Keyword)
    private String category;// 分类
    @Field(type = FieldType.Keyword)
    private String brand; // 品牌
    @Field(type = FieldType.Double)
    private Double price; // 价格
    //index = false 不参与索引搜索
        /*
      * 设置index为false的好处是，当您为文档建立索引时，Elasticsearch将不必为该字段构建反向
    索引。结果，索引文档将稍快一些。同样，由于该字段在磁盘上将没有持久化的反向索引，因此您将使用更少
    的磁盘空间。*/
    @Field(index = false,type = FieldType.Keyword)
    private String images; // 图片地址

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "GoodsEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                ", images='" + images + '\'' +
                '}';
    }
}
```



#### 新建测试类

```
//让测试在Spring容器环境下执行
@RunWith(SpringRunner.class)
//声明启动类,当测试方法运行的时候会帮我们自动启动容器
@SpringBootTest(classes = {RunTestEsApplication.class})
```

#### 创建索引

@Autowired
  private ElasticsearchRestTemplate elasticsearchRestTemplate;

  /*
  创建索引
  */
  @Test
  public void createGoodsIndex(){
    IndexOperations indexOperations =
elasticsearchRestTemplate.indexOps(IndexCoordinates.of("indexname"));
    indexOperations.create();//创建索引
    //indexOperations.exists() 判断索引是否存在
    System.out.println(indexOperations.exists()?"索引创建成功":"索引创建失败");
 }

#### 创建映射

/*
  创建映射
  */
  @Test
  public void createGoodsMapping(){
    //此构造函数会检查有没有索引存在,如果没有则创建该索引,如果有则使用原来的索引
    IndexOperations indexOperations =
elasticsearchRestTemplate.indexOps(GoodsEntity.class);
    indexOperations.createMapping();//创建映射,不调用此函数也可以创建映射,这就是高
版本的强大之处
    System.out.println("映射创建成功");
 }

#### 删除索引

**/*
  删除索引
  */
  @Test
  public void deleteGoodsIndex(){
    IndexOperations indexOperations =
elasticsearchRestTemplate.indexOps(GoodsEntity.class);
    indexOperations.delete();
    System.out.println("索引删除成功");

 }**

##### 新建GoodsEsRepository

import com.mr.entity.GoodsEntity;
import
org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
/**
* @ClassName GoodsEsRepository

* @Description: TODO

* @Author shenyaqi

* @Date 2020/9/3

* @Version V1.0
  **/
  public interface GoodsEsRepository extends
  ElasticsearchRepository<GoodsEntity,Long> {
  }

* ##### 注入repository

  #### 新增

  /*
    新增文档
    */
    @Test
    public void saveData(){
      GoodsEntity entity = new GoodsEntity();
      entity.setId(1L);
      entity.setBrand("小米");
      entity.setCategory("手机");
      entity.setImages("xiaomi.jpg");
      entity.setPrice(1000D);
      entity.setTitle("小米3");
      goodsEsRepository.save(entity);
      System.out.println("新增成功");
   }

  #### 批量新增

  /*
    批量新增文档
    */
    @Test
    public void saveAllData(){
      GoodsEntity entity = new GoodsEntity();
      entity.setId(2L);
      entity.setBrand("苹果");
      entity.setCategory("手机");
      entity.setImages("pingguo.jpg");
      entity.setPrice(5000D);entity.setTitle("iphone11手机");
      GoodsEntity entity2 = new GoodsEntity();
      entity2.setId(3L);
      entity2.setBrand("三星");
      entity2.setCategory("手机");
      entity2.setImages("sanxing.jpg");
      entity2.setPrice(3000D);
      entity2.setTitle("w2019手机");
      GoodsEntity entity3 = new GoodsEntity();
      entity3.setId(4L);
      entity3.setBrand("华为");
      entity3.setCategory("手机");
      entity3.setImages("huawei.jpg");
      entity3.setPrice(4000D);
      entity3.setTitle("华为mate30手机");
      goodsEsRepository.saveAll(Arrays.asList(entity,entity2,entity3));
      System.out.println("批量新增成功");
   }

#### 删除文档



/*
  删除文档
  */
  @Test
  public void delData(){
    GoodsEntity entity = new GoodsEntity();
    entity.setId(1L);
    goodsEsRepository.delete(entity);
    System.out.println("删除成功");
 }

#### 查询所有



/*
  查询所有
  */
  @Test
  public void searchAll(){
    //查询总条数
    long count = goodsEsRepository.count();
    System.out.println(count);
    //查询所有数据
    Iterable<GoodsEntity> all = goodsEsRepository.findAll();
    all.forEach(goods -> {
      System.out.println(goods);
   });
 }



#### 条件查询

List<GoodsEntity> findAllByAndTitle(String title);
  List<GoodsEntity> findByAndPriceBetween(Double start,Double end);



/*
  条件查询
  */
  @Test
  public void searchByParam(){
    List<GoodsEntity> allByAndTitle =
goodsEsRepository.findAllByAndTitle("手机");
    System.out.println(allByAndTitle);
    System.out.println("===============================");
    List<GoodsEntity> byAndPriceBetween =
goodsEsRepository.findByAndPriceBetween(1000D, 3000D);
    System.out.println(byAndPriceBetween);



#### 自定义查询



/*
  自定义查询
  */
  @Test
  public void customizeSearch(){
    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
    queryBuilder.withQuery(
        QueryBuilders.boolQuery()
           .must(QueryBuilders.matchQuery("title","华为"))
          
.must(QueryBuilders.rangeQuery("price").gte(1000).lte(10000))
   );
    //排序

 queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
    //分页
    //当前页 -1
    queryBuilder.withPageable(PageRequest.of(0,10));
    SearchHits<GoodsEntity> search =
elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);
    search.getSearchHits().stream().forEach(hit -> {
      System.out.println(hit.getContent());
   });
 }



#### 高亮

/*
  高亮
  */
  @Test
  public void customizeSearchHighLight(){
    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
    //构建高亮查询
    HighlightBuilder highlightBuilder = new HighlightBuilder();
    HighlightBuilder.Field title = new HighlightBuilder.Field("title");
    title.preTags("<span style='color:red'>");
    title.postTags("</span>");
    highlightBuilder.field(title);
    queryBuilder.withHighlightBuilder(highlightBuilder);//设置高亮queryBuilder.withQuery(
        QueryBuilders.boolQuery()
           .must(QueryBuilders.matchQuery("title","华为手机"))
          
.must(QueryBuilders.rangeQuery("price").gte(1000).lte(10000))
   );

 queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
    queryBuilder.withPageable(PageRequest.of(0,2));
    SearchHits<GoodsEntity> search =
elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);
    List<SearchHit<GoodsEntity>> searchHits = search.getSearchHits();
    //重新设置title
    List<SearchHit<GoodsEntity>> result = searchHits.stream().map(hit -> {
      Map<String, List<String>> highlightFields =
hit.getHighlightFields();
      hit.getContent().setTitle(highlightFields.get("title").get(0));
      return hit;
   }).collect(Collectors.toList());
    System.out.println(result);
 }

#### 高亮工具类封装

```
package com.baidu.util;

import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 2 * @ClassName ESHighLightUtil
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/3/3
 * 6 * @Version V1.0
 * 7
 **/
public class ESHighLightUtil<T> {

    //构建高亮字段buiilder
    public static HighlightBuilder getHighlightBuilder(String ...highLightField){
        HighlightBuilder highlightBuilder = new HighlightBuilder();

        Arrays.asList(highLightField).forEach(hlf ->{
            HighlightBuilder.Field field = new HighlightBuilder.Field(hlf);
            field.preTags("<span style='color:red'>");
            field.postTags("</span>");
            highlightBuilder.field(field);

        });

        return highlightBuilder;

    }

    //将返回的内容替换成高亮
    public static <T> List<SearchHit<T>> getHighLightHit(List<SearchHit<T>> list){

        return  list.stream().map(hit -> {
            //得到高亮字段
            Map<String, List<String>> highlightFields = hit.getHighlightFields();
            highlightFields.forEach((key, value) -> {


                try {
                    T content = hit.getContent();
                    Method method = content.getClass().getMethod("set" +
                            String.valueOf(key.charAt(0)).toUpperCase() + key.substring(1), String.class);
                    //执行set方法并赋值
                    method.invoke(content,value.get(0));


                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }


            });
            return hit;

        }).collect(Collectors.toList());


    }

    //首字母大写,效率最高
    private static String firstCharUpperCase(String name){
        char[] chars = name.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

}
```



#### 聚合为桶

@Test
  public void searchAgg(){
    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
    queryBuilder.addAggregation(
        AggregationBuilders.terms("brand_agg").field("brand")
   );
    SearchHits<GoodsEntity> search =
elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);
    Aggregations aggregations = search.getAggregations();
    //terms 是Aggregation的子接口
    //Aggregation brand_agg = aggregations.get("brand_agg");/
    Terms terms = aggregations.get("brand_agg");
    List<? extends Terms.Bucket> buckets = terms.getBuckets();
    buckets.forEach(bucket -> {System.out.println(bucket.getKeyAsString() + ":" +
bucket.getDocCount());
   });
    System.out.println(search);
 }

#### 嵌套聚合，聚合函数值

/*
  聚合函数
  */
  @Test
  public void searchAggMethod(){
    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
    queryBuilder.addAggregation(
        AggregationBuilders.terms("brand_agg")
           .field("brand")
            //聚合函数
          
.subAggregation(AggregationBuilders.max("max_price").field("price"))
   );
    SearchHits<GoodsEntity> search =
elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);
    Aggregations aggregations = search.getAggregations();
    Terms terms = aggregations.get("brand_agg");
    List<? extends Terms.Bucket> buckets = terms.getBuckets();
    buckets.forEach(bucket -> {
      System.out.println(bucket.getKeyAsString() + ":" +
bucket.getDocCount());
      //获取聚合
      Aggregations aggregations1 = bucket.getAggregations();
      //得到map
      Map<String, Aggregation> map = aggregations1.asMap();
      map.forEach((key,value) -> {
        //需要强转,为什么?
        //通过debug我们知道这个value的值是ParsedMax,ParsedMax是一个类(实现类),
通过层层关系查找到最后他实现的接口是Aggregation,
        //接口类型转实现类需要强转
        //上面的Terms是Aggregation的子接口,所以Terms不需要强转
        ParsedMax v = (ParsedMax) value;
        System.out.println("key:" + key + "value:" + v.getValue());
     });
   });
 }



