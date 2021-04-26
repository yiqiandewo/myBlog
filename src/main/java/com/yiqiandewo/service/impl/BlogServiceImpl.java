package com.yiqiandewo.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yiqiandewo.mapper.BlogMapper;
import com.yiqiandewo.pojo.Blog;
import com.yiqiandewo.service.BlogService;
import com.yiqiandewo.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private BlogMapper blogMapper;

    @Override
    public Blog selectOne(Long id) {
        //首先查缓存   k - v
        String key = "blog::" + id;
        Blog blog = blogMapper.selectOneById(id);
        blog.setViews(blog.getViews() + 1);
        blogMapper.updateViews(blog.getId());
        return blog;
    }

    @Override
    public Blog selectOne(String title) {
        return blogMapper.selectOneByTitle(title);
    }

    @Override
    public List<Blog> selectList(int size) {
        return blogMapper.selectListByUpdateTime(size);
    }

    @Override
    public PageInfo<Blog> selectList(int page, int size) {
        PageHelper.startPage(page, size);
        List<Blog> list = blogMapper.selectList();
        return new PageInfo<>(list);
    }

    public PageInfo<Blog> selectList(int page, int size, boolean published) {
        PageHelper.startPage(page, size);
        List<Blog> list = blogMapper.selectListPublished();
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<Blog> selectList(int page, int size, String query) {
        PageHelper.startPage(page, size);
        List<Blog> list = blogMapper.selectListConditional(query);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<Blog> selectList(int page, int size, String title, Long typeId, boolean recommend) {
        PageHelper.startPage(page, size);
        List<Blog> list = blogMapper.selectListMultipleConditional(title, typeId, recommend);
        return new PageInfo<>(list);
    }

    public Map<String, List<Blog>> selectMap() {
        Map<String, List<Blog>> map = new LinkedHashMap<>();  //改成LinkedHashMap  排序的hashmap
        //首先查询出所有的年份
        List<String> years = blogMapper.selectListYear();

        //然后封装一个 map<年份，blogs>
        for (String year : years) {
            List<Blog> list = blogMapper.selectListByYear(year);
            map.put(year, list);
        }

        return map;
    }

    @Override
    public Blog update(Long id, Blog blog) {
        Blog b = blogMapper.selectOneById(id);
        if (b == null) {
            return null;
        }
        blog.setUpdateTime(new Date());
        blogMapper.update(blog);
        return blog;
    }

    @Override
    public Blog insert(Blog blog) {
        Blog b = blogMapper.selectOneByTitle(blog.getTitle());
        if (b != null) {
            return null;
        }
        blog.setCreateTime(new Date());
        blog.setUpdateTime(new Date());
        blog.setViews(0);
        blogMapper.insert(blog);

        String key = "type_blogs";

        boolean exist = redisUtils.exists(key);
        if (exist) {
            redisUtils.zIncrby(key, blog.getType().getId());
        }
        return blog;
    }

    @Override
    public void delete(Long id) {
        blogMapper.delete(id);
    }


}
