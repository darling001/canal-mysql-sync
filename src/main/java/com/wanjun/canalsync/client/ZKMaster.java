package com.wanjun.canalsync.client;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;

/**
 *
 * zookeeper HA
 * @author wangchengli
 * @version 1.0
 * @date 2018-02-01
 */
public class ZKMaster implements Watcher {

    private static final Logger LOG = LoggerFactory.getLogger(ZKMaster.class);
    enum MasterStates {RUNNING, ELECTED, NOTELECTED};
    private volatile MasterStates state = MasterStates.RUNNING;
    MasterStates getState() {
        return state;
    }

    private static final int SESSION_TIMEOUT = 5000;
    private static final String CONNECTION_STRING = "192.168.5.217:2181,192.168.5.218:2181,192.168.5.219:2181";
    private static final String ZNODE_NAME = "/master";
    private Random random = new Random(System.currentTimeMillis());
    private ZooKeeper zk;
    private String serverId = Integer.toHexString(random.nextInt());

    private volatile boolean connected = false;
    private volatile boolean expired = false;

    public void startZk() throws IOException {
        zk = new ZooKeeper(CONNECTION_STRING, SESSION_TIMEOUT, this);
    }

    public void stopZk() {
        if (zk != null) {
            try {
                zk.close();
            } catch (InterruptedException e) {
                LOG.warn("Interrupted while closing ZooKeeper session.", e);
            }
        }
    }


    /**
     * 抢注节点
     */
    public void enroll() {
        zk.create(ZNODE_NAME,
                serverId.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL,
                masterCreateCallBack, null);
    }

    AsyncCallback.StringCallback masterCreateCallBack = new AsyncCallback.StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    //网络问题，需要检查节点是否创建成功
                    checkMaster();
                    return;
                case OK:
                    state = MasterStates.ELECTED;
                    break;
                case NODEEXISTS:
                    state = MasterStates.NOTELECTED;
                    // 添加Watcher
                    addMasterWatcher();
                    break;
                default:
                    state = MasterStates.NOTELECTED;
                    LOG.error("Something went wrong when running for master.",
                            KeeperException.create(KeeperException.Code.get(rc), path));
            }
            LOG.info("I'm " + (state == MasterStates.ELECTED ? "" : "not ") + "the leader " + serverId);
        }
    };

    public void checkMaster() {
        zk.getData(ZNODE_NAME, false, masterCheckCallBack, null);
    }

    AsyncCallback.DataCallback masterCheckCallBack = new AsyncCallback.DataCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    checkMaster();
                    return;
                case NONODE:
                    // 节点未创建，再次注册
                    enroll();
                    return;
                case OK:
                    if (serverId.equals(new String(data))) {
                        state = MasterStates.ELECTED;
                    } else {
                        state = MasterStates.NOTELECTED;
                        addMasterWatcher();
                    }
                    break;
                default:
                    LOG.error("Error when reading data.",KeeperException.create(KeeperException.Code.get(rc), path));
            }
        }
    };

    void addMasterWatcher() {
        zk.exists(ZNODE_NAME,
                masterExistsWatcher,
                masterExistsCallback,
                null);
    }
    Watcher masterExistsWatcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            if(event.getType() == Event.EventType.NodeDeleted){
                assert ZNODE_NAME.equals(event.getPath());
                enroll();
            }
        }
    };
    AsyncCallback.StatCallback masterExistsCallback = new AsyncCallback.StatCallback() {
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    addMasterWatcher();
                    break;
                case OK:
                    break;
                case NONODE:
                    state = MasterStates.RUNNING;
                    enroll();
                    LOG.info("It sounds like the previous master is gone, " +
                            "so let's run for master again.");
                    break;
                default:
                    checkMaster();
                    break;
            }
        }
    };

    public static void main(String[] args) throws InterruptedException, IOException {
        ZKMaster m = new ZKMaster( );
        m.startZk();

        while (!m.isConnected()) {
            Thread.sleep(100);
        }
        m.enroll();
        while (!m.isExpired()) {
            Thread.sleep(1000);
        }

        m.stopZk();
    }


    boolean isConnected() {
        return connected;
    }

    boolean isExpired() {
        return expired;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        LOG.info("Processing event: " + watchedEvent.toString());
        if (watchedEvent.getType() == Event.EventType.None) {
            switch (watchedEvent.getState()) {
                case SyncConnected:
                    connected = true;
                    break;
                case Disconnected:
                    connected = false;
                    break;
                case Expired:
                    expired = true;
                    connected = false;
                    LOG.error("Session expiration");
                default:
                    break;
            }
        }
    }
}
