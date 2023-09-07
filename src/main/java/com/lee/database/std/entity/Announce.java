package com.lee.database.std.entity;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@AllArgsConstructor
@TableName("ga_announce")
public class Announce implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String boxNumber;

    private String wmsid;

    private Integer state;

    private Integer weight;

    private LocalDateTime announceTime;

    //private LocalDateTime arriveTime;

    private Integer level;

    private Integer location;

    private String area;

    public Announce(String boxNumber, Integer level, Integer location, String area) {
        this.boxNumber = boxNumber;
        this.wmsid = RandomUtil.randomString(18);
        this.state = 0;
        this.weight = 20;
        this.announceTime = LocalDateTime.now();
        this.level = level;
        this.location = location;
        this.area = area;
        this.targetFloor = "0";
        this.boxType = "S";
        this.seq = 1;
        this.width = 0;
    }
//@TableField("Container_status")
    //private Integer containerStatus;

    private String targetFloor;

    private String boxType;

    private Integer seq;

    //private String orderNo;

    private Integer width;


}
