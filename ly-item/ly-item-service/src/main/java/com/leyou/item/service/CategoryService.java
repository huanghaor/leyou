package com.leyou.item.service;


import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.Category;
import com.leyou.item.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCateporyListByPid(Long pid) {
        /** 查询条件：mapper会把对象中的非空属性作为查询条件*/
        Category t = new Category();
        t.setParentId(pid);
       List<Category> list = categoryMapper.select(t);
       //判断查询结果
       if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.CATEGORY_QUERY_NOT_FOUND);
       }
        return list;
    }

    /**
     * 根据id查询商品分类名称
     * @param cid
     * @return
     */
    public List<Category> queryByIds(List<Long> cid){
        List<Category> list = categoryMapper.selectByIdList(cid);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.CATEGORY_QUERY_NOT_FOUND);
        }
        return list;
    }
}
