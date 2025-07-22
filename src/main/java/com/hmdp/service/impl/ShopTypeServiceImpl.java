package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查询商铺类型缓存
     */
    public Result queryTypeList() {
        //用String实现，opsForValue写法
        //public static final String CACHE_SHOP_TYPE_KEY = "cache:shopType:";
        String key = CACHE_SHOP_TYPE_KEY;
        //1.从redis查询商铺类型缓存
        String shopTypeJson = stringRedisTemplate.opsForValue().get(key);
        //2.判断是否存在
        if (StrUtil.isNotBlank(shopTypeJson)){
            //3.存在，直接返回
            List<ShopType> shopTypeList = JSONUtil.toList(shopTypeJson, ShopType.class);
            return Result.ok(shopTypeList);
        }
        //4.不存在，查询数据库  MybatisPlus的query()拿来用
        List<ShopType> shopTypeList = query().orderByAsc("sort").list();
        //5.数据库中不存在，返回错误信息
        if (shopTypeList == null){
            return Result.fail("商铺类型不存在!");
        }
        //6.数据库中存在，写入redis
        String jsonStr = JSONUtil.toJsonStr(shopTypeList);
        stringRedisTemplate.opsForValue().set(key, jsonStr);
        //7.返回
        return Result.ok(shopTypeList);
    }
}
