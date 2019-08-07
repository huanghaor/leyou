package com.leyou.search.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClent;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClent goodsClent;

    @Autowired
    private SpecificationClient specificationClient;


    /**创建索引库*/
    public Goods buildGoods(Spu spu){

        //查询分类
        List <Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_QUERY_NOT_FOUND);
        }
        List <String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if(brand==null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        //查询sku
        List <Sku> skuList = goodsClent.querySkuBySpuId(spu.getId());
        if(CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        //对sku数据进行处理
        List<Map<String,Object>> skus = new ArrayList <>();
        //价格集合
        Set <Long> priceList = new HashSet<>();
        for (Sku sku : skuList) {
            Map<String,Object> map = new HashMap <>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("image", StringUtils.substringBefore(",",sku.getImages()));
            skus.add(map);
            //处理价格
            priceList.add(sku.getPrice());
        }
        //得到价格
        //Set <Long> priceList = skuList.stream().map(Sku::getPrice).collect(Collectors.toSet());

        //查询规格参数
        List <SpecParam> specParam = specificationClient.queryParamList(null, spu.getCid3(), true);
        if(CollectionUtils.isEmpty(specParam)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        //查询商品详情
        SpuDetail spuDetail = goodsClent.querySpuDetailById(spu.getId());
        //查询通用规格参数
        Map <String, String> genericSpec = JsonUtils.parseMap(spuDetail.getGenericSpec(), String.class, String.class);
        //查询特有规格参数
        Map <String, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference <Map <String, List<String>>>() {
        });
        //规格参数 key:规则参数名字，value:规则参数的值
        Map<String,Object> specs = new HashMap <>();
        for (SpecParam param : specParam) {
           String key =  param.getName();
           Object value="";
           //判断是否为通用规格属性
            if(param.getGeneric()){
                value = genericSpec.get(param.getId());
                //判断是否为数字类型
                if(param.getNumeric()){
                    //处理成段

                }
            }else{
                value=specialSpec.get(param.getId());
            }

        }

        String all = spu.getTitle()+ StringUtils.join(names,"")+brand.getName();

        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid3(spu.getCid2());
        goods.setCid2(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        // 搜索字段  包含标题，分类，品牌，规格等
        goods.setAll(all);
        // spu的价格集合
        goods.setPrice(priceList);
        //所有sku的json字符串
        goods.setSkus(JsonUtils.serialize(skus));
        //所有可搜索的规格参数
        goods.setSpecs(null);
        goods.setSubTitle(spu.getSubTitle());
        return goods;
    }

    /**
     * 消息队列对索引库进行修改
     * @param spuId
     */
    public void creatOrUpdateIndex(Long spuId) {
        //查询spu
        Spu spu = goodsClent.querySpuBySpuId(spuId);
        //构建goods
        Goods goods = buildGoods(spu);
        //存入索引库
        //repository.save(goods);

    }

    public void deleteIndex(Long spuId) {
        //repository.deleteById(goods);
    }
}
