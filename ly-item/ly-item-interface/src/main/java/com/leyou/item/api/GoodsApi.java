package com.leyou.item.api;

import com.leyou.common.dto.CartDto;
import com.leyou.common.page.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GoodsApi {

    /**
     * 分页查询Spu
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("spu/page")
    PageResult<Spu> querySpuByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                   @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                   @RequestParam(value = "saleable", required = false) Boolean saleable,
                                   @RequestParam(value = "key", required = false) String key);



    /**
     * 根据spu的id查询商品详情
     * @param id spu的id
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    SpuDetail querySpuDetailById(@PathVariable("id") Long id) ;

    /**
     * 根据spuid查询sku信息
     * @param id
     * @return
     */
    @GetMapping("sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("id") Long id) ;

    @GetMapping("/spu/{id}")
    Spu querySpuBySpuId(@RequestParam("id") Long id);

    /**
     * 库存操作接口
     * */
    @PostMapping("stock/decrease")
    public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDto> carts);
}
