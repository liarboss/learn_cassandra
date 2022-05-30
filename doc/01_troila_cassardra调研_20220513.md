## thingsboard存储调研


1. 查看集群信息
cqlsh> Describe cluster;

2. 查看keyspaces信息
cqlsh> Describe Keyspaces;

3. 列出键空间的所有表
cqlsh> Describe tables;

4. 列出system_traces 下的 sessions信息
cqlsh> USE system_traces;
cqlsh:system_traces> DESCRIBE sessions;

5. 输入命令，将输出内容捕获到名为outputfile的文件
CAPTURE '/opt/outputfile'

6. 显示当前cqlsh 连接的Cassandra服务的ip和端口
cqlsh:system_traces> SHOW HOST

7. SHOW VERSION 显示当前的版本
cqlsh:system_traces> SHOW VERSION

8. 出入SHOW SESSION 显示会话信息，需要参数uuid
cqlsh:system_traces> SHOW SESSION <uuid>

9. 编写完成的创建语句 创建一个键空间名字为：school，副本策略选择：简单策略 SimpleStrategy，副本因子：3
CREATE KEYSPACE school WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};

10. 输入 DESCRIBE school 查看键空间的创建语句
DESCRIBE school;

11. 连接school 键空间
use school;

12. 修改school键空间，把副本引子 从3 改为1
ALTER KEYSPACE school WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};

13. 删除school键空间
DROP KEYSPACE school

14. 操作前，先把键空间school键空间创建，并使用school 键空间
CREATE KEYSPACE school WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};
use school;

15. 查看键空间下所有表
DESCRIBE TABLES;

16. 完整创建表语句，创建student 表，student包含属性如下： 学生编号（id）， 姓名（name），年龄（age），性别（gender），家庭地址（address）
，interest（兴趣），phone（电话号码），education（教育经历） id 为主键，并且为每个Column选择对应的数据类型。 注意：interest 的数据类型是set
，phone的数据类型是list，education 的数据类型是map
CREATE TABLE student(
   id int PRIMARY KEY,
   name text,
   age int,
   gender int, 
   email text,
   address text ,
   interest set<text>,
   phone list<text>,
   education map<text, text>
);

17. 查看创建的表
cqlsh:school> DESCRIBE TABLE student;


18. 在 Single column Primary Key 决定这一条记录放在哪个节点。
create table testTab (
id int PRIMARY KEY,
name text
);

19. 如果 Primary Key 由多列组成，那么这种情况称为 Compound Primary Key 或 Composite Primary Key。
create table testTab (
key_one int,
key_two int,
name text,
PRIMARY KEY(key_one, key_two)
);

20. 如果 Partition key 由多个字段组成，称之为 Composite Partition key
create table testTab (
key_part_one int,
key_part_two int,
key_clust_one int,
key_clust_two int,
key_clust_three uuid,
name text,
PRIMARY KEY((key_part_one,key_part_two), key_clust_one, key_clust_two, key_clust_three)
);

21. 给student添加一个列email代码：
ALTER TABLE student ADD email text;

22. 删除student的email列
cqlsh:school> ALTER table student DROP column email;

23. 删除student表
DROP TABLE student;

24. 清空表
TRUNCATE student;

25. 为student的 name 添加索引，索引的名字为：sname
CREATE INDEX sname ON student (name);

26. 给集合列设置索引
CREATE INDEX ON student(interest);                 -- set集合添加索引
CREATE INDEX mymap ON student(KEYS(education));          -- map结合添加索引

27. 删除student的sname 索引
drop index sname;

28. 当前student表有2行数据，全部查询出来
cqlsh:school> select * from student;

29. 根据主键查询
cqlsh:school> select * from student where student_id=1012;

/**
create table testTab (
key_one int,
key_two int,
name text,
age  int,
PRIMARY KEY(key_one, key_two)
);
create INDEX tage ON testTab (age);
**/

30. key_one列是第一主键 对key_one进行 = 号查询，可以查出结果
select * from testtab where key_one=4;

31. 第二主键 支持 = 、>、 <、 >= 、 <=
加上ALLOW FILTERING 后确实可以查询出数据，但是不建议这么做
select * from testtab where key_two = 8 ALLOW FILTERING;

32. 正确的做法是 ，在查询第二主键时，前面先写上第一主键
select * from testtab where key_one=12 and key_two = 8;
select * from testtab where key_one=12 and key_two > 7;

