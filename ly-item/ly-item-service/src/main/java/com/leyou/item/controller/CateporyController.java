package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品列表
 * @author hhr
 */
@RestController
@RequestMapping("/category")
public class CateporyController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点id查询商品分类
     * @param pid  节点id
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Category>> queryCateporyListByPid(@RequestParam("pid") Long pid){

        return ResponseEntity.ok(categoryService.queryCateporyListByPid(pid));
    }

    /**
     * 根据id查询商品分类
     * @param ids 商品分类id
     * @return
     */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(categoryService.queryByIds(ids));
    }

}
