package com.lee.database.std.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("ga_boxliftshelfpd")
public class Boxliftshelfpd implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer level;

    private Integer pos;

    private Integer inboundLocation;

    private Integer outboundLocation;

    private String type;

    private String ip;

    private Integer port;

    private Integer outboundState;

    private Integer outboundGrouplevel;

    private Integer outboundGrouppos;

    private Integer outboundGrouplocation;

    private Integer inboundRequest;

    private Integer inboundState;

    private String inboundBox;

    private String inboundArea;

    private String inboundDefaultArea;

    private String inboundWmsid;

    private LocalDateTime inboundArriveTime;

    private Integer inboundWeight;

    private Integer inboundPos;

    private Integer outboundPos;

    private String boxType;

    private Integer aisle;

    private String outboundArea;

    private Integer inboundTargetLocation;

    private Integer state;

    private String liftArea;

    private Integer pdType;

    private Integer isputdownEn;

    private Integer inboundPriority;

    private Integer remainingSpace;

    private Integer width;

    private Integer targetFloor;

    private String orderNo;

    @TableField("Container_status")
    private String containerStatus;

    private Integer existBox;


}
