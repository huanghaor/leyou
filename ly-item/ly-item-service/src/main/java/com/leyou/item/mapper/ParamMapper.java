package com.leyou.item.mapper;

import com.leyou.item.pojo.SpecParam;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ParamMapper extends Mapper<SpecParam> {

    @Select("SELECT * from tb_spec_param where cid = #{cid}")
    List<SpecParam> queryParamList(@Param("cid") Long cid);


}
