package com.rinbo.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author libo
 */
public class ZookeeperTest {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zookeeper = null;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
//        zookeeper = new ZooKeeper("192.168.208.129:2181", 2000000,
        zookeeper = new ZooKeeper("192.168.56.101:2181", 2000000,
                (WatchedEvent event) -> {
                    if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                        connectedSemaphore.countDown();
                    } else if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                        try {
                            //Watcher通知是一次性的，即一旦触发一次通知后，该Watcher就失效了,所以在process里面又注册了Watcher
                            System.out.println("ReGet Child:" + zookeeper.getChildren(event.getPath(), true));
                        } catch (Exception e) {
                        }
                    }
                });
        try {
            connectedSemaphore.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        create(zookeeper);
        getData(zookeeper);
//        Thread.sleep(Integer.MAX_VALUE);
    }

    //新增节点
    public static void create(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
        //创建临时节点
        String path1 = zooKeeper.create("/FileTxnLog3", "hello FileTxnLog3".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
//        System.out.println("Success create znode: " + path1);
        //创建了临时顺序节点，在创建顺序节点时，系统会在后面自动增加一串数字
//        string path2 = zooKeeper.create("/jztest2", "word".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
//        System.out.println("Success create znode: " + path2);
        //异步操作
//        zooKeeper.create("/jztest3", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
//                (int rc, string path, Object ctx, string name) -> {
//                    System.out.println("Create path result: [" + rc + ", " + path + ", " + ctx + ", real path name: " + name);
//                }, "I am context. ");
    }

    //删除节点，只允许删除叶子节点，即一个节点如果有子节点，那么该节点将无法直接删除，必须先删掉其所有子节点
    public static void delete(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
        zooKeeper.delete("/jztest", -1);
        //异步删除
        zooKeeper.delete("/jztest2", -1,
                (int rc, String path, Object ctx) -> {
                    System.out.println(rc + ", " + path + ", " + ctx);
                }, null);
    }

    public static void getData(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData("/tq2", true, null);
        System.out.println(new String(data));
    }

    //读取节点的子节点列表
    public static void getChildren(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
        //获取子节点
        List<String> childrens = zooKeeper.getChildren("/", true);
        //更新值,setData中的version参数设置-1含义为客户端需要基于数据的最新版本进行更新操作。
        zooKeeper.setData("/zktest", "rrrr".getBytes(), -1);
        for (String ch : childrens) {
            System.out.println(ch);
        }
        //异步获取值
        zooKeeper.getData("/zktest", true,
                (int rc, String path, Object ctx, byte[] data, Stat stat) -> {
                    System.out.println("rc: " + rc + ", path: " + path + ", data: " + new String(data));
                    System.out.println("czxID: " + stat.getCzxid() + ", mzxID: " + stat.getMzxid() + ", version: " + stat.getVersion());
                }, null);
    }

    //setData方法存在一个version参数，其用于指定节点的数据版本，表明本次更新操作是针对指定的数据版本进行的，
    // 但是，在getData方法中，并没有提供根据指定数据版本来获取数据的接口，那么，这里为何要指定数据更新版本呢，
    // 这里方便理解，可以等效于CAS（compare and swap），对于值V，每次更新之前都会比较其值是否是预期值A，只有符合预期，
    // 才会将V原子化地更新到新值B。Zookeeper的setData接口中的version参数可以对应预期值，
    // 表明是针对哪个数据版本进行更新，假如一个客户端试图进行更新操作，它会携带上次获取到的version值进行更新，
    // 而如果这段时间内， Zookeeper服务器上该节点的数据已经被其他客户端更新，那么其数据版本也会相应更新，
    // 而客户端携带的version将无法匹配，无法更新成功
    public static void update(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
        //更新值,setData中的version参数设置-1含义为客户端需要基于数据的最新版本进行更新操作。
        Stat stat = zooKeeper.setData("/zktest", "rrrr".getBytes(), 6);
        System.out.println(stat.getVersion());
        zooKeeper.setData("/zktest", "456".getBytes(), -1,
                (int rc, String path, Object ctx, Stat xx) -> {
                    System.out.println("rc: " + rc + ", path: " + path + ", stat: " + xx);
                }, null);
    }

    //判断是否存在
    public static void isExists(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
        Stat exists = zooKeeper.exists("/zktest", true);
        System.out.println(exists.getVersion());
        zooKeeper.exists("/zktest", true,
                (int rc, String path, Object ctx, Stat stat) -> {
                    System.out.println("rc: " + rc + ", path: " + path + ", stat: " + stat);
                }, null);
    }

    //权限管理
    public static void authInfoManager(ZooKeeper zooKeeper) throws KeeperException, InterruptedException, IOException {
        zooKeeper.addAuthInfo("digest", "foo:true".getBytes());
        zooKeeper.create("/zktest2", "init".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
        ZooKeeper zookeeper2 = new ZooKeeper("192.168.208.129:2181", 5000, null);
        //如果zookeeper2调用addAuthInfo()，而执行getData()，会抛出没有权限异常
        zookeeper2.addAuthInfo("digest", "foo:true".getBytes());
        zookeeper2.getData("/zktest2", false, null);
    }
}
