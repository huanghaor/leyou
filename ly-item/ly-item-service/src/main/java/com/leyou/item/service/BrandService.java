package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.page.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.mapper.BrandMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 商品品牌管理业务代码
 */
@Service
public class BrandService {

    @Autowired
    BrandMapper brandMapper;

    /**
     * 品牌分页及关键字查询
     * @param page 页码
     * @param rows 每页行数
     * @param sortBy 排序字段
     * @param desc 是否排序
     * @param key 搜索关键字
     * @return
     */
    public PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key){
        //开始分页
        PageHelper.startPage(page,rows);
        // 过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().andLike("name", "%" + key + "%")
                    .orEqualTo("letter", key.toUpperCase());
        }
        if (StringUtils.isNotBlank(sortBy)) {
            // 排序
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        // 查询
        List<Brand> list = brandMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            throw  new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        PageInfo<Brand> info = new PageInfo<>(list);
        // 返回结果
        return new PageResult<>(info.getTotal(), list);
    }


    /**
     * 保存新增品牌
     * @param brand 品牌信息
     * @param cids 类别id集合
     */
    public void saveBrand(Brand brand, List<Long> cids) {
        // 新增品牌信息
        int count = this.brandMapper.insertSelective(brand);
        if(count == 0){
            throw new LyException(ExceptionEnum.ADD_BRAND_FAIL);
        }
        // 新增品牌和分类中间表
        for (Long cid : cids) {
            count =  this.brandMapper.insertCategoryBrand(cid, brand.getId());
            if(count == 0){
                throw new LyException(ExceptionEnum.ADD_BRAND_FAIL);
            }
        }
    }

    public Brand queryById(Long id){
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if(brand == null){
            throw  new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return  brand;
    }

    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> listBrand = brandMapper.queryBrandByCid(cid);

        if(CollectionUtils.isEmpty(listBrand)){
            throw  new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return listBrand;
    }
}
