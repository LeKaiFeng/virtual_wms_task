package com.lee.database.std.entity;

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
@TableName("ga_lift")
public class Lift implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String type;

    private String ip;

    private Integer port;

    private Integer pos;

    private Integer closerPos;

    private Integer dir;

    private Integer dis;

    private Integer liftLevel;

    private Integer liftTargetLevel;

    private Integer inLiftFlashNumber;

    private Integer liftState;

    private Integer isLiftConnected;

    private Integer liftTimeout;

    private String liftArea;

    private Integer isError;

    private String allowLevel;

    private String targetSide;

    private Integer inLiftFlashState;

    private Integer liftCpuState;

    private Integer liftRunState;

    private String defaultArea;

    private Integer useS7;

    private Integer rack;

    private Integer slot;

    private Integer boxliftStateDbNum;

    private Integer boxliftStateDbStart;

    private Integer barcodeLength;

    private Integer barcodeCount;

    private Integer deviceCount;

    private Integer aisle;

    private Integer reserveFlash;

    private String runEvent;

    private Integer mode;


}
