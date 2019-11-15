package com.unifig.organ.service.impl;

import com.unifig.organ.constant.Constants;
import com.unifig.organ.mapper.UmsMemberReceiveAddressMapper;
import com.unifig.organ.model.UmsMemberReceiveAddress;
import com.unifig.organ.model.UmsMemberReceiveAddressExample;
import com.unifig.organ.service.UmsMemberReceiveAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 用户地址管理Service实现类
 *    on 2018/8/28.
 */
@Service
public class UmsMemberReceiveAddressServiceImpl implements UmsMemberReceiveAddressService {

    @Autowired
    private UmsMemberReceiveAddressMapper addressMapper;

    @Override
    public int add(UmsMemberReceiveAddress address, String userId) {
        address.setMemberId(Long.valueOf(userId));
        int insert = addressMapper.insert(address);
        if(address.getDefaultStatus()!=null&&address.getDefaultStatus()==Constants.DEFAULT_VAULE_INT_ONE){
            defaultStatus(address.getId(),userId);
        }
        return insert;
    }

    @Override
    public int delete(Long id, String userId) {
        UmsMemberReceiveAddressExample example = new UmsMemberReceiveAddressExample();
        example.createCriteria().andMemberIdEqualTo(Long.valueOf(userId)).andIdEqualTo(id);
        return addressMapper.deleteByExample(example);
    }

    @Override
    public int update(Long id, UmsMemberReceiveAddress address, String userId) {
        address.setId(null);
        UmsMemberReceiveAddressExample example = new UmsMemberReceiveAddressExample();
        example.createCriteria().andMemberIdEqualTo(Long.valueOf(userId)).andIdEqualTo(id);
        return addressMapper.updateByExampleSelective(address, example);
    }

    @Override
    public List<UmsMemberReceiveAddress> list(String userId) {
        UmsMemberReceiveAddressExample example = new UmsMemberReceiveAddressExample();
        example.createCriteria().andMemberIdEqualTo(Long.valueOf(userId));
        return addressMapper.selectByExample(example);
    }

    @Override
    public UmsMemberReceiveAddress getItem(Long id, String userId) {
        UmsMemberReceiveAddressExample example = new UmsMemberReceiveAddressExample();
        example.createCriteria().andMemberIdEqualTo(Long.valueOf(userId)).andIdEqualTo(id);
        List<UmsMemberReceiveAddress> addressList = addressMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(addressList)) {
            return addressList.get(0);
        }
        return null;
    }

    @Override
    public UmsMemberReceiveAddress defaultStatus(Long id, String userId) {
        UmsMemberReceiveAddressExample example = new UmsMemberReceiveAddressExample();
        example.createCriteria().andMemberIdEqualTo(Long.valueOf(userId)).andDefaultStatusEqualTo(Constants.DEFAULT_VAULE_INT_ONE);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressesList = addressMapper.selectByExample(example);
        for (UmsMemberReceiveAddress umsMemberReceiveAddress : umsMemberReceiveAddressesList) {
            umsMemberReceiveAddress.setDefaultStatus(Constants.DEFAULT_VAULE_INT_ZERO);
            addressMapper.updateByPrimaryKey(umsMemberReceiveAddress);
        }
        UmsMemberReceiveAddress umsMemberReceiveAddress = addressMapper.selectByPrimaryKey(id);
        umsMemberReceiveAddress.setDefaultStatus(Constants.DEFAULT_VAULE_INT_ONE);
        addressMapper.updateByPrimaryKey(umsMemberReceiveAddress);
        return umsMemberReceiveAddress;
    }
}
