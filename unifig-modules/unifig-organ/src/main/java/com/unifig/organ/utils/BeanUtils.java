package com.unifig.organ.utils;

import com.unifig.model.UmsMember;
import com.unifig.organ.vo.UserVo;
import com.unifig.utils.BeanMapUtils;

import java.util.ArrayList;
import java.util.List;

public final class BeanUtils extends BeanMapUtils {

    public static List<UserVo> covertMgCarCatVo(List<UmsMember> records) {
        if (records == null) return null;
        List<UserVo> userVos = new ArrayList<UserVo>();
        for (UmsMember umsMember : records) {
            userVos.add(new UserVo(umsMember));
        }
        return userVos;
    }

}
