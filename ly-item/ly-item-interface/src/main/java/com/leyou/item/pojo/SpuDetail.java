package com.leyou.item.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="tb_spu_detail")

public class SpuDetail {

    /**对应的SPU的id*/
    @Id
    private Long spu_id;

    /** 商品描述*/
    @Column(name = "description")
    private String description;

    /** 商品特殊规格的名称及可选值模板*/

    @Column(name = "genericspec")
    private String genericSpec;

    /** 商品的全局规格属性*/
    @Column(name = "specialspec")
    private String specialSpec;

    /** 包装清单*/
    @Column(name = "packinglist")
    private String packingList;

    /** 售后服务*/
    @Column(name = "afterservice")
    private String afterService;

    public Long getSpu_id() {
        return spu_id;
    }

    public void setSpu_id(Long spu_id) {
        this.spu_id = spu_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenericSpec() {
        return genericSpec;
    }

    public void setGenericSpec(String genericSpec) {
        this.genericSpec = genericSpec;
    }

    public String getSpecialSpec() {
        return specialSpec;
    }

    public void setSpecialSpec(String specialSpec) {
        this.specialSpec = specialSpec;
    }

    public String getPackingList() {
        return packingList;
    }

    public void setPackingList(String packingList) {
        this.packingList = packingList;
    }

    public String getAfterService() {
        return afterService;
    }

    public void setAfterService(String afterService) {
        this.afterService = afterService;
    }
}