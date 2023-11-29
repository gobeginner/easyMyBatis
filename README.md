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
修改application.properties文件
