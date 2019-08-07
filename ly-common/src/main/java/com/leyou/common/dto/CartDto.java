package com.leyou.common.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {

    /**商品skuid*/
    private Long SkudId;
    /**购买数量*/
    private Integer num;
}
