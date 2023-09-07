package com.lee.database.mk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * @TableName ga_position
 */
@TableName(value = "ga_position")
@Data
public class Position implements Serializable {
    /**
     *
     */
    @TableId
    private Integer level;

    /**
     *
     */
    @TableId
    private Integer pos;

    /**
     *
     */
    private Integer junction;

    /**
     *
     */
    private Integer direction;

    /**
     *
     */
    private Integer distance;

    /**
     *
     */
    private Integer type;

    /**
     *
     */
    private Integer aisle;

    /**
     *
     */
    private Integer distanceAcross;

    /**
     *
     */
    private Integer directionAcross;

    /**
     *
     */
    private String liftArea;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}