package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_spec_param")
@Data
public class SpecParam {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**商品分类id*/
    private int cid;

    private int group_id;

    /**参数名*/
    private String name;

    /**是否为数字类型==true\false*/
    private Boolean numeric;

    /**数字类型的单位，非数字类型可为空*/
    private String until;

    /**是否是sku通用属性 true或者false*/
    private Boolean generic;

    /**是否用于搜索过滤  true或者false*/
    private Boolean searching;

    /**数值类型参数，如需要搜索*/
    private String segments;
}
