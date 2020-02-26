package org.zk_msb.config;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConfig {

    ZooKeeper zk;

    @Before
    public void connect() throws Exception {
        zk = ZookeeperUtils.getZookeeper();
    }

    @After
    public void close() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getConf() throws Exception {
        String conf = CacheConfig.getConfig("/service1");
        System.out.println(conf);
    }

}
