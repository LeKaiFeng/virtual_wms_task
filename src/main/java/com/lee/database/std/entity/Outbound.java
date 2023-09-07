package com.lee.database.std.entity;

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
 * @since 2022-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ga_outbound")
public class Outbound implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer level;

    private Integer pos;

    private Integer location;

    private Integer groupLevel;

    private Integer groupPos;

    private Integer groupLocation;

    private Integer isActive;

    private Integer isError;

    private Integer closerAisle;

    private Integer liftId;


}
