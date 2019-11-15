package com.unifig.organ.dto;

import com.unifig.organ.model.UmsPermission;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *    on 2018/9/30.
 */
public class UmsPermissionNode extends UmsPermission {
    @Getter
    @Setter
    private List<UmsPermissionNode> children;
}
