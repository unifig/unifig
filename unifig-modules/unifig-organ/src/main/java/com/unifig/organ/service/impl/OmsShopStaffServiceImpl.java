package com.unifig.organ.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.organ.model.OmsShopStaff;
import com.unifig.organ.mapper.OmsShopStaffMapper;
import com.unifig.organ.service.OmsShopStaffService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.organ.service.UserService;
import com.unifig.utils.kartor.KartorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 店铺员工表 服务实现类
 * </p>
 *
 *
 * @since 2019-03-11
 */
@Service
public class OmsShopStaffServiceImpl extends ServiceImpl<OmsShopStaffMapper, OmsShopStaff> implements OmsShopStaffService {

    @Autowired
    private OmsShopStaffMapper OmsShopStaffMapper;

    @Autowired
    private UserService userService;

    @Override
    public OmsShopStaff save(OmsShopStaff OmsShop) {
        OmsShop.setCreateTime(new Date());
        OmsShopStaffMapper.insert(OmsShop);
        String openId = KartorUtils.createKartorUser(OmsShop.getAccountNumber().toString());
        if (openId == null) {
            return null;
        }
        int i = userService.updateUserShopId(OmsShop.getUserId(), OmsShop.getShopId(),openId);
        return OmsShop;
    }

    @Override
    public OmsShopStaff updateShop(OmsShopStaff OmsShop) {
        OmsShopStaffMapper.updateById(OmsShop);
        //解除关联店铺
        if(OmsShop.getStatus() == 2){
            userService.updateUserShopId(OmsShop.getUserId(), "0");
        }
        if(OmsShop.getStatus() == 0){
            KartorUtils.createKartorUser(OmsShop.getAccountNumber().toString());
        }else{
            OmsShopStaff omsShopStaff = OmsShopStaffMapper.selectById(OmsShop.getId());
            KartorUtils.delKartorUserTag(omsShopStaff.getAccountNumber().toString());
        }
        return OmsShop;
    }

    @Override
    public OmsShopStaff selectByShopId(String id) {
        return OmsShopStaffMapper.selectById(id);
    }

    @Override
    public Page<OmsShopStaff> selectShopList(Integer page, Integer rows, String shopId) {
        Page<OmsShopStaff> OmsShopPage = new Page<>();
        EntityWrapper<OmsShopStaff> wrapper = new EntityWrapper<OmsShopStaff>();
        wrapper.eq("shop_id",shopId);
        wrapper.ne("status",2);
        List<OmsShopStaff> OmsShops = OmsShopStaffMapper.selectPage(new Page<OmsShopStaff>(page, rows), wrapper);
        Integer integer = OmsShopStaffMapper.selectCount(wrapper);
        OmsShopPage.setRecords(OmsShops);
        OmsShopPage.setTotal(integer);
        return OmsShopPage;
    }

    @Override
    public List<OmsShopStaff> saveList(List<OmsShopStaff> omsShopStaff) {
        for (OmsShopStaff shopStaff : omsShopStaff) {
            shopStaff.setCreateTime(new Date());
            OmsShopStaffMapper.insert(shopStaff);
            String openId = KartorUtils.createKartorUser(shopStaff.getAccountNumber().toString());
            int i = userService.updateUserShopId(shopStaff.getUserId(), shopStaff.getShopId(),openId);
        }
        return omsShopStaff;
    }

    @Override
    public String selectByUserId(String id) {
        EntityWrapper<OmsShopStaff> wrapper = new EntityWrapper<OmsShopStaff>();
        wrapper.eq("user_id",id);
        wrapper.eq("status",0);
        List<OmsShopStaff> omsShopStaffs = OmsShopStaffMapper.selectList(wrapper);
        if(omsShopStaffs.size() > 0){
            return omsShopStaffs.get(0).getShopId();
        }
        return null;
    }
}
