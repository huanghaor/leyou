package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "tb_sku")
@Data
public class Sku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long spuId;
    private String title;
    private String images;
    private Long price;
    /** 商品特殊规格的键值对*/
    private String ownSpec;
    /** 商品特殊规格的下标*/
    private String indexes;
    /** 是否有效，逻辑删除用*/
    private Boolean enable;
    /** 创建时间*/
    private Date createTime;
    /** 最后修改时间*/
    private Date lastUpdateTime;
    @Transient
    /** 库存*/
    private Long stock;
}