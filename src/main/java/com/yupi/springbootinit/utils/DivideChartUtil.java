package com.yupi.springbootinit.utils;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.mapper.ChartMapper;
import com.yupi.springbootinit.model.entity.Chart;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * packageName com.yupi.springbootinit.utils
 *
 * @author yuxin
 * @version JDK 8
 * @className DivideChartUtil (此处以class为例)
 * @date 2024/7/20
 * @description 分表工具类*/
public class DivideChartUtil {
    public void divideChart(Chart chart, @NotNull String data, ChartMapper chartMapper) {
        String[] lines = data.split("\n");
        chartMapper.updateGenChart(chart.getId());
        for (int i = 0; i < lines.length; i++) {
            String[] lineData = lines[i].split(",");
            chartMapper.insertAll(lineData[0], lineData[1], chart.getId());
        }
    }

    public List<Map<String, Object>> queryData(Chart chart, String dataType, String data, ChartMapper chartMapper) {
        if (!dataType.equalsIgnoreCase("1") && !dataType.equalsIgnoreCase("2")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入查询参数类型错误");
        }
        if (dataType.equalsIgnoreCase("1"))
            return chartMapper.queryAllByData1(data, chart.getId());
        else return  chartMapper.queryAllByData2(data, chart.getId());
    }


}
