package org.example;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
public class App 
{
    static volatile boolean isConnected = false;
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        //watch:观察、回调
        //第一类：new zk时传入watch，session级别跟path、node没有关系
        try {
//            final CountDownLatch latch = new CountDownLatch(1);
            ZooKeeper zooKeeper = new ZooKeeper("192.168.40.141:2181,192.168.40.136:2181,192.168.40.139:2181,192.168.40.137:2181", 3000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    Event.KeeperState state = watchedEvent.getState();
                    switch (state) {
                        case Unknown:
                            break;
                        case Disconnected:
                            System.out.println("disconnected from server");
                            break;
                        case NoSyncConnected:
//                            latch.countDown();
                            isConnected = true;
                            System.out.println("no synchronize connected");
                            break;
                        case SyncConnected:
//                            latch.countDown();
                            isConnected = true;
                            System.out.println("synchronize connected");
                            break;
                        case AuthFailed:
                            System.out.println("authentication failed");
                            break;
                        case ConnectedReadOnly:
                            System.out.println("connected in readonly mode");
                            break;
                        case SaslAuthenticated:
                            System.out.println("SaslAuthenticated");
                            break;
                        case Expired:
                            System.out.println("expired");
                            break;
                        case Closed:
                            System.out.println("closed");
                            break;
                    }
                    System.out.println("Session level watcher invoked...");
                }
            });

//            latch.await();

            while (!isConnected) {}

            Stat stat = new Stat();
            byte[] data = zooKeeper.getData("/", false, stat);
            System.out.println(new String(data));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
