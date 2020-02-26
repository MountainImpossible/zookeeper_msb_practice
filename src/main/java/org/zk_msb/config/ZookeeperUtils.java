package org.zk_msb.config;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperUtils {

    private static ZooKeeper zooKeeper;

    public static CountDownLatch latch = new CountDownLatch(1);

    //zookeeper实例不能采用单例模式，每个连接一个实例
    public static ZooKeeper getZookeeper() throws IOException, InterruptedException {
        if(zooKeeper != null) {
            return zooKeeper;
        }
        zooKeeper = new ZooKeeper("192.168.40.141:2818,192.168.40.136:2818,192.168.40.137:2818,192.168.40.139:2181/userCenter",
                3000,
                new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        switch (watchedEvent.getState()) {
                            case Unknown:
                                break;
                            case Disconnected:
                                System.out.println("disconnect from zookeeper");
                                break;
                            case NoSyncConnected:
                                System.out.println("no synchronize connected");
                                break;
                            case SyncConnected:
                                latch.countDown();
                                System.out.println("synchronize connected");
                                break;
                            case AuthFailed:
                                System.out.println("authenticate failed");
                                break;
                            case ConnectedReadOnly:
                                System.out.println("connected in readonly mode");
                                break;
                            case SaslAuthenticated:
                                System.out.println("SaslAuthenticated...");
                                break;
                            case Expired:
                                System.out.println("expired, please try restart, shutting down...");
                                try {
                                    zooKeeper.close();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case Closed:
                                System.out.println("connection closed");
                                break;
                        }
                    }
                });
        //与zookeeper服务器建立连接之后才返回连接客户端
        latch.await();
        return zooKeeper;
    }

//    public static String fetchConfig(String key) {
//
//    }
}
