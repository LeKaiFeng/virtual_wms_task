package com.lee.database.std.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@TableName("ga_task")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer type;

    private Integer sLevel;

    private Integer sPos;

    private Integer sLocation;

    private Integer eLevel;

    private Integer ePos;

    private Integer eLocation;

    private Integer rLevel;

    private Integer rPos;

    private Integer rLocation;

    private Integer state;

    private String boxNumber;

    private Integer weight;

    private Integer priority;

    private Integer carNumber;

    private String aisle;

    private String area;

    private String wmsid;

    private LocalDateTime createTime;

    private LocalDateTime assignTime;

    private LocalDateTime startTime;

    private LocalDateTime finishTime;

    private Integer announceId;

    private String targetPos;

    private String targetSide;

    private LocalDateTime deadLine;

    private String liftArea;

    private Integer status;

    private String boxType;

    @TableField("Container_status")
    private Integer containerStatus;

    private Integer targetFloor;

    private Integer width;

    private Integer carGroup;

    private LocalDateTime pdTime;

    private LocalDateTime pickupTime;

    private LocalDateTime leaveTime;

    private Integer orderState;

    private Integer runningState;

    //private String orderNo;


}
