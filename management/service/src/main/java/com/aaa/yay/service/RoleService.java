package com.aaa.yay.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.aaa.yay.base.BaseService;
import com.aaa.yay.mapper.RoleMenuMapper;
import com.aaa.yay.model.Role;
import com.aaa.yay.model.RoleMenu;
import com.aaa.yay.utils.CompareListUtils;
import com.aaa.yay.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author yay
 * @Description Role
 * @CreatTime 2020年 07月16日 星期四 16:11:06
 */
@Service
public class RoleService extends BaseService<Role> {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    /**
    * 新增角色以及批量新增权限
    * @author yay
    * @param roleVo
    * @updateTime 2020/07/16 20:09
    * @throws
    * @return java.lang.Boolean
    */
    public Boolean addRole(RoleVo roleVo){

        //获取新增时间
        DateTime CreateTime = DateUtil.parse(DateUtil.now());
        roleVo.getRole().setCreateTime(CreateTime);

        // 执行新增
        int insertRole = getMapper().insert(roleVo.getRole());
        // 判断是否新增成功
        if (insertRole > 0){
            //说明新增成功了
            //那就开始加roleMenu
            //判断 如果传过来的menuId是null 说明不添加了 如果传来的不是空  说明需要添加roleMenu
            if (null == roleVo.getMenuId() || 0 == roleVo.getMenuId().size()){
                //说明不添加权限  只是加一个角色 返回true结束
                return true;
            }else {
                //说明需要添加新的菜单权限
                List<RoleMenu> list = new ArrayList<>();
                //遍历菜单权限
                for (Long menuId : roleVo.getMenuId()){
                    RoleMenu roleMenu = new RoleMenu();
                    roleMenu.setMenuId(menuId).setRoleId(roleVo.getRole().getRoleId());
                    list.add(roleMenu);
                }
                int insertRoleMenu = roleMenuMapper.batchInsertRoleMenu(list);
                //说明批量新增菜单权限成功
                return insertRoleMenu > 0;
            }
        }
        //新增失败
        return false;
    }

    /**
    * 删除角色以及对应的菜单
    * @author yay
    * @param roleId
    * @updateTime 2020/07/16 22:16
    * @throws
    * @return java.lang.Boolean
    */
    public Boolean deleteRole (Long roleId) {
        int deleteResult = getMapper().deleteByPrimaryKey(roleId);
        if (deleteResult > 0) {
            // 说明删除成功
            // 接下来要去把role_menu表中对应的数据删掉
            //先去查他有没有权限 有权限就全部删掉 没有就不删
            List<RoleMenu> roleMenus = roleMenuMapper.selectMenuByRoleId(roleId);
            if (roleMenus.size() > 0) {
                //说明权限不是是空的  需要删除
                int deleteRoleMenuResult = roleMenuMapper.deleteRoleMenu(roleId);
                if (deleteRoleMenuResult > 0) {
                    //说明删除成功
                    return true;
                } else {
                    return false;
                }
            } else {
                //说明没有权限 直接返回true
                return true;
            }
        }
        //删除失败
        return false;
    }

    /**
    * 修改角色及其权限
    * @author yay
    * @param roleVo
    * @updateTime 2020/07/16 23:27
    * @throws
    * @return java.lang.Boolean
    */
    public Boolean updateRole(RoleVo roleVo){

        //获取修改时间
        DateTime modifyTime = DateUtil.parse(DateUtil.now());
        roleVo.getRole().setModifyTime(modifyTime);

        // 修改role表
        int updateResult = getMapper().updateByPrimaryKeySelective(roleVo.getRole());
        if (updateResult > 0){
            //说明修改成功 需要继续修改菜单
            //在这要先判断菜单表有没有被改动 如果没改动 直接返回true
            //如果改动了 就执行修改菜单操作
            //先根据roleId查询menuId
            List<RoleMenu> roleMenus = roleMenuMapper.selectMenuByRoleId(roleVo.getRole().getRoleId());
            //判断是否被改动
            if (CompareListUtils.compareList(roleMenus,roleVo.getMenuId())){
                //说明没有改动
                return true;
            }else {
                // 说明要改动权限表  那就先查他之前是否有权限
                List<RoleMenu> roleMenus1 = roleMenuMapper.selectMenuByRoleId(roleVo.getRole().getRoleId());
                if (roleMenus1.size() > 0){
                    //说明以前是有权限的  无论是要给他撤销全部的权限  还是要更改他的权限  都先全部删除
                    int deleteRoleMenuResult = roleMenuMapper.deleteRoleMenu(roleVo.getRole().getRoleId());
                    if (deleteRoleMenuResult > 0){
                        //说明权限已经全部删除了   接下来判断是否要给他换上新的权限
                        //如果传进来的权限是空的
                        if (null == roleVo.getMenuId() || roleVo.getMenuId().size() == 0){
                            //说明没有新的权限
                            return true;
                        }else {
                            //说明要设置新的权限
                            List<RoleMenu> menuList = new ArrayList<>();
                            for (Long menuLists : roleVo.getMenuId()){
                                RoleMenu roleMenu = new RoleMenu();
                                roleMenu.setMenuId(menuLists).setRoleId(roleVo.getRole().getRoleId());
                                menuList.add(roleMenu);
                            }
                            //批量添加新的权限
                            int insertRoleMenuResult = roleMenuMapper.batchInsertRoleMenu(menuList);
                            if (insertRoleMenuResult > 0 ){
                                //说明添加成功 修改完成
                                return true;
                            }
                        }
                    }
                }
            }
        }
        //说明修改失败
        return false;
    }
}
