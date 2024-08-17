package com.yuxin.springbootinit.mapper;

import com.yuxin.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author hp
* @description 针对表【chart(图表信息表)】的数据库操作Mapper
* @createDate 2024-07-20 09:53:24
* @Entity generator.domain.Chart
*/
public interface ChartMapper extends BaseMapper<Chart> {
    void updateGenChart(@Param("tableId") Long tableId);

    @MapKey("chartId")
    List<Map<String, Object>> queryAllByData1(@Param("data1") String data1, @Param("chartId") Long chartId);

    @MapKey("chartId")
    List<Map<String, Object>> queryAllByData2(@Param("data2") String data2, @Param("chartId") Long chartId);
    void  insertAll(@Param("data1") String data1, @Param("data2") String data2, @Param("chartId") Long chartId);
}




