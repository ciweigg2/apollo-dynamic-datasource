package com.example.apollodynamicdatasource.user.model;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author Ciwei
 * @since 2019-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class UserModel implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 用户id
     */
    private Integer id;

    /**
     * 用户名称
     */
    private String name;


}
