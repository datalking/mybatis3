# note-db

- CLOB & BLOB
    - CLOB使用CHAR来保存数据，如保存XML文档
    - BLOB就是使用二进制保存数据，如保存位图
    - BLOB和CLOB都是大字段类型，其实两个是可以互换的的，但是为了更好的管理数据库，通常像图片、文件、音乐等信息就用BLOB字段来存储，先将文件转为二进制再存储进去。而像文章或者是较长的文字，就用CLOB存储，这样对以后的查询更新存储等操作都提供很大的方便
    - MySQL中：clob对应text，blob对应blob  
    - DB2/Oracle中：clob对应clob，blob对应blob  

- oracle dual
    - Dual是Oracle中的一个实际存在的表，任何用户均可读取
    - DUAL是属于SYS schema的一个表,然后以PUBLIC SYNONYM的方式供其他数据库USER使用.
    - DUAL就是个一行一列的表，如果你往里执行insert、delete、truncate操作，就会导致很多程序出问题
    
