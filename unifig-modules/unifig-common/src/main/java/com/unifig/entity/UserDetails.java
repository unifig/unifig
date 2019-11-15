package com.unifig.entity;

import com.unifig.context.Constants;
import org.apache.commons.collections.MapUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * 后台用户详情封装
 *    on 2019/01/18.
 */
public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {
    private Map<Object, Object> userInfo;

    public UserDetails(Map<Object, Object> userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //返回当前用户的权限
        return Arrays.asList(new SimpleGrantedAuthority("TEST"));
    }

    @Override
    public String getPassword() {
        return MapUtils.getString(userInfo,Constants.RATEL_USER_PASSWORD);
    }

    @Override
    public String getUsername() {
       return MapUtils.getString(userInfo,Constants.RATEL_USER_ID);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        Integer status = MapUtils.getInteger(userInfo, Constants.RATEL_USER_STATUS);
        if(status==null){
            status=0;
        }
        return  status == 1;
    }

    public Map<Object, Object> getUserInfo() {
        return userInfo;
    }
}
