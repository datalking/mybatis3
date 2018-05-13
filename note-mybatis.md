# note-mybatis
orm映射、查询缓存、动态sql、二级缓存


- resultMap & resultType
- 两者都是表示查询结果集与java对象之间的一种关系，处理查询结果集，映射到java对象。
- resultMap表示将查询结果集中的列一一映射到bean对象的各个属性，映射的查询结果集中的列标签可以根据需要灵活变化，并且，在映射关系中，还可以通过typeHandler设置实现查询结果值的类型转换，比如布尔型与0/1的类型转换。
- resultType 表示的是bean中的对象类，此时可以省略掉resultMap标签的映射，但是必须保证查询结果集中的属性 和 bean对象类中的属性是一一对应的，此时大小写不敏感，但是有限制
- resultType可以指定pojo将查询结果映射为pojo，但需要pojo的属性名和sql查询的列名一致方可映射成功。
如果sql查询字段名和pojo的属性名不一致，可以通过resultMap将字段名和属性名作一个对应关系 ，resultMap实质上还需要将查询结果映射到pojo对象中。
resultMap可以实现将查询结果映射为复杂类型的pojo，比如在查询结果映射对象中包括pojo和list实现一对一查询和一对多查询。

- parameterMap(不推荐) & parameterType
- parameterMap和resultMap类似，表示将查询结果集中列值的类型一一映射到java对象属性的类型上，在开发过程中不推荐这种方式。
- 一般使用parameterType直接将查询结果列值类型自动对应到java对象属性类型上，不再配置映射关系一一对应，例如上述代码中下划线部分表示将查询结果类型自动对应到hdu.terence.bean.Message的Bean对象属性类型。
- parameterMap很少使用，更多的是使用parameterClass，pm基本思想是定义一系列有次序的参数系列，用于匹配JDBC PreparedStatement的值符号

- resultMap和ParameterMap书写拼写要使用#{}，resultType 和parameterType类型使用${}，使用例子如下：  
`Select ID，COMMAND from Message where COMMAND=#{command}`  
`Select ID，COMMAND from Message where COMMAND=‘${command}`  
前者解析为： `Select ID，COMMAND from Message where COMMAND=?` 具有预编译效果  
后者解析为： `Select ID，COMMAND from Message where COMMAND=段子` 不具有预编译效果    
  

- ResultMaps 被用来 将 SQL SELECT 语句的结果集映射到 JavaBeans 的属性中

- 类型处理器 typeHandlers  
当 MyBatis 发现 属性的类型属于上述类型，他会使用对应的类型处理器将值设置到 PreparedStatement 中，
同样地，当从 SQL 结果集构 建 JavaBean 时，也有类似的过程。

- jdbc需要使用大量重复代码
    - 创建连接
    - 创建statement
    - 设置参数
    - 关闭资源
    - 管理资源    
MyBatis 抽象了上述的这些相同的任务，如准备需要被执行的 SQL statement 对象并且将 Java 对象作为输入数据 传递给 statement 对象的任务，进而开发人员可以专注于真正重要的方面。   
MyBatis 自动化了将从输入的 Java 对象中的属性设置成查询参数、从 SQL 结果集上生成 Java 对象这两个过程。  



- ibatis 2 dao is deprecated    
Eventually iBATIS DAO was deprecated, considering that better DAO frameworks were available, such as Spring Framework.
The Java DAO framework has been deprecated and removed from iBATIS as of release 2.3.0. We heartily recommend that you consider using the Spring Framework for all your DAO needs! Spring offers great support for iBATIS.

