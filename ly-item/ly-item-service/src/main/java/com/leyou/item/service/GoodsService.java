package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.dto.CartDto;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.page.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired BrandService brandService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public PageResult<Spu> queryGoodsDetail(int page, int rows, Boolean saleable, String key){

        //分页
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria =  example.createCriteria();
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        if(saleable !=null){
            criteria.andEqualTo("saleable",saleable);
        }

        //默认排序：商品更新时间
        example.setOrderByClause("last_update_time DESC");

        List<Spu> list =  spuMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //解析分类品牌名称
        loadCategoryAndBrandName(list);
        //解析分析结果
        PageInfo<Spu> pageInfo = new PageInfo <>(list);
        return  new PageResult <>(pageInfo.getTotal(),list);
    }

    public void loadCategoryAndBrandName(List<Spu> list){
        for (Spu spu : list) {
            //处理分类名称
            long cid1 = spu.getCid1();
            long cid2 = spu.getCid2();
            long cid3 = spu.getCid3();
            //流的调用：jdk1.8新特性
            List<String> names = categoryService.queryByIds(Arrays.asList(cid1,cid2,cid3))
                                    .stream().map(Category::getName).collect(Collectors.toList());
            //将结合拼装成字符串，并以“/”隔开
            spu.setCname(StringUtils.join(names,"/"));

            //处理品牌名称
            spu.setBname(brandService.queryById( spu.getBrandId()).getName());
        }
    }

    /**
     * 保存商品
     * @param spu
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveGoods(Spu spu) {
        System.out.println(spu);
        try {
            //新增spu
            spu.setId(null);
            spu.setCreateTime(new Date());
            spu.setLastUpdateTime(spu.getCreateTime());
            spu.setSaleable(true);
            spu.setValid(false);
            int count = spuMapper.insert(spu);
            if(count !=1){
                throw  new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }

            //新增spudetail
            SpuDetail spuDetail = spu.getSpuDetail();
            spuDetail.setSpu_id(spu.getId());
            spuDetail.setAfterService(spu.getSpuDetail().getSpecialSpec());
            spuDetail.setDescription(spu.getSpuDetail().getDescription());
            spuDetail.setPackingList(spu.getSpuDetail().getPackingList());
            spuDetail.setSpecialSpec(spu.getSpuDetail().getSpecialSpec());
            spuDetail.setGenericSpec(spu.getSpuDetail().getAfterService());
            spuDetailMapper.insert(spuDetail);

            //新增sku
            saveSkuAndStock(spu);

            //发送mq消息
            amqpTemplate.convertAndSend("item.insert",spu.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public SpuDetail querySpuDetailById(Long id) {

        SpuDetail spuDetail = this.spuDetailMapper.selectByPrimaryKey(id);
        if(spuDetail==null){
            throw  new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return spuDetail;
    }

    public Spu querySpuBySpuId(Long id){
        Spu spu = this.spuMapper.selectByPrimaryKey(id);
        if(spu ==null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return spu;
    }

    public List<Sku> querySkuBySpuId(Long spuId) {
        // 查询sku
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(record);
        if(CollectionUtils.isEmpty(skus)){
            throw  new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        // 同时查询出库存
        for (Sku sku : skus) {
            Stock stock = this.stockMapper.selectByPrimaryKey(sku.getId());
            if(stock == null){
                throw  new LyException(ExceptionEnum.GOODS_STOCK_NOT_FOUND);
            }
            sku.setStock(stock.getStock());
        }
        return skus;
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Spu spu) {
        try {
            // 查询以前sku
            List<Sku> skus = this.querySkuBySpuId(spu.getId());
            // 如果以前存在，则删除
            if(!CollectionUtils.isEmpty(skus)) {
                List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
                // 删除以前库存
                Example example = new Example(Stock.class);
                example.createCriteria().andIn("skuId", ids);
                this.stockMapper.deleteByExample(example);

                // 删除以前的sku
                Sku record = new Sku();
                record.setSpuId(spu.getId());
                this.skuMapper.delete(record);

            }
            // 新增sku和库存
            //saveSkuAndStock(spu.getSkus(), spu.getId());

            // 更新spu
            spu.setLastUpdateTime(new Date());
            spu.setCreateTime(null);
            spu.setValid(null);
            spu.setSaleable(null);
            int count = this.spuMapper.updateByPrimaryKeySelective(spu);
            if(count != 1){
                throw  new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
            }
            //修改detail  更新spu详情
            count = this.spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
            if(count != 1){
                throw  new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
            }

            saveSkuAndStock(spu);

            //发送mq消息
            amqpTemplate.convertAndSend("item.update",spu.getId());


            
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void saveSkuAndStock(Spu spu){
        int count;//定义库存集合
        List<Stock> stockList = new ArrayList <>();
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());

            count = skuMapper.insert(sku);
            if(count !=1){
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
           //新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());

            stockList.add(stock);
        }
        count = stockMapper.insertList(stockList);
        if(count != stockList.size()){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
    }

    /**
     * 减少库存
     * 在库存代码编写过程中需要解决商品超卖的问题，因为在高并发的情况下，多人进行减库存时会导致在某一时刻同时进行该操作逻辑
     * @param carts
     */
    @Transactional(readOnly = false,rollbackFor=Exception.class,propagation= Propagation.REQUIRED)
    public void decreaseStock(List<CartDto> carts) {
        for (CartDto cart : carts) {
            //直接在失去了中进行限制
            // update tb_stock set stock = stock-num where id = cart.getskuid and stock > 1
            int count = stockMapper.decreaseStock(cart.getSkudId(), cart.getNum());
            if(count != 1){
                throw new LyException(ExceptionEnum.CART_NOT_HAVE);
            }
        }
    }
}
