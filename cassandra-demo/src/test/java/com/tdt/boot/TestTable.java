package com.tdt.boot;


import com.datastax.driver.core.*;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.tdt.boot.entity.Student2;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;


public class TestTable {
    Session session = null;
    Mapper<Student2> mapper;

    /**
     * 初始化
     */
    @Before
    public void init() {
        //Cassandra服务器地址
        String hosts = "172.27.106.180";
        //端口
        int port = 9042;

        Cluster cluster = Cluster.builder().
                addContactPoint(hosts).
                withPort(port).
                build();
        session = cluster.connect();

    }

    /**
     * 创建表
     */
    @Test
    public void createTable() {

        Statement statement = SchemaBuilder.
                createTable("school", "student2").
                addPartitionKey("id", DataType.bigint()).
                addColumn("address", DataType.text()).
                addColumn("age", DataType.cint()).
                addColumn("education", DataType.map(DataType.text(), DataType.text())).
                addColumn("email", DataType.text()).
                addColumn("gender", DataType.cint()).
                addColumn("interest", DataType.set(DataType.text())).
                addColumn("phone", DataType.list(DataType.text())).
                addColumn("name", DataType.text()).
                ifNotExists();
        ResultSet resultSet = session.execute(statement);
        System.out.println(resultSet.getExecutionInfo());

    }

    /**
     * 修改表
     */
    @Test
    public void updateTable() {
//        添加字段
        SchemaBuilder.alterTable("school", "student2")
                .addColumn("email").type(DataType.text());
//        修改字段
        SchemaBuilder.alterTable("school", "student2")
                .alterColumn("email").type(DataType.set(DataType.text()));
//        删除字段
        SchemaBuilder.alterTable("school", "student2")
                .dropColumn("email");

    }

    /**
     * 删除表
     */
    @Test
    public void dropTable() {

        Statement statement = SchemaBuilder.dropTable("school", "student2").ifExists();
        session.execute(statement);
    }


    /**
     * 添加数据
     * 使用CQL
     */
    @Test
    public void insertByCQL() {

        String insertSql = "INSERT INTO school.student2 (id,address,age,gender,name,interest, phone,education) VALUES (1011,'中山路21号',16,1,'李小仙',{'游泳', '跑步'},['010-88888888','13888888888'],{'小学' : '城市第一小学', '中学' : '城市第一中学'}) ;";
        session.execute(insertSql);
    }

    /**
     * 添加数据
     * 使用Mapper和Bean对象
     */
    @Test
    public void insertByMapper() {
        mapper = new MappingManager(session).mapper(Student2.class);
        HashMap<String, String> education = new HashMap<>();
        education.put("小学", "中心第五小学");
        education.put("中学", "中心实验中学");
        HashSet<String> interest = new HashSet<>();
        interest.add("看书");
        interest.add("电影");
        List<String> phones = new ArrayList<>();
        phones.add("020-66666666");
        phones.add("13666666666");
//        构造student
        Student2 student2 = new Student2(
                1013L,
                "北京市朝阳区100号",
                20,
                education,
                "xiaoshuai@123.com",
                1,
                interest,
                phones,
                "马小帅");
//         数据保存到cassandra服务器
        mapper.save(student2);

    }

    /**
     * 查询所有数据
     */
    @Test
    public void queryAll() {
        mapper = new MappingManager(session).mapper(Student2.class);
        ResultSet resultSet = session.execute(select().all().from("school", "student2"));
        List<Student2> studentList = mapper.map(resultSet).all();
        for (Student2 student : studentList) {
            System.out.println(student);
        }

    }

    /**
     * 查询一条数据
     */
    @Test
    public void queryOne() {
        mapper = new MappingManager(session).mapper(Student2.class);
        ResultSet resultSet = session.execute(select().all().from("school", "student2"));
        Student2 student = mapper.map(resultSet).one();
        System.out.println(student);
    }


    /**
     * 根据id 查询
     */
    @Test
    public void queryById() {
        mapper = new MappingManager(session).mapper(Student2.class);
        ResultSet resultSet = session.execute(select().all().from("school", "student2").where(eq("id", 1012)));
        List<Student2> studentList = mapper.map(resultSet).all();
        for (Student2 student : studentList) {
            System.out.println(student);
        }
    }

    /**
     * 删除
     */
    @Test
    public void delete() {
        String sql = "DELETE FROM school.student2 where id = 1012;";
        session.execute(sql);
    }


    @Test
    public void batchPrepare() {
        //先把语句预编译
        BatchStatement batch = new BatchStatement();
        PreparedStatement ps = session.prepare("INSERT INTO school.student2 (id,address,age,gender,name,interest, phone,education) VALUES (?,?,?,?,?,?,?,?)");
        //循环10次，构造不同的student对象
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> education = new HashMap<>();
            education.put("小学", "中心第" + i + "小学");
            education.put("中学", "第" + i + "中学");
            HashSet<String> interest = new HashSet<>();
            interest.add("看书");
            interest.add("电影");
            List<String> phones = new ArrayList<>();
            phones.add("0" + i + "0-66666666");
            phones.add("1" + i + "666666666");
            Student2 student2 = new Student2();
            student2.setId(1013L + i);
            student2.setAddress("北京市朝阳区10" + i + "号");
            student2.setAge(21 + i);
            student2.setEducation(education);
            student2.setEmail("124275630@qq.com");
            student2.setGender(1);
            student2.setInterest(interest);
            student2.setName("学生" + i);
            student2.setPhone(phones);

            BoundStatement bs = ps.bind(student2.getId(),
                    student2.getAddress(),
                    student2.getAge(),
                    student2.getGender(),
                    student2.getName(),
                    student2.getInterest(),
                    student2.getPhone(),
                    student2.getEducation());
            batch.add(bs);
        }
        session.execute(batch);
        batch.clear();
    }
}
