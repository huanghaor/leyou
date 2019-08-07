package com.leyou.item.mapper;

import com.leyou.item.pojo.SpecGroup;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpecGroupMapper extends Mapper<SpecGroup> {

    @Select("SELECT id,cid,name from tb_spec_group where cid = #{cid}")
    List<SpecGroup> querySpecGroupByCid(@Param("cid") int cid);


}
