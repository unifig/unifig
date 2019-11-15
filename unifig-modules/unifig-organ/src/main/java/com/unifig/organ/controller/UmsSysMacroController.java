package com.unifig.organ.controller;

import com.unifig.organ.dto.PageUtils;
import com.unifig.organ.model.SysMacroEntity;
import com.unifig.organ.service.UmsSysMacroService;
import com.unifig.organ.dto.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * 通用字典表Controller
 *
 *
 *
 * @date 2018-10-24
 */
@RestController
@RequestMapping("sys/macro")
@ApiIgnore
public class UmsSysMacroController {
    @Autowired
    private UmsSysMacroService sysMacroService;

    /**
     * 所有字典列表
     *
     * @param params 请求参数
     * @return R
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        //分页参数
        params.put("offset", (Integer.parseInt(String.valueOf(params.get("page"))) - 1) * Integer.parseInt(String.valueOf(params.get("limit"))));

        List<SysMacroEntity> sysMacroList = sysMacroService.queryList(params);
        int total = sysMacroService.queryTotal(params);

        PageUtils pageUtil = new PageUtils(sysMacroList, total, Integer.parseInt(String.valueOf(params.get("limit"))), Integer.parseInt(String.valueOf(params.get("page"))));

        return R.ok().put("page", pageUtil);
    }

    /**
     * 根据主键获取字典信息
     *
     * @param macroId 主键
     * @return R
     */
    @RequestMapping("/info/{macroId}")
    public R info(@PathVariable("macroId") Long macroId) {
        SysMacroEntity sysMacro = sysMacroService.queryObject(macroId);

        return R.ok().put("macro", sysMacro);
    }

    /**
     * 新增字典
     *
     * @param sysMacro 字典
     * @return R
     */
    @RequestMapping("/save")
    public R save(@RequestBody SysMacroEntity sysMacro) {
        sysMacroService.save(sysMacro);

        return R.ok();
    }

    /**
     * 修改字典
     *
     * @param sysMacro 字典
     * @return R
     */
    @RequestMapping("/update")
    public R update(@RequestBody SysMacroEntity sysMacro) {
        sysMacroService.update(sysMacro);

        return R.ok();
    }

    /**
     * 删除字典
     *
     * @param macroIds 主键集
     * @return R
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] macroIds) {
        sysMacroService.deleteBatch(macroIds);

        return R.ok();
    }

    /**
     * 查看字典列表
     *
     * @param params 请求参数
     * @return R
     */
    @RequestMapping("/queryAll")
    public R queryAll(@RequestParam Map<String, Object> params) {

        List<SysMacroEntity> list = sysMacroService.queryList(params);

        return R.ok().put("list", list);
    }

    /**
     * 根据value查询数据字典
     *
     * @param value value
     * @return R
     */
    @RequestMapping("/queryMacrosByValue")
    public R queryMacrosByValue(@RequestParam String value) {

        List<SysMacroEntity> list = sysMacroService.queryMacrosByValue(value);

        return R.ok().put("list", list);
    }
}
