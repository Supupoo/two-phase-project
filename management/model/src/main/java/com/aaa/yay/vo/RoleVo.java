package com.aaa.yay.vo;

import com.aaa.yay.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yay
 * @Description RoleVo
 * @CreatTime 2020年 07月16日 星期四 17:30:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RoleVo implements Serializable {

    private List<Long> menuId;
    private Role role;
    private Integer pageNo;
    private Integer pageSize;
}
