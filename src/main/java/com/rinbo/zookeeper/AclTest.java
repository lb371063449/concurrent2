package com.rinbo.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import java.io.IOException;

public class AclTest {
    @Test
    public void testDigest() throws IOException, KeeperException, InterruptedException {
        ZooKeeper zookeeper = new ZooKeeper("192.168.208.130:2181",2000,null);
        zookeeper.addAuthInfo("digest","foo:zk-books".getBytes());
        byte[] data = zookeeper.getData("/zk-book3", false, null);
        System.out.println(new String(data));
    }
}
