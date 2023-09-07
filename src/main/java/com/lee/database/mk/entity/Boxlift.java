package com.lee.database.mk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author lee
 * @since 2022-06-20
 */
@Data
//@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("ga_boxlift")
public class Boxlift implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String type;

    private String ip;

    private Integer port;

    //private Integer workmode;

    //private String allowLevel;

    private Integer pos;

    private String boxNumber;

    //private Integer targetLevel;

    //private Integer state;

    //private Integer isError;

    /**
     * 入库提升机是否异常
     */
    //private Integer inLongError;

    /**
     * 出库提升机是否异常
     */
    //private Integer outLongError;

    //private Integer boxliftId;

    //private Integer requestLevel;

    /**
     * 是否与提升机建立通讯
     */
    //private Integer isLiftConnected;

    /**
     * 是否主提升机
     */
    private Integer isMaster;

    /**
     * 是否启用snap7接口协议
     */
    //private Integer useS7;

    /**
     * 机架号
     */
    //private Integer rack;

    /**
     * 卡槽号
     */
    //private Integer slot;

    /**
     * DB区
     */
    //private Integer boxliftStateDbNum;

    /**
     * 接口读取起点
     */
    //private Integer boxliftStateDbStart;

    //private Integer barcodeLength;

    //private Integer barcodeCount;

    //private Integer deviceCount;

    //private String announceContent;

    /**
     * 事件监控端口
     */
    //@TableField("boxLiftEventPort")
    //private String boxlifteventport;

    /**
     * 入库提升机事件
     */
    //private String inToLiftEvent;

    /**
     * 入库到达层间线事件
     */
    //private String inToLevelEvent;

    /**
     * 出库提升机事件
     */
    //private String outToLiftEvent;

    /**
     * 出库到达层间线事件
     */
    //private String outToLineEvent;

    /**
     * 上位请求切换的模式
     */
    //private Integer switchModel;

    /**
     * 单翼提升机分左右   1左2右
     */
    //private Integer leftRight;


}
