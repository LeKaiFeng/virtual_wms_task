package com.lee.database.std.entity;

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
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("ga_appointannounce")
public class Appointannounce implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String boxNumber;

    private String wmsid;

    public Appointannounce(String boxNumber, String wmsid, Integer state, Integer weight, Integer level, Integer location, LocalDateTime announceTime, Integer width,
                           String boxType) {
        this.boxNumber = boxNumber;
        this.wmsid = wmsid;
        this.state = state;
        this.weight = weight;
        this.level = level;
        this.location = location;
        this.announceTime = announceTime;
        this.width = width;
        this.boxType = boxType;
    }

    private Integer state;

    private Integer weight;

    private Integer level;

    private Integer location;

    //@TableField("liftId")
    //private Integer liftid;

    private LocalDateTime announceTime;

    //private LocalDateTime readTime;

    private Integer width;

    private String boxType;

    private Integer targetFloor;


}
