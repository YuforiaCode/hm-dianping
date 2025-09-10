package com.hmdp;

import com.hmdp.entity.Shop;
import com.hmdp.service.impl.ShopServiceImpl;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisIdWorker;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_KEY;
import static com.hmdp.utils.RedisConstants.SHOP_GEO_KEY;

@SpringBootTest
class HmDianPingApplicationTests {

    @Resource
    private CacheClient cacheClient;

    @Resource
    private ShopServiceImpl shopService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 测试RedisIdWorker，准备一个线程池，给500个线程
     */
    private ExecutorService es = Executors.newFixedThreadPool(500);

    /**
     * 测试一下并发情况下，生成id的性能以及生成id的情况
     */
    @Test
    void testIdWorker() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(300);
        Runnable task = () -> {  //任务：每个线程来了，都去生成100个id，并且打印一下
            for (int i = 0; i < 100; i++) {
                long id = redisIdWorker.nextId("order");
                System.out.println("id = " + id);
            }
            latch.countDown();
        };
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 300; i++) {  //将任务提交300次
            es.submit(task);
        }
        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("time = " + (end - begin));
    }

    @Test
    void testSaveShop() throws InterruptedException {
        Shop shop = shopService.getById(9L);

        cacheClient.setWithLogicalExpire(CACHE_SHOP_KEY + 9L, shop, 10L, TimeUnit.SECONDS);
    }

    /**
     * 导入店铺数据到GEO
     */
    @Test
    void loadShopData() {
        //1.查询店铺信息
        List<Shop> list = shopService.list();
        //2.把店铺分组，按照typeId分组，typeId一致的放到一个集合
        Map<Long, List<Shop>> map = list.stream()
                .collect(Collectors.groupingBy(Shop::getTypeId));
        //3.分批完成存储
        for (Map.Entry<Long, List<Shop>> entry : map.entrySet()) {
            //3.1.获取类型id
            Long typeId = entry.getKey();
            String key = SHOP_GEO_KEY + typeId;
            //3.2.获取同类型的店铺列表
            List<Shop> value = entry.getValue();
            List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>(value.size());
            //3.3.写入redis  geoadd key 经度 纬度 member
            for (Shop shop : value) {
                //stringRedisTemplate.opsForGeo().add(key, new Point(shop.getX(), shop.getY()), shop.getId().toString());
                locations.add(new RedisGeoCommands.GeoLocation<>(
                        shop.getId().toString(),
                        new Point(shop.getX(), shop.getY())
                ));
            }
            stringRedisTemplate.opsForGeo().add(key, locations);
        }
    }

    @Test
    void testHyperLogLog() {
        String[] values = new String[1000];
        int j = 0;
        for (int i = 0; i < 1000000; i++) {
            j = i % 1000;
            values[j] = "user_" + i;
            if (j == 999) {
                //发送到Redis
                stringRedisTemplate.opsForHyperLogLog().add("hl1", values);
            }
        }
        //统计数量
        Long count = stringRedisTemplate.opsForHyperLogLog().size("hl1");
        System.out.println("count = " + count);
    }
}
