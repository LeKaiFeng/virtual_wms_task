package com.lee.database.mk.entity;

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
 * @since 2022-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ga_boxliftshelfpd")
public class Boxliftshelfpd implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer state;

    private Integer level;

    private Integer aisle;

    private Integer pos;

    //private Integer inboundLocation;

    //private Integer inboundPos;

    //private Integer outboundLocation;

    //private Integer outboundPos;

    private String type;

    private String ip;

    private Integer port;

    //private Integer outboundState;

    //private Integer outboundGrouplevel;

    //private Integer outboundGrouppos;

    //private Integer outboundGrouplocation;

    private Integer inboundRequest;

    private Integer inboundState;

    private String inboundBox;

    //private String inboundArea;

    //private String inboundDefaultArea;

    //private String inboundWmsid;

    //private Integer inboundWeight;

    //private LocalDateTime inboundArriveTime;

    //private String existBox;

    //private Integer reserveFlash;

    /**
     * 是否与pd建立通讯
     */
    //private Integer isLiftConnected;

    //private Integer isputdownEn;

    //private Integer remainingSpace;

    /**
     * 上位请求的切换模式
     */
    //private Integer switchModel;

    /**
     * 1左2右
     */
    //private Integer leftRight;


}
