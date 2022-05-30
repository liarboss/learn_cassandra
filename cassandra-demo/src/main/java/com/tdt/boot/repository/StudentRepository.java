package com.tdt.boot.repository;


import com.tdt.boot.entity.Student;
import org.springframework.data.cassandra.repository.CassandraRepository;


/**
 * 持久层
 */
public interface StudentRepository extends CassandraRepository<Student, Integer> {


}