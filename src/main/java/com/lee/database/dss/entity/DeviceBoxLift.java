package com.lee.database.dss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("ga_device_box_lift")
public class DeviceBoxLift implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String type;

    private String ip;

    private Integer port;

    /**
     * 出入库事件的连接参数, 使用json格式
     */
    private String inOutEventConnectorArgs;

    private Integer level;

    private Integer aisle;

    private Integer inboundAisle;

    private Integer outboundAisle;

    private Integer pos;

    private Integer inboundPos;

    private Integer outboundPos;

    private Boolean active;

    private Boolean master;

    private String allowLevel;

    private Integer workMode;

    private Integer inboundRequest;

    private String barcode;

    /**
     * 1 入库  2 出库  3 出入库
     */
    private Integer inOutType;

    private String inToLiftEvent;

    private String inToLevelEvent;

    private String outToLiftEvent;

    private String outToLineEvent;


}
