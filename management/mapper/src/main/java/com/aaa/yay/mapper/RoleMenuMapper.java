package com.aaa.yay.mapper;

import com.aaa.yay.model.RoleMenu;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RoleMenuMapper extends Mapper<RoleMenu> {

    /**
    * 批量新增
    * @author yay
    * @param roleMenus
    * @updateTime 2020/07/16 19:53
    * @throws
    * @return int
    */
    int batchInsertRoleMenu(List<RoleMenu> roleMenus);

    /**
    * 根据roleId删除menu
    * @author yay
    * @param roleId
    * @updateTime 2020/07/16 21:45
    * @throws
    * @return int
    */
    int deleteRoleMenu(Long roleId);

    /**
    * 根据roleId查询是否有menuId
    * @author yay
    * @param roleId
    * @updateTime 2020/07/16 21:49
    * @throws
    * @return java.util.List<com.aaa.yay.model.RoleMenu>
    */
    List<RoleMenu> selectMenuByRoleId(Long roleId);
}