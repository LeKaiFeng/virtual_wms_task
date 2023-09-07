package com.lee.database.mk.entity;

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
 * @since 2022-06-20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("ga_locations")
public class Locations implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Integer level;

    @TableId
    private Integer location;

    private Integer pos;

    private Integer aisle;

    private Integer state;

    private String area;

    //private Integer priority;

    private String boxNumber;

    //private Integer weight;

    private String liftArea;

    //private String colorArea;

    private Integer type;

    //@TableField("Container_status")
    //private Integer containerStatus;

    //private Integer boxType;

    //private String resourceid;

    //private Integer liftRight;

}
