package com.lee.database.mk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2022-06-20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("ga_announce")
public class Announce implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String boxNumber;

    private String wmsid;

    private Integer state;

    private Integer weight;

    private Integer level;

    private Integer location;

    private String area;

    private LocalDateTime announceTime;

    private LocalDateTime arriveTime;

    private Integer width;

    private String boxType;

    private Integer targetFloor;


}
