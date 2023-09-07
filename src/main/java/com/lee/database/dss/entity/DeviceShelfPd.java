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
@TableName("ga_device_shelf_pd")
public class DeviceShelfPd implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer boxLiftId;

    private String type;

    private String ip;

    private Integer port;

    private Integer level;

    private Integer inboundAisle;

    private Integer outboundAisle;

    private Integer inboundPos;

    private Integer outboundPos;

    private Integer inboundLocation;

    private Integer outboundLocation;

    private Boolean active;

    private Integer workMode;

    private Integer freeStation;

    private Integer inboundState;

    private Integer inboundRequest;

    private String inboundBarcode;

    private LocalDateTime inboundArriveTime;

    private Integer inboundState6;

    private Integer inboundRequest6;

    private String inboundBarcode6;

    private LocalDateTime inboundArriveTime6;

    private Integer outboundState;

    private Integer outboundAllow;

    private String outboundBarcode;

    private Integer outboundState6;

    private Integer outboundAllow6;

    private String outboundBarcode6;

    /**
     * 1 入库pd  2 出库pd  3 出入库类型
     */
    private Integer inOutType;

    /**
     * pd组
     */
    private String pdGroup;


}
