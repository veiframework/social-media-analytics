package com.chargehub.admin.api.model;

import com.chargehub.admin.api.domain.SysUser;
import com.chargehub.common.core.constant.CacheConstants;
import com.chargehub.common.core.constant.SecurityConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户信息
 *
 * @author ruoyi
 */
public class LoginUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 用户名id
     */
    private Long userid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 权限列表
     */
    private Set<String> permissions;

    /**
     * 角色列表
     */
    private Set<String> roles;

    /**
     * 用户信息
     */
    private SysUser sysUser;

    private String info;

    private long expireDuration = CacheConstants.EXPIRATION;

    private Integer shopId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public SysUser getSysUser() {
        return sysUser;
    }

    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    public Set<String> ownerIds() {
        Set<String> ownerIds = new HashSet<>();
//        Set<String> roles = this.getRoles();
//        if (CollectionUtils.isNotEmpty(roles)) {
//            ownerIds.addAll(roles);
//        }
//        Set<String> permissions = this.getPermissions();
//        if (CollectionUtils.isNotEmpty(permissions)) {
//            ownerIds.addAll(permissions);
//        }
        if (userid != null) {
            ownerIds.add(String.valueOf(userid));
        }
        if (StringUtils.isNotBlank(username)) {
            ownerIds.add(username);
        }
        return ownerIds;
    }

    public boolean isAdmin() {
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        return getRoles().contains(SecurityConstants.ROLE_ADMIN);
    }

    public boolean isJtAdmin() {
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        return getRoles().contains(SecurityConstants.ROLE_JT_ADMIN);
    }

    public boolean isYwAdmin() {
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        return getRoles().contains(SecurityConstants.ROLE_YW_ADMIN);
    }

    public boolean isZgAdmin() {
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        return getRoles().contains(SecurityConstants.ROLE_ZG_ADMIN);
    }

    public boolean isSuperAdmin(){
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        return getRoles().contains("管理员");
    }

    public boolean isCharger(){
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        return getRoles().contains("平台负责人");
    }

    public boolean isLabor(){
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        return getRoles().contains("员工");
    }

    public boolean isLeader(){
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        return getRoles().contains("组长");
    }

    public boolean isNormalUser() {
       return !this.isAdmin() && !this.isCharger();
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public long getExpireDuration() {
        return expireDuration;
    }

    public void setExpireDuration(long expireDuration) {
        this.expireDuration = expireDuration;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }
}
