package com.tdt.boot;

import com.tdt.boot.entity.Student;
import com.tdt.boot.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
public class TestSpringCassandra {
    @Autowired
    private StudentService studentService;

    /**
     * 查询所有
     */
    @Test
    public void testQueryAll(){
        List<Student> students = studentService.queryAllStudent();
        for (Student student : students) {
            System.out.println(student);
            System.out.println("=============");
        }
    }

    @Test
    public void testOne(){
        Student student = studentService.queryOneStudent(1028);
        System.out.println(student);
    }

    @Test
    public void insert(){
        HashMap<String, String> education = new HashMap<>();
        education.put("小学", "中心第五小学");
        education.put("中学", "中心实验中学");
        HashSet<String> interest = new HashSet<>();
        interest.add("看书");
        interest.add("电影");
        List<String> phones = new ArrayList<>();
        phones.add("130-66666666");
        phones.add("15766666666");
//        构造student
        Student student = new Student(
                1028,
                "北京市朝阳区800号",
                30,
                education,
                "xiaoxiaoxian@14564e.com",
                0,
                interest,
                phones,
                "小小咸");
        studentService.saveStudent(student);
    }

    @Test
    public void delete(){
        studentService.deleteStudent(1028);
    }

}
