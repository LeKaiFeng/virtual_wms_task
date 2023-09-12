package com.lee.database.std.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
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
 * @since 2022-06-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("ga_locations")
public class Locations implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private int level;
    @TableId
    private int location;

    private int pos;

    private int aisle;

    private int state;

    private String area;

    //private Integer priority;

    private String boxNumber;

    //private Integer weight;

    private String liftArea;

    //private String colorArea;

    private int type;

    //@TableField("Container_status")
    //private Integer containerStatus;

    //private String orderNo;

    //private Integer boxType;

    //private Integer width;


}
