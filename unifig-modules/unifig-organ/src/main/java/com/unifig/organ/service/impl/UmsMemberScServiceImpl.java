/**
 * FileName: 用户关系表
 * Author:   maxl
 * Date:     2019-08-30
 * Description: 用户关系表
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.organ.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.entity.cache.UserCache;
import com.unifig.organ.constant.Constants;
import com.unifig.organ.mapper.UmsMemberScMapper;
import com.unifig.organ.model.UmsMemberSc;
import com.unifig.organ.service.UmsMemberScService;
import com.unifig.organ.vo.UserShareVo;
import com.unifig.result.ResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.sql.Date;
import java.util.List;

/**
 * <p>
 * 用户关系表 服务实现类
 * </p>
 *
 *
 * @since 2019-08-30
 */
@Service
public class UmsMemberScServiceImpl extends ServiceImpl<UmsMemberScMapper, UmsMemberSc> implements UmsMemberScService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UmsMemberScMapper umsMemberScMapper;

    /**
     * 查询分页数据
     */
    @Override
    public ResultData<UmsMemberSc> findListByPage(int pageNum, int pageSize) {
        Integer count = umsMemberScMapper.selectCount(null);
        List<UmsMemberSc> umsMemberScList = umsMemberScMapper.selectPage(new Page<UmsMemberSc>(pageNum, pageSize), null);
        return ResultData.result(true).setData(umsMemberScList).setCount(count);
    }


    /**
     * 根据id查询
     */
    @Override
    public ResultData<UmsMemberSc> getById(String id) {
        UmsMemberSc umsMemberSc = umsMemberScMapper.selectById(id);
        return ResultData.result(true).setData(umsMemberSc);
    }

    /**
     * 新增
     */
    @Override
    public ResultData add(UmsMemberSc umsMemberSc) {
        umsMemberScMapper.insert(umsMemberSc);
        return ResultData.result(true).setMsg("新增成功");
    }

    /**
     * 删除
     */
    @Override
    public ResultData delete(List<String> ids) {
        if (null != ids && ids.size() > 0) {
            for (String id : ids) {
                umsMemberScMapper.deleteById(id);
            }
            return ResultData.result(true).setMsg("删除成功");
        } else {
            return ResultData.result(true).setMsg("删除失败,请检查ids是否为空!");
        }
    }

    /**
     * 修改
     */
    @Override
    public ResultData update(UmsMemberSc umsMemberSc) {
        umsMemberScMapper.updateById(umsMemberSc);
        return ResultData.result(true).setMsg("修改成功");
    }

    @Override
    public UmsMemberSc getByToId(String userId) {
        EntityWrapper<UmsMemberSc> entityWrapper = new EntityWrapper<UmsMemberSc>();
        entityWrapper.eq("enable", Constants.DEFAULT_VAULE_INT_ONE);
        entityWrapper.eq("to_id", userId);
        UmsMemberSc umsMemberSc = selectOne(entityWrapper);
        return umsMemberSc;
    }

    @Override
    public int checkUserHaveFrom(String toUserId,String fromUserId) {
        if(fromUserId.equals(toUserId))return Constants.DEFAULT_VAULE_INT_ONE;
        EntityWrapper<UmsMemberSc> entityWrapper = new EntityWrapper<UmsMemberSc>();
        entityWrapper.eq("enable", Constants.DEFAULT_VAULE_INT_ONE);
        entityWrapper.eq("to_id", toUserId);
        entityWrapper.or().eq("to_id",fromUserId).eq("from_id",toUserId).eq("enable",Constants.DEFAULT_VAULE_INT_ONE);
        int i = selectCount(entityWrapper);
        return i;
    }

    @Override
    public void bindSc(UserShareVo userShareVo, UserCache userCache,String code) {
        Date now = new Date(System.currentTimeMillis());
        UmsMemberSc umsMemberSc = new UmsMemberSc();
        umsMemberSc.setFromId(userShareVo.getUserId());
        umsMemberSc.setToNickname(userCache.getNickName());
        umsMemberSc.setToId(userCache.getUserId());
        umsMemberSc.setOrCode(code);
        umsMemberSc.setCreateTime(now);
        umsMemberSc.setEditTime(now);
        umsMemberSc.setEnable(Constants.DEFAULT_VAULE_INT_ONE);
        insert(umsMemberSc);
    }
}
