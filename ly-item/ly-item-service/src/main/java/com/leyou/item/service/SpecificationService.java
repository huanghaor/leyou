package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.ParamMapper;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private ParamMapper paramMapper;

    public List<SpecGroup> queryGroupByCid(int cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> list = specGroupMapper.querySpecGroupByCid(cid);
        if(CollectionUtils.isEmpty(list)){
            throw  new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    public List<SpecParam> queryParamList(int gid,long cid,Boolean searching) {

        //SpecParam  param = new SpecParam();
        //param.setGroup_id(gid);
        //param.setCid(cid);
        //param.setSearching(searching);
        List<SpecParam> list =paramMapper.queryParamList(cid);
        if(CollectionUtils.isEmpty(list)){
            throw  new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }
}
