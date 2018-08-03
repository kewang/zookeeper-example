package tw.kewang;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class Counter {
    private static final String PATH = "/caches";
    private static final String PATH_COUNT = "/count";
    private static final String CONNECT_STRING = "127.0.0.1";
    private static final String NAMESPACE = "test";

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(CONNECT_STRING)
                .sessionTimeoutMs(5000).connectionTimeoutMs(5000).retryPolicy(retryPolicy)
                .namespace(NAMESPACE).build();

        client.start();

        SharedCount count = new SharedCount(client, PATH + PATH_COUNT, 1);

        count.start();

        count.setCount(count.getCount() + 1);
    }
}