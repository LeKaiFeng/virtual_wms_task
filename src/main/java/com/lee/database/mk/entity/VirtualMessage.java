package com.lee.database.mk.entity;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
 * @since 2022-06-20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("virtual_message")
public class VirtualMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String messagebody;

    private String responsebody;

    private Integer state;

    @TableField("requestTime")
    private LocalDateTime requestTime;
    @TableField("responseTime")
    private LocalDateTime responseTime;

    public VirtualMessage(String request) {
        JSONObject entries = JSONUtil.parseObj(request);
        this.id = entries.getInt("id");
        this.name = entries.getStr("messageName");
        this.messagebody = request;
        this.state = 0;
        this.requestTime = LocalDateTime.now();
        this.responseTime = null;
    }
}
