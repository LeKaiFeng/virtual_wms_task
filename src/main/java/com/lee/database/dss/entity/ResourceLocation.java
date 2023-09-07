package com.lee.database.dss.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author lee
 * @since 2022-08-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ga_resource_location")
public class ResourceLocation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Integer level;

    private Integer location;

    private Integer pos;

    private Integer aisle;

    private Integer type;

    private String area;

    private String resourceId;

    private Integer state;

    private String barcode;

    private Integer boxType;

    private Integer weight;


}
