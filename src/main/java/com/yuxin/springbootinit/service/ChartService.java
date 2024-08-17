package com.yuxin.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuxin.springbootinit.mapper.ChartMapper;
import com.yuxin.springbootinit.model.dto.chart.ChartQueryRequest;
import com.yuxin.springbootinit.model.entity.Chart;
import com.yuxin.springbootinit.model.vo.ChartVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子服务
 *
*/
public interface ChartService extends IService<Chart> {

    @Resource
    ChartMapper chartMapper = null;

    /**
     * 校验
     *
     * @param chart
     * @param add
     */
    void validChart(Chart chart, boolean add);

    /**
     * 获取查询条件
     *
     * @param chartQueryRequest
     * @return
     */
    QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);

    /**
     * 从 ES 查询
     *
     * @param chartQueryRequest
     * @return
     */
    Page<Chart> searchFromEs(ChartQueryRequest chartQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param chart
     * @param request
     * @return
     */
    ChartVO getChartVO(Chart chart, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param chartPage
     * @param request
     * @return
     */
    Page<ChartVO> getChartVOPage(Page<Chart> chartPage, HttpServletRequest request);
}
