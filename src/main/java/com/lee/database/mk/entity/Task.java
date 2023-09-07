package com.lee.database.mk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.lee.database.std.entity.Locations;
import lombok.Data;

/**
 * @TableName ga_task
 */
@TableName(value = "ga_task")
@Data
public class Task implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 主任务id
     */
    private Integer pid;

    /**
     *
     */
    private Integer oLevel;

    /**
     *
     */
    private Integer oPos;

    /**
     *
     */
    private Integer tLevel;

    /**
     *
     */
    private Integer tPos;

    /**
     *
     */
    private Integer type;

    /**
     *
     */
    private Integer sLevel;

    /**
     *
     */
    private Integer sPos;

    /**
     *
     */
    private Integer sLocation;

    /**
     *
     */
    private Integer eLevel;

    /**
     *
     */
    private Integer ePos;

    /**
     *
     */
    private Integer eLocation;

    /**
     *
     */
    private Integer rLevel;

    /**
     *
     */
    private Integer rPos;

    /**
     *
     */
    private Integer rLocation;

    /**
     *
     */
    private Integer state;

    /**
     *
     */
    private String boxNumber;

    /**
     *
     */
    private Integer carNumber;

    /**
     *
     */
    private Integer weight;

    /**
     *
     */
    private Integer priority;

    /**
     *
     */
    private String aisle;

    /**
     *
     */
    private String area;

    /**
     *
     */
    private String wmsid;

    /**
     *
     */
    private LocalDateTime createTime;

    /**
     *
     */
    private Date assignTime;

    /**
     *
     */
    private Date startTime;

    /**
     *
     */
    private Date finishTime;

    /**
     *
     */
    private Integer announceId;

    /**
     *
     */
    private String targetPos;

    /**
     *
     */
    private String targetSide;

    /**
     *
     */
    private Integer status;

    /**
     *
     */
    private Date deadLine;

    /**
     *
     */
    private String liftArea;

    /**
     *
     */
    private Integer carGroup;

    /**
     * 标记穿梭车提升机出入库
     */
    private Integer liftType;

    /**
     *
     */
    private Integer orderState;

    /**
     *
     */
    private Integer liftId;

    /**
     * 出库层
     */
    private Integer outLevel;

    /**
     *
     */
    private Integer runningState;

    /**
     * 宽度
     */
    private Integer width;

    /**
     * 长度
     */
    private Integer length;

    /**
     * 高度
     */
    private Integer height;

    /**
     *
     */
    private Date pdTime;

    /**
     *
     */
    private Date pickupTime;

    /**
     *
     */
    private Date leaveTime;

    /**
     *
     */
    private Integer targetFloor;

    /**
     *
     */
    private String boxType;

    /**
     * 输送线站点
     */
    private String site;

    /**
     * 转向
     */
    private Integer turn;

    /**
     * 输送线查询任务状态
     */
    private Integer lineState;

    /**
     * 是否是弹夹箱
     */
    private Integer isclipbox;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}