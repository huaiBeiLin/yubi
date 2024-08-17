package com.yuxin.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuxin.springbootinit.annotation.AuthCheck;
import com.yuxin.springbootinit.common.BaseResponse;
import com.yuxin.springbootinit.common.ErrorCode;
import com.yuxin.springbootinit.common.ResultUtils;
import com.yuxin.springbootinit.constant.UserConstant;
import com.yuxin.springbootinit.exception.ThrowUtils;
import com.yuxin.springbootinit.model.dto.chart.ChartQueryRequest;
import com.yuxin.springbootinit.model.entity.Chart;
import com.yuxin.springbootinit.model.vo.ChartVO;
import com.yuxin.springbootinit.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 图表接口
 *
*/
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;
    

    // region 增删改查

    /**
     * 分页获取列表（仅管理员）
     *
     * @param chartQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        Page<Chart> chartPage = chartService.page(new Page<>(current, size));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ChartVO>> listChartVOByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                        HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> postPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartService.getChartVOPage(postPage, request));
    }
}
