package com.yupi.springbootinit.common;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

/**
 * packageName com.yupi.springbootinit.common
 *
 * @author yuxin
 * @version JDK 8
 * @className BiResponse (此处以class为例)
 * @date 2024/7/12
 * @description 生成图表响应类
 */
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
