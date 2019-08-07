package com.leyou.item.controller;

import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌管理controller
 */
@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 品牌数据分页条件查询
     * @param page 页码
     * @param rows  每页条数
     * @param sortBy 排序字段
     * @param desc 是否排序
     * @param key 搜索关键字
     * @return 分页数据
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key) {
        PageResult<Brand> result = this.brandService.queryBrandByPageAndSort(page,rows,sortBy,desc, key);
        if (result == null || result.getItems().size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 保存新增品牌信息
     * @param brand 品牌基本信息
     * @param cids 分类类别id
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand , @RequestParam(value = "cids") List<Long> cids){
        brandService.saveBrand(brand,cids);
        //有返回体写body(),无返回体则写“build()”
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 根据商品分类id查询品牌名称
     * @param cid 商品分类id
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCId(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(brandService.queryBrandByCid(cid));
    }

    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id){
        return  ResponseEntity.ok(brandService.queryById(id));
    }
}
