package org.zk_msb.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class WatcherCallback implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {

    private ZooKeeper zooKeeper;

    private boolean nodeChanged = false;

    @Override
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        System.out.println("DataCallback.processResult : fetch data from zookeeper->path:" + s);
        if(bytes != null) {
            CacheConfig.updateConfig(s, new String(bytes));
            nodeChanged = true;
        }
    }

    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        if(stat != null) {
            System.out.println("StatCallback.processResult->path:" + s);
            zooKeeper.getData(s, this, this, "context");
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case None:
                break;
            case NodeCreated:
            case NodeDataChanged:
                zooKeeper.getData(watchedEvent.getPath(), this, this, "userCenter");
                break;
            case NodeDeleted:
                CacheConfig.updateConfig(watchedEvent.getPath(), null);
                break;
//            case NodeChildrenChanged:
//                break;
//            case DataWatchRemoved:
//                break;
//            case ChildWatchRemoved:
//                break;
            default:
                System.out.println(watchedEvent.getType().toString());
                break;
        }
    }

//    public String fetch(String key) throws Exception {
//        if(zooKeeper == null) {
//            throw new Exception("zookeeper not set, cannot fetch configuration without zookeeper");
//        }
//        zooKeeper.getData(key, )
//    }

    public void waitForConfigCreated() {
        while(!nodeChanged) {}
        nodeChanged = false;
    }

    public WatcherCallback() {}

    public WatcherCallback(ZooKeeper zk) {
        this.zooKeeper = zk;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

}
