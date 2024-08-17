package com.yuxin.springbootinit.model.dto.chart;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *
*/
@Data
public class ChartAddRequest implements Serializable {

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表数据
     */
    private String chartData;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * 生成的图表
     */
    private String genChart;

    /**
     * 生成的结论
     */
    private String genResult;

    /**
     * 用户id
     */
    private Long UserId;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 图表名称
     */
    private String name;

    private static final long serialVersionUID = 1L;
}