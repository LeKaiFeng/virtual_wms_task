package com.lee.database.std.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @TableName ga_boxlift
 */
@TableName(value = "ga_boxlift")
@EqualsAndHashCode(callSuper = false)
@Data
public class Boxlift implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private String type;

    /**
     *
     */
    private Integer groupId;

    /**
     *
     */
    private String ip;

    /**
     *
     */
    private Integer port;

    /**
     *
     */
    private Integer workmode;

    /**
     *
     */
    private String allowLevel;

    /**
     *
     */
    private Integer pos;

    /**
     *
     */
    private String boxNumber;

    /**
     *
     */
    private Integer targetLevel;

    /**
     *
     */
    private Integer state;

    /**
     *
     */
    private String liftArea;

    /**
     *
     */
    private String announceContent;

    /**
     *
     */
    private Integer useS7;

    /**
     *
     */
    private Integer rack;

    /**
     *
     */
    private Integer slot;

    /**
     *
     */
    private Integer boxliftStateDbNum;

    /**
     *
     */
    private Integer boxliftStateDbStart;

    /**
     *
     */
    private Integer barcodeLength;

    /**
     *
     */
    private Integer barcodeCount;

    /**
     *
     */
    private Integer deviceCount;

    /**
     *
     */
    private Integer boxliftId;

    /**
     *
     */
    private Integer requesetLevel;

    /**
     *
     */
    private Integer inLongError;

    /**
     *
     */
    private Integer outLongError;

    /**
     *
     */
    private Integer isMaster;

    /**
     *
     */
    private Integer requestLevel;

    /**
     *
     */
    private Integer virtualLevel;

    /**
     *
     */
    private String inToLiftEvent;

    /**
     *
     */
    private String inToLevelEvent;

    /**
     *
     */
    private String outToLiftEvent;

    /**
     *
     */
    private String outToLineEvent;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}