package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 异常枚举类型定义:
 * 枚举里面的构造函数默认为私有
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
    /**商品价格*/
    PRICE_CANNOT_BE_NULL(400,"价格不能为空"),
    CATEGORY_QUERY_NOT_FOUND(404,"商品分类没有查询到"),
    SPEC_GROUP_NOT_FOUND(404,"商品规格组没查询到"),
    SPEC_PARAM_NOT_FOUND(404,"商品参数没查询到"),
    BRAND_NOT_FOUND(404,"品牌不存在"),
    GOODS_NOT_FOUND(404,"商品不存在"),
    GOODS_SKU_NOT_FOUND(404,"商品SKU不存在"),
    GOODS_STOCK_NOT_FOUND(404,"商品库存不存在"),
    ADD_BRAND_FAIL(500,"新增品牌失败"),
    UPLOAD_FAIL_ERROR(500,"文件上传失败"),
    INVLID_FILE_TYPE(400,"无效文件类型"),
    GOODS_SAVE_ERROR(500,"新增商品失败"),
    GOODS_UPDATE_ERROR(500,"修改商品失败"),
    INVLID_USER_DATA_TYPE(400,"数据类型不存在"),
    INVLID_VERIFY_CODE(400,"无效验证码"),
    USER_NOT_FOUND(400,"用户不存在"),
    PASSWORD_ERROR(400,"密码不正确"),
    CREATE_TOKEN_ERROR(500,"用户凭证生成失败"),
    UN_AUTHORIZED(403,"未授权"),
    CART_NOT_HAVE(400,"库存不足"),
    ;
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
