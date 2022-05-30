package com.tdt.boot;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Session;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class TestKeySpace {
    CqlSession cqlsession = null;
    Session session = null;

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
     * 查询 键空间
     */
    @Test
    public void findKeySpace() {
        List<KeyspaceMetadata> keyspaces = session.getCluster().getMetadata().getKeyspaces();
        for (KeyspaceMetadata keyspaceMetadata : keyspaces) {
            System.out.println(keyspaceMetadata.getName());
        }
    }

    /**
     * 创建键空间
     */
    @Test
    public void createKeySpace() {
        //1. 方法1
//        session.execute("CREATE KEYSPACE school2 WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};");
        //2. 方法2
        Map<String,Object> replicationMap = new HashMap<>();
        replicationMap.put("class","SimpleStrategy");
        replicationMap.put("replication_factor",3);
        SimpleStatement opts = SchemaBuilder.createKeyspace("school2")
                .ifNotExists()
                .withReplicationOptions(replicationMap)
                .build();
        session.execute(opts.getQuery());

    }

    /**
     * 删除键空间
     */
    @Test
    public void dropKeySpace() {
        SimpleStatement state = SchemaBuilder.dropKeyspace("school2").ifExists().build();
        session.execute(state.getQuery());

    }

}
