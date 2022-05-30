package com.tdt.boot.conf;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CassandraConfig extends AbstractCassandraConfiguration {


    @Autowired
    private CassandraProperties properties;


    //空间名称
    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspaceName;

    //节点IP（连接的集群节点IP）
    @Value("${spring.data.cassandra.contact-points}")
    private String contactPoints;

    @Value("${spring.data.cassandra.username}")
    private String username;

    @Value("${spring.data.cassandra.password}")
    private String password;

//    @Value("${spring.data.cassandra.session-name}")
//    private String sessionName;

    public String getKeyspaceName() {
        return keyspaceName;
    }

    public String getContactPoints() {
        return contactPoints;
    }

//    @Override
//    public String getSessionName() {
//        return sessionName;
//    }

//    @Override
//    public String getLocalDataCenter() {
//        return "datacenter1";
//    }

    @Override
    public CqlSessionFactoryBean cassandraSession() {
        CqlSessionFactoryBean cqlSessionFactoryBean = super.cassandraSession();
        cqlSessionFactoryBean.setPassword(password);
        cqlSessionFactoryBean.setUsername(username);
        return cqlSessionFactoryBean;
    }



}
