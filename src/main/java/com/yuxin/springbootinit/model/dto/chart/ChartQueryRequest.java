package com.yuxin.springbootinit.model.dto.chart;

import com.yuxin.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * 查询请求
 *
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    @Id
    private Long id;

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