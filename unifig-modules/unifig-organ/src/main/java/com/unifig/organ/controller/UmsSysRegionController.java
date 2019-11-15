package com.unifig.organ.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.unifig.organ.cache.RegionCacheUtil;
import com.unifig.organ.dto.PageUtils;
import com.unifig.organ.dto.R;
import com.unifig.organ.model.SysRegionEntity;
import com.unifig.organ.service.UmsSysRegionService;
import com.unifig.organ.utils.TreeUtils;
import com.unifig.organ.vo.RegionVo;
import com.unifig.result.ResultData;
import com.unifig.utils.Query;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地址管理Controller
 *
 *
 *
 * @date 2018-10-24
 */
@RestController
@RequestMapping("sys/region")
@ApiIgnore
public class UmsSysRegionController {

    private static final Logger logger = LoggerFactory.getLogger(UmsSysRegionController.class);

    @Autowired
    private UmsSysRegionService sysRegionService;
    /**
     * 查看列表
     *
     * @param params 请求参数
     * @return R
     */
    @RequestMapping("/list")
    public ResultData list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);

        List<SysRegionEntity> regionList = sysRegionService.queryList(query);
        int total = sysRegionService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(regionList, total, query.getLimit(), query.getPage());
        return ResultData.result(true).setMsg("true").setData(pageUtil.getList()).setCount(total);

      //  return R.ok().put("page", pageUtil);
    }

    /**
     * 根据主键获取信息
     *
     * @param id 主键
     * @return R
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        SysRegionEntity region = sysRegionService.queryObject(id);

        return R.ok().put("region", region);
    }

    /**
     * 根据主键获取信息
     *
     * @微服务调用
     * @return R
     */
    @RequestMapping("/list/all")
    public  List<SysRegionEntity> listall() {
        List<SysRegionEntity> sysRegionEntities = sysRegionService.selectList(new EntityWrapper<SysRegionEntity>());
        return sysRegionEntities;
    }

    /**
     * 新增地址
     *
     * @param region 地址
     * @return R
     */
    @RequestMapping("/save")
    public R save(@RequestBody SysRegionEntity region) {
        sysRegionService.save(region);

        return R.ok();
    }

    /**
     * 修改地址
     *
     * @param region 地址
     * @return R
     */
    @RequestMapping("/update")
    public R update(@RequestBody SysRegionEntity region) {
        sysRegionService.update(region);

        return R.ok();
    }

    /**
     * 删除地址
     *
     * @param ids 主键集
     * @return R
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids) {
        sysRegionService.deleteBatch(ids);

        return R.ok();
    }

    /**
     * 查询所有国家
     *
     * @return R
     */
    @GetMapping("/getAllCountry")
    public ResultData getAllCountry() {
        List<SysRegionEntity> list = RegionCacheUtil.getAllCountry();
        return  ResultData.result(true).setData(list);
    }

    /**
     * 查询所有省份
     *
     * @return R
     */
    @GetMapping("/getAllProvice")
    public ResultData getAllProvice(@RequestParam Integer areaId) {
        List<SysRegionEntity> list = RegionCacheUtil.getAllProviceByParentId(areaId);
        //logger.info("SysRegionController-getAllProvice areaId:{} data:{}",areaId,list.size());
        return  ResultData.result(true).setData(list);
    }

    /**
     * 查询所有城市
     *
     * @return R
     */
    @GetMapping("/getAllCity")
    public ResultData getAllCity(@RequestParam Integer areaId) {
        List<SysRegionEntity> list = RegionCacheUtil.getChildrenCity(areaId);
        return  ResultData.result(true).setData(list);
    }


    /**
     * 查询所有区县
     *
     * @return R
     */
    @RequestMapping("/getChildrenDistrict")
    public ResultData getChildrenDistrict(@RequestParam Integer areaId) {
        List<SysRegionEntity> list = RegionCacheUtil.getChildrenDistrict(areaId);
        return  ResultData.result(true).setData(list);
    }

    /**
     * 查看信息(全部加载页面渲染太慢！)
     *
     * @return R
     */
    @RequestMapping("/getAreaTree")
    public R getAreaTree() {
        List<SysRegionEntity> list = RegionCacheUtil.sysRegionEntityList;
        for (SysRegionEntity sysRegionEntity : list) {
            sysRegionEntity.setValue(sysRegionEntity.getId() + "");
            sysRegionEntity.setLabel(sysRegionEntity.getName());
        }
        List<SysRegionEntity> node = TreeUtils.factorTree(list);

        return R.ok().put("node", node);
    }

    /**
     * 根据类型获取区域
     *
     * @param type 类型
     * @return R
     */
    @RequestMapping("/getAreaByType")
    public R getAreaByType(@RequestParam(required = false) Integer type) {

        List<SysRegionEntity> list = new ArrayList<>();
        if (type.equals(0)) {

        } else if (type.equals(1)) {//省份
            list = RegionCacheUtil.getAllCountry();
        } else if (type.equals(2)) {
            list = RegionCacheUtil.getAllProvice();
        } else if (type.equals(3)) {
            list = RegionCacheUtil.getAllCity();
        }
        return R.ok().put("list", list);
    }

    @PostMapping("list")
    public Object list(Integer parentId) {
        List<SysRegionEntity> regionEntityList = RegionCacheUtil.getChildrenByParentId(parentId);
        List<RegionVo> regionVoList = new ArrayList<RegionVo>();
        if (null != regionEntityList && regionEntityList.size() > 0) {
            for (SysRegionEntity sysRegionEntity : regionEntityList) {
                regionVoList.add(new RegionVo(sysRegionEntity));
            }
        }
        return toResponsSuccess(regionVoList);
    }

    @PostMapping("provinceList")
    public Object provinceList() {
        List<SysRegionEntity> regionEntityList = RegionCacheUtil.getAllProvice();
        List<RegionVo> regionVoList = new ArrayList<RegionVo>();
        if (null != regionEntityList && regionEntityList.size() > 0) {
            for (SysRegionEntity sysRegionEntity : regionEntityList) {
                regionVoList.add(new RegionVo(sysRegionEntity));
            }
        }
        return toResponsSuccess(regionVoList);
    }

    @PostMapping("cityList")
    public Object provinceList(String proviceName) {
        List<SysRegionEntity> regionEntityList = RegionCacheUtil.getChildrenCity(proviceName);
        List<RegionVo> regionVoList = new ArrayList<RegionVo>();
        if (null != regionEntityList && regionEntityList.size() > 0) {
            for (SysRegionEntity sysRegionEntity : regionEntityList) {
                regionVoList.add(new RegionVo(sysRegionEntity));
            }
        }
        return toResponsSuccess(regionVoList);
    }

    @PostMapping("distinctList")
    public Object distinctList(String proviceName, String cityName) {
        List<SysRegionEntity> regionEntityList = RegionCacheUtil.getChildrenDistrict(proviceName, cityName);
        List<RegionVo> regionVoList = new ArrayList<RegionVo>();
        if (null != regionEntityList && regionEntityList.size() > 0) {
            for (SysRegionEntity sysRegionEntity : regionEntityList) {
                regionVoList.add(new RegionVo(sysRegionEntity));
            }
        }
        return toResponsSuccess(regionVoList);
    }

    @PostMapping("wxinfo")
    public Object wxinfo(Integer regionId) {
        SysRegionEntity regionEntity = RegionCacheUtil.getAreaByAreaId(regionId);
        return toResponsSuccess(new RegionVo(regionEntity));
    }

    @PostMapping("regionIdsByNames")
    public Object regionIdsByNames(String provinceName, String cityName, String districtName) {
        Map<String, Integer> resultObj = new HashMap<String, Integer>();
        Integer provinceId = 0;
        Integer cityId = 0;
        Integer districtId = 0;
        if (null != provinceName) {
            provinceId = RegionCacheUtil.getProvinceIdByName(provinceName);
        }
        if (null != provinceId && StringUtils.isNotEmpty(cityName)) {
            cityId = RegionCacheUtil.getCityIdByName(provinceId, cityName);
        }
        if (null != provinceId && null != cityId && StringUtils.isNotEmpty(districtName)) {
            districtId = RegionCacheUtil.getDistrictIdByName(provinceId, cityId, districtName);
        }
        resultObj.put("provinceId", provinceId);
        resultObj.put("cityId", cityId);
        resultObj.put("districtId", districtId);
        return toResponsSuccess(resultObj);
    }



    /**
     * @param requestCode
     * @param msg
     * @param data
     * @return Map<String,Object>
     * @throws
     * @Description:构建统一格式返回对象
     * @date 2016年9月2日
     * @author zhuliyun
     */
    public Map<String, Object> toResponsObject(int requestCode, String msg, Object data) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", requestCode);
        obj.put("errmsg", msg);
        if (data != null)
            obj.put("data", data);
        return obj;
    }

    public Map<String, Object> toResponsSuccess(Object data) {
        Map<String, Object> rp = toResponsObject(0, "执行成功", data);
        return rp;
    }

    /**
     * 创建日期:2018年4月6日<br/>
     * 代码创建:maxl<br/>
     * 功能描述:通过request来获取到json数据<br/>
     * @param request
     * @return
     */
    public JSONObject getJSONParam(HttpServletRequest request){
        JSONObject jsonParam = null;
        try {
            // 获取输入流
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));

            // 写入数据到Stringbuilder
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = streamReader.readLine()) != null) {
                sb.append(line);
            }
            jsonParam = JSONObject.parseObject(sb.toString());
            // 直接将json信息打印出来
            System.out.println(jsonParam.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonParam;
    }


}
