package com.lee.database.dss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2022-08-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ga_resource_task")
public class ResourceTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer bindId;

    private Integer type;

    private String barcode;

    private Integer state;

    private Integer phase;

    private Integer carNo;

    private Integer startLevel;

    private Integer startAisle;

    private Integer startPos;

    private Integer startLocation;

    private Integer endLevel;

    private Integer endAisle;

    private Integer endPos;

    private Integer endLocation;

    private Integer changeReason;

    private Integer realEndLevel;

    private Integer realEndAisle;

    private Integer realEndPos;

    private Integer realEndLocation;

    private Integer outLevel;

    private String area;

    private Integer priority;

    private String wmsId;

    private Integer weight;

    private Integer boxType;

    private LocalDateTime createTime;

    private LocalDateTime assignTime;

    private LocalDateTime startTime;

    private LocalDateTime finishTime;

    private Integer liftId;

    private Integer liftType;

    private Integer liftLevel;

    private Integer liftPos;

    private Integer orderState;

    private Integer runningState;

    /**
     * 到达层间线（入库）
     */
    private LocalDateTime arrivePdTime;

    /**
     * 取货完成时间
     */
    private LocalDateTime pickUpTime;

    /**
     * 放货行走开始时间(初次)
     */
    private LocalDateTime putdownWalkingTime;

    /**
     * 离开提升机（出库）
     */
    private LocalDateTime leaveTime;


}
