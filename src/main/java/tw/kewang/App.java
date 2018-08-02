package tw.kewang;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class App {
    private static final String PATH = "/caches";
    private static final String CONNECT_STRING = "127.0.0.1";
    private static final String NAMESPACE = "test";

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(CONNECT_STRING)
                .sessionTimeoutMs(5000).connectionTimeoutMs(5000).retryPolicy(retryPolicy)
                .namespace(NAMESPACE).build();

        client.start();

        createPathChildrenCacheListener(client);
        createNodeCacheListener(client);
        createTreeCacheListener(client);

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void createTreeCacheListener(CuratorFramework client) throws Exception {
        TreeCache cache = new TreeCache(client, PATH);

        cache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent event) throws Exception {
                System.out.println("=== TreeCacheListener ===");

                System.out.println("type: " + event.getType());
                System.out.println("path: " + (null != event.getData() ? event.getData().getPath() : null));

                System.out.println();
            }
        });

        cache.start();
    }

    private static void createNodeCacheListener(CuratorFramework client) throws Exception {
        final NodeCache cache = new NodeCache(client, PATH);

        cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("=== NodeCacheListener ===");

                ChildData data = cache.getCurrentData();

                if (null != data) {
                    System.out.println("node data: " + new String(cache.getCurrentData().getData()));
                } else {
                    System.out.println("node was removed");
                }

                System.out.println();
            }
        });

        cache.start();
    }

    private static void createPathChildrenCacheListener(CuratorFramework client) throws Exception {
        PathChildrenCache cache = new PathChildrenCache(client, PATH, true);

        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                System.out.println("=== PathChildrenCacheListener ===");

                System.out.println("type: " + event.getType());

                if (null != event.getData()) {
                    System.out.println("node data: " + event.getData().getPath() + " = " + new String(event.getData().getData()));
                }

                System.out.println();
            }
        });

        cache.start();
    }
}