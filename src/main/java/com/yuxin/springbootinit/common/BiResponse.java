package com.yuxin.springbootinit.common;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

/**
 * packageName com.yuxin.springbootinit.common
 * @author yuxin
 * @description 生成图表响应类*/
@Data
public class BiResponse {
    private String genChartData;
    private String genChartResult;
    private Long ChartId;

    public BiResponse(Long id) {
        this.genChartData = null;
        this.genChartResult = null;
        ChartId = id;
    }

}
