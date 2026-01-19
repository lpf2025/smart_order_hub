package com.smartorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartorder.dto.RolePermSaveDTO;
import com.smartorder.dto.RolePermVO;
import com.smartorder.entity.SysPermission;
import com.smartorder.entity.SysRole;
import com.smartorder.entity.SysRolePerm;
import com.smartorder.mapper.SysPermissionMapper;
import com.smartorder.mapper.SysRoleMapper;
import com.smartorder.mapper.SysRolePermMapper;
import com.smartorder.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysRolePermMapper sysRolePermMapper;
    
    public RoleServiceImpl(SysRoleMapper sysRoleMapper, SysPermissionMapper sysPermissionMapper, 
                          SysRolePermMapper sysRolePermMapper) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysPermissionMapper = sysPermissionMapper;
        this.sysRolePermMapper = sysRolePermMapper;
    }
    
    @Override
    public List<SysRole> getRoleList() {
        return sysRoleMapper.selectList(null);
    }
    
    @Override
    public SysRole getRoleDetail(Long roleId) {
        return sysRoleMapper.selectById(roleId);
    }
    
    @Override
    public boolean addRole(SysRole role) {
        return sysRoleMapper.insert(role) > 0;
    }
    
    @Override
    public boolean updateRole(SysRole role) {
        return sysRoleMapper.updateById(role) > 0;
    }
    
    @Override
    @Transactional
    public boolean deleteRole(Long roleId) {
        LambdaQueryWrapper<SysRolePerm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePerm::getRoleId, roleId);
        sysRolePermMapper.delete(wrapper);
        
        return sysRoleMapper.deleteById(roleId) > 0;
    }
    
    @Override
    public List<RolePermVO> getRolePermissions(Long roleId) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysPermission::getPermId);
        List<SysPermission> allPerms = sysPermissionMapper.selectList(wrapper);
        
        List<Long> checkedPermIds;
        if (roleId != null) {
            LambdaQueryWrapper<SysRolePerm> rolePermWrapper = new LambdaQueryWrapper<>();
            rolePermWrapper.eq(SysRolePerm::getRoleId, roleId);
            List<SysRolePerm> rolePerms = sysRolePermMapper.selectList(rolePermWrapper);
            checkedPermIds = rolePerms.stream()
                    .map(SysRolePerm::getPermId)
                    .collect(Collectors.toList());
        } else {
            checkedPermIds = new ArrayList<>();
        }
        
        List<RolePermVO> permVOList = allPerms.stream()
                .map(perm -> {
                    RolePermVO vo = new RolePermVO();
                    vo.setPermId(perm.getPermId());
                    vo.setPermName(perm.getPermName());
                    vo.setPermCode(perm.getPermCode());
                    vo.setParentId(perm.getParentId());
                    vo.setMenuType(perm.getMenuType());
                    vo.setChecked(checkedPermIds.contains(perm.getPermId()));
                    return vo;
                })
                .collect(Collectors.toList());
        
        return buildPermTree(permVOList, 0L);
    }
    
    @Override
    @Transactional
    public boolean saveRolePermissions(RolePermSaveDTO dto) {
        LambdaQueryWrapper<SysRolePerm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePerm::getRoleId, dto.getRoleId());
        sysRolePermMapper.delete(wrapper);
        
        for (Long permId : dto.getPermIds()) {
            SysRolePerm rolePerm = new SysRolePerm();
            rolePerm.setRoleId(dto.getRoleId());
            rolePerm.setPermId(permId);
            sysRolePermMapper.insert(rolePerm);
        }
        
        return true;
    }
    
    private List<RolePermVO> buildPermTree(List<RolePermVO> permissions, Long parentId) {
        List<RolePermVO> tree = new ArrayList<>();
        
        for (RolePermVO perm : permissions) {
            if (parentId.equals(perm.getParentId())) {
                List<RolePermVO> children = buildPermTree(permissions, perm.getPermId());
                perm.setChildren(children);
                tree.add(perm);
            }
        }
        
        return tree;
    }
}
