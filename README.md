# easyMyBatis
简洁版mybatis，只生成自己想用的crud。
# 项目主要结构的树形
```java
├─src
   ├─main
      ├─java
      │  └─com
      │      └─easyMyBatis
      │          │  RunApplication.java
      │          │  
      │          ├─bean
      │          │      Constants.java
      │          │      FieldInfo.java
      │          │      TableInfo.java
      │          │      
      │          ├─builder
      │          │      BuildBase.java
      │          │      BuildComment.java
      │          │      BuildController.java
      │          │      BuildMappers.java
      │          │      BuildMapperXml.java
      │          │      BuildPo.java
      │          │      BuildQuery.java
      │          │      BuildService.java
      │          │      BuildServiceImpl.java
      │          │      BuildTable.java
      │          │      
      │          └─utils
      │                  DateUtils.java
      │                  JsonUtils.java
      │                  PropertiesUtils.java
      │                  StringUtils.java
      │                  
      └─resources
          │  application.properties
          │  
          └─template
                  ABaseController.txt
                  AGlobalExceptionHandlerController.txt
                  BaseMapper.txt
                  BaseQuery.txt
                  BusinessException.txt
                  DateTimePatternEnum.txt
                  DateUtil.txt
                  PageSize.txt
                  PaginationResultVO.txt
                  ResponseCodeEnum.txt
                  ResponseVO.txt
                  SimplePage.txt
```         
# 使用建议
## 修改application.properties文件
修改参数连接数据库
```java
db.driver.name=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true
db.username=root
db.password=123456
```
修改作者名称
```java
#作者名字
author.comment=阿牛
```
下面的为规定写法，不必修改
```java
#需要忽略的属性
ignore.bean.tojson.field=companyId
ignore.bean.tojson.expression=@JsonIgnore
ignore.bean.tojson.class=import com.fasterxml.jackson.annotation.JsonIgnore
#时间格式序列化
bean.date.format.expression=@JsonFormat(pattern = "%s", timezone = "GMT+8")
bean.date.format.class=import com.fasterxml.jackson.annotation.JsonFormat
#时间格式反序列化
bean.date.unformat.expression=@DateTimeFormat(pattern = "%s")
bean.date.unformat.class=import org.springframework.format.annotation.DateTimeFormat
```
是否忽略表前缀（一般选择true）
```java
#是否忽略表前缀
ignore.table.prefix=true
```
按照个人喜好修改（建议不要改变）
```java
#参数bean后缀
suffix.bean.query=Query
#参数模糊搜索后缀
suffix.bean.query.fuzzy=Fuzzy
#Mappers后缀
suffix.mappers=Mapper
#参数日期起始
suffix.bean.query.time.start=Start
suffix.bean.query.time.end=End
#包名
package.base=cn.pan
package.po=entity.po
package.query=entity.query
package.vo=entity.vo
package.enum=entity.enums
package.utils=utils
package.mappers=mappers
package.service=service
package.service.impl=service.impl
package.exception=exception
package.controller=controller
```
修改项目路径
```java
#文件输出路径
path.base=D:/Maven-spacework/easy-java/easyJava-demo/src/main/
```

