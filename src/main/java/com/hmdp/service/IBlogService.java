package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IBlogService extends IService<Blog> {

    /**
     * 查询热门博客
     */
    Result queryHotBlog(Integer current);

    /**
     * 根据id查询博客
     */
    Result queryBlogById(Long id);

    /**
     * 点赞博客
     */
    Result likeBlog(Long id);

    /**
     * 查询点赞列表排行榜
     */
    Result queryBlogLikes(Long id);
}
