package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 品牌管理
 */
public interface BrandMapper extends Mapper<Brand>{

    /**
     * 新增商品分类和品牌中间表数据
     * @param cid 商品分类id
     * @param bid 品牌id
     * @return 返回保存结果
     */
    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    int  insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);


    /**
     * 根据商品分类id查询品牌名称
     * @param cid
     * @return
     */
    @Select("SELECT b.id,b.name FROM tb_brand b INNER JOIN tb_category_brand cb ON b.id=cb.brand_id where cb.category_id = #{cid}")
    List<Brand>  queryBrandByCid(@Param("cid") Long cid);
}
