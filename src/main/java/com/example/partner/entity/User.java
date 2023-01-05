package com.example.partner.entity;
import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import cn.hutool.core.annotation.Alias;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
* <p>
    * 
    *</p>
*
* @author 程序员人走茶凉
* @since 2022-12-27
*/
@Getter
@Setter
@TableName("sys_user")
@ApiModel(value = "User对象", description = "")
    public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    // 用户名
    @ApiModelProperty("用户名")
    @Alias("用户名")
    private String username;

    // 密码
    @ApiModelProperty("密码")
    @Alias("密码")
    private String password;

    // 昵称
    @ApiModelProperty("昵称")
    @Alias("昵称")
    private String name;

    // 用户唯一标识
    @ApiModelProperty("用户唯一标识")
    @Alias("用户唯一标识")
    private String uid;

    // 逻辑删除字段
    @ApiModelProperty("逻辑删除字段")
    @Alias("逻辑删除字段")
    @TableLogic(value = "0",delval = "id")
    private Integer deleted;

    // 创建时间
    @ApiModelProperty("创建时间")
    @Alias("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 更新时间
    @ApiModelProperty("更新时间")
    @Alias("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    // 邮箱
    @ApiModelProperty("邮箱")
    @Alias("邮箱")
    private String email;




}