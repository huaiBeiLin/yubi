package com.yupi.springbootinit.model.dto.chart;

import lombok.Data;

/**
 * packageName com.yupi.springbootinit.model.dto.chart
 *
 * @author yuxin
 * @version JDK 8
 * @className GenChartByAiRequest (此处以class为例)
 * @date 2024/7/12
 * @description 生成图表请求
 */
@Data
public class GenChartByAiRequest {
    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;


    /**
     * 图表名称
     */
    private String name;
}
