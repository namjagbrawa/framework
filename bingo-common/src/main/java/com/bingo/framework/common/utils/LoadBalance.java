package com.bingo.framework.common.utils;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalance {

    private static final Random random = new Random();

    public static URL doSelect(List<URL> urls) {
        if (urls == null || urls.size() == 0)
            return null;
        if (urls.size() == 1)
            return urls.get(0);
        int length = urls.size(); // 总个数
        int leastActive = -1; // 最小的活跃数
        int leastCount = 0; // 相同最小活跃数的个数
        int[] leastIndexs = new int[length]; // 相同最小活跃数的下标
        int totalWeight = 0; // 总权重
        int firstWeight = 0; // 第一个权重，用于于计算是否相同
        boolean sameWeight = true; // 是否所有权重相同
        for (int i = 0; i < length; i++) {
            URL url = urls.get(i);
            int active = LoadBalanceStatus.getStatus(url).getActive(); // 活跃数
            int weight = Constants.DEFAULT_WEIGHT;// 权重
            if (leastActive == -1 || active < leastActive) { // 发现更小的活跃数，重新开始
                leastActive = active; // 记录最小活跃数
                leastCount = 1; // 重新统计相同最小活跃数的个数
                leastIndexs[0] = i; // 重新记录最小活跃数下标
                totalWeight = weight; // 重新累计总权重
                firstWeight = weight; // 记录第一个权重
                sameWeight = true; // 还原权重相同标识
            } else if (active == leastActive) { // 累计相同最小的活跃数
                leastIndexs[leastCount++] = i; // 累计相同最小活跃数下标
                totalWeight += weight; // 累计总权重
                // 判断所有权重是否一样
                if (sameWeight && i > 0
                        && weight != firstWeight) {
                    sameWeight = false;
                }
            }
        }
        // assert(leastCount > 0)
        if (leastCount == 1) {
            // 如果只有一个最小则直接返回
            return urls.get(leastIndexs[0]);
        }
        if (!sameWeight && totalWeight > 0) {
            // 如果权重不相同且权重大于0则按总权重数随机
            int offsetWeight = random.nextInt(totalWeight);
            // 并确定随机值落在哪个片断上
            for (int i = 0; i < leastCount; i++) {
                int leastIndex = leastIndexs[i];
                offsetWeight -= getWeight();
                if (offsetWeight <= 0)
                    return urls.get(leastIndex);
            }
        }
        // 如果权重相同或权重为0则均等随机
        return urls.get(leastIndexs[random.nextInt(leastCount)]);
    }

    protected static int getWeight() {
        int weight = Constants.DEFAULT_WEIGHT;
        if (weight > 0) {
            long timestamp = 0L;
            if (timestamp > 0L) {
                int uptime = (int) (System.currentTimeMillis() - timestamp);
                int warmup = Constants.DEFAULT_WARMUP;
                if (uptime > 0 && uptime < warmup) {
                    weight = calculateWarmupWeight(uptime, warmup, weight);
                }
            }
        }
        return weight;
    }

    static int calculateWarmupWeight(int uptime, int warmup, int weight) {
        int ww = (int) ((float) uptime / ((float) warmup / (float) weight));
        return ww < 1 ? 1 : (ww > weight ? weight : ww);
    }

    public static void main(String[] args) {
        /*Set<String> str = new HashSet<>();
        str.add("a");
        str.add("a1");
        str.add("a3");
        str.add("a2");
        str.add("a4");
        str.add("a5");
        int a = 0, b = 0, c = 0, d = 0, e = 0, f = 0;
        ArrayList<String> strings = new ArrayList<>();
        strings.addAll(str);
        for (int i = 0; i < 10000000; i++) {
            String s = LoadBalance.doSelect(strings);
            // System.err.println(s);
            if (s.equals("a")) {
                a++;
            }
            if (s.equals("a1")) {
                b++;
            }
            if (s.equals("a2")) {
                c++;
            }
            if (s.equals("a3")) {
                d++;
            }
            if (s.equals("a4")) {
                e++;
            }
            if (s.equals("a5")) {
                f++;
            }
            if (i > 100) {
                strings.remove("a1");
                strings.remove("a2");
            }
        }
        System.err.println(a + " " + b + " " + c + " " + d + " " + e + " " + f);*/
    }
}

class LoadBalanceStatus {

    private static final ConcurrentMap<String, LoadBalanceStatus> URL_STATISTICS = new ConcurrentHashMap<String, LoadBalanceStatus>();

    private final AtomicInteger active = new AtomicInteger();

    public static LoadBalanceStatus getStatus(URL url) {
        String uri = url.toIdentityString();
        LoadBalanceStatus status = URL_STATISTICS.get(uri);
        if (status == null) {
            URL_STATISTICS.putIfAbsent(uri, new LoadBalanceStatus());
            status = URL_STATISTICS.get(uri);
        }
        return status;
    }

    public int getActive() {
        return active.get();
    }
}
