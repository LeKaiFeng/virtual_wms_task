package com.lee.database.dss.entity;

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
 * @since 2022-09-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ga_config_configure")
public class ConfigConfigure implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String name;

    private String value;

    private String valueRange;

    private String description;

    private Boolean showOnWeb;

    private String showType;

    private String showGroup;

    private String showName;


}
