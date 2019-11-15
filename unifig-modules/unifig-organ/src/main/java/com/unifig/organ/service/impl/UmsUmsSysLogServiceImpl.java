package com.unifig.organ.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.unifig.organ.dao.UmsSysLogDao;
import com.unifig.organ.model.SysLogEntity;
import com.unifig.organ.service.UmsSysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


@Service("sysLogService")
public class UmsUmsSysLogServiceImpl implements UmsSysLogService {
    @Autowired
    private UmsSysLogDao umsSysLogDao;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public SysLogEntity queryObject(Long id) {
        return umsSysLogDao.queryObject(id);
    }

    @Override
    public List<SysLogEntity> queryList(Map<String, Object> map) {
        List<SysLogEntity> list = umsSysLogDao.queryList(map);

        for (SysLogEntity sysLogEntity : list) {
            sysLogEntity.setIp(getIpDetails(sysLogEntity.getIp()));
        }
        return list;
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return umsSysLogDao.queryTotal(map);
    }

    @Override
    public void save(SysLogEntity sysLog) {
        umsSysLogDao.save(sysLog);
    }

    @Override
    public void update(SysLogEntity sysLog) {
        umsSysLogDao.update(sysLog);
    }

    @Override
    public void delete(Long id) {
        umsSysLogDao.delete(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        umsSysLogDao.deleteBatch(ids);
    }

    /**
     * 向指定URL发送GET方法的请求
     */
    public String getIpDetails(String ip) {
        String str = null;

        if (ip.startsWith("0:") || ip.startsWith("0.") || ip.startsWith("127.")) {
            return str;
        }
        try {
            str = restTemplate.getForObject("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip, String.class);
            JSONObject jsonObject = JSONObject.parseObject(str.toString());

            //{"code":0,"data":{"ip":"1.1.1.1","country":"澳大利亚","area":"","region":"XX","city":"XX","county":"XX","isp":"XX","country_id":"AU","area_id":"","region_id":"xx","city_id":"xx","county_id":"xx","isp_id":"xx"}}
            jsonObject = (JSONObject) jsonObject.get("data");

            str = ip + ":" + jsonObject.getString("country") + " " + jsonObject.getString("region") + " "
                    + jsonObject.getString("city") + " " + jsonObject.getString("county") + " " + jsonObject.getString("isp");
        } catch (RestClientException e) {
            str = ip;
        }
        return str;
    }
}
