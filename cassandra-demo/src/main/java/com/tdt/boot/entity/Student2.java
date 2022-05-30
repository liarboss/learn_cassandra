package com.tdt.boot.entity;

import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;

import java.math.BigInteger;
import java.util.*;

@Data
@Table(name = "student2" ,keyspace = "school")
@AllArgsConstructor
@NoArgsConstructor
public class Student2 {

    @PrimaryKey
    private Long id;
    private  String address;
    private  Integer age;
    private Map<String,String> education;
    private  String email ;
    private  Integer gender;
    private Set<String> interest;
    private List<String> phone ;
    private  String name;


}
