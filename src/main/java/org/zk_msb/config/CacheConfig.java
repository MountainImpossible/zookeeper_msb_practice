package org.zk_msb.config;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class CacheConfig {
    private final static ConcurrentHashMap<String, String> container = new ConcurrentHashMap<>();

    private final static Set removed = new TreeSet();

    private static ZooKeeper zooKeeper;

    public static String getConfig(String key) throws IOException, InterruptedException {
        if(container.containsKey(key)) {
            return container.get(key);
        }
        if(removed.contains(key)) {
            throw new RuntimeException("configuration removed, please wait for creation");
        }
        if(zooKeeper == null) {
            zooKeeper = ZookeeperUtils.getZookeeper();
        }
        WatcherCallback watcherCallback = new WatcherCallback(zooKeeper);
        zooKeeper.getData(key, watcherCallback, watcherCallback, "userCenter");
        System.out.println("request get data sent...");
        watcherCallback.waitForConfigCreated();
        return container.get(key);
    }

    public static void updateConfig(String key, String value) {
        if(value == null || "".equals(value)) {
            container.remove(key);
            removed.add(key);
            System.out.println("delete cached configuration, key: " + key);
        } else {
            container.put(key, value);
        }
        System.out.println("Configuration changed, updating cache. New value[" + value + "]");
    }

}
