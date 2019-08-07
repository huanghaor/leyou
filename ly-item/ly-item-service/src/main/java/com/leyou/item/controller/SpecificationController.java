package com.leyou.item.controller;


import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.ParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 查询规格参数
 */
@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    @Autowired
    private ParamMapper paramMapper;

    /**
     * 根据分类id查询规格组
     * @param cid 分类id
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") int cid){
        return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
    }

    /**
     * 根据组id查询参数集合
     * @param gid 组id
     * @param cid 分类id
     * @param searching 是否搜索
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamList(@RequestParam(value = "gid",required = false) Integer  gid,
                                                          @RequestParam(value = "cid",required = false) Long cid,
                                                          @RequestParam(value = "searching",required = false) Boolean searching){
        //List<SpecParam> list= specificationService.queryParamList(gid,cid,searching);
        List<SpecParam> list =paramMapper.queryParamList(cid);
        if(CollectionUtils.isEmpty(list)){
            throw  new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
}
