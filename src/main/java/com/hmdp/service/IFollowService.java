package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IFollowService extends IService<Follow> {

    /**
     * 关注或取关
     */
    Result follow(Long followUserId, Boolean isFollow);

    /**
     * 查询是否关注
     */
    Result isFollow(Long followUserId);

    /**
     * 查询共同关注
     */
    Result followCommons(Long id);
}
