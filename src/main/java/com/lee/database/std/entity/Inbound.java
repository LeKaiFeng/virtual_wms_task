package com.lee.database.std.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2022-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ga_inbound")
public class Inbound implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer state;

    private String boxNumber;

    private String area;

    private String wmsid;

    private Integer level;

    private Integer pos;

    private Integer location;

    private String defaultArea;

    private Integer weight;

    private Integer liftTimeout;

    private Integer liftId;

    private String liftArea;

    private Integer boxType;

    private Integer width;

    @TableField("Container_status")
    private Integer containerStatus;


}