33. 索引列 只支持=号
select * from testtab where age = 19;   -- 正确
select * from testtab where age > 20 ;  --会报错
select * from testtab where age >20 allow filtering;  --可以查询出结果，但是不建议这么做

34. 普通列，非索引非主键字段
select * from testtab where key_one=12 and name='张小仙'; --报错
select * from testtab where key_one=12 and name='张小仙' allow filtering;  --可以查询

35. 使用student表来测试集合列上的索引使用。
假设已经给集合添加了索引，就可以使用where子句的CONTAINS条件按照给定的值进行过滤。
select * from student where interest CONTAINS '电影';        -- 查询set集合
select * from student where education CONTAINS key  '小学';  --查询map集合的key值
select * from student where education CONTAINS '中心第9小学' allow filtering; --查询map的value值

36. cassandra的任何查询，最后的结果都是有序的，内部就是这样存储的。
select * from testtab where key_one = 12 order by key_two;  --正确
select * from testtab where key_one = 12 and age =19 order key_two;  --错误，不能有索引查询

37. 给student添加2行数据，包含对set，list ，map类型数据
INSERT INTO student (id,address,age,gender,name,interest, phone,education) VALUES (1011,'中山路21号',16,1,'Tom',{'游泳', '跑步'},['010-88888888','13888888888'],{'小学' : '城市第一小学', '中学' : '城市第一中学'}) ;
INSERT INTO student (id,address,age,gender,name,interest, phone,education) VALUES (1012,'朝阳路19号',17,2,'Jerry',{'看书', '电影'},['020-66666666','13666666666'],{'小学' :'城市第五小学','中学':'城市第五中学'});

38. 把student_id = 1012 的数据的gender列 的值改为1
UPDATE student set gender = 1 where student_id= 1012;

39. 更新set类型数据
添加一个元素
UPDATE student SET interest = interest + {'游戏'} WHERE student_id = 1012;
删除一个元素
UPDATE student SET interest = interest - {'电影'} WHERE student_id = 1012;
删除所有元素
UPDATE student SET interest = {} WHERE student_id = 1012;
或
DELETE interest FROM student WHERE student_id = 1012;

40. 更新list类型数据
使用UPDATA命令向list插入值
UPDATE student SET phone = ['020-66666666', '13666666666'] WHERE student_id = 1012;
在list前面插入值
UPDATE student SET phone = [ '030-55555555' ] + phone WHERE student_id = 1012;
在list后面插入值
UPDATE student SET phone = phone + [ '040-33333333' ]  WHERE student_id = 1012;
使用列表索引设置值，覆盖已经存在的值
UPDATE student SET phone[2] = '050-22222222' WHERE student_id = 1012;
【不推荐】使用DELETE命令和索引删除某个特定位置的值
DELETE phone[2] FROM student WHERE student_id = 1012;
【推荐】使用UPDATE命令和‘-’移除list中所有的特定值
UPDATE student SET phone = phone - ['020-66666666'] WHERE student_id = 1012;

41. 更新map类型数据
使用Insert或Update命令
UPDATE student SET education={'中学': '城市第五中学', '小学': '城市第五小学'} WHERE student_id = 1012;
使用UPDATE命令设置指定元素的value
UPDATE student SET education['中学'] = '爱民中学' WHERE student_id = 1012;
可以使用如下语法增加map元素。如果key已存在，value会被覆盖，不存在则插入
UPDATE student SET education = education + { '幼儿园' : '大海幼儿园', '中学': '科技路中学'} WHERE student_id = 1012;
使用DELETE删除数据
DELETE education['幼儿园'] FROM student WHERE student_id = 1012;
使用UPDATE删除数据
UPDATE student SET education=education - {'中学','小学'} WHERE student_id = 1012;

42. 删除student中student_id=1012 的数据
DELETE FROM student WHERE student_id=1012;

43. 批量操作
在批量操作中实现 3个操作：
新增一行数据，student_id =1015
更新student_id =1012的数据，把年龄改为11，
删除已经存在的student_id=1011的数据
BEGIN BATCH
    INSERT INTO student (id,address,age,gender,name) VALUES (1015,'上海路',20,1,'Jack') ;
    UPDATE student set age = 11 where id= 1012;
    DELETE FROM student WHERE id=1011;
APPLY BATCH;






