package com.yupi.springbootinit.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * packageName com.yupi.springbootinit.utils
 *
 * @author 你的名字
 * @version JDK 8
 * @className ExcelUtils (此处以class为例)
 * @date 2024/7/12
 * @description TODO*/

@Slf4j
public class ExcelUtils {
    /**
     * Excel转csv工具函数
     * @param multipartFile
     * @return
     */
    public static String ExcelToCsv(MultipartFile multipartFile) {
        try {
            // 上传文件
            List<Map<Integer, String>> list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .headRowNumber(0)
                    .sheet()
                    .doReadSync();
            LinkedHashMap<Integer, String> headerMap = (LinkedHashMap<Integer, String>) list.get(0);
            List<String> headlist = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(StringUtils.join(headlist, ",")).append("\n");

            for (int i = 1; i < list.size(); i++) {
                LinkedHashMap<Integer, String> dataMap = (LinkedHashMap<Integer, String>) list.get(i);
                List<String> datalist = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
                stringBuilder.append(StringUtils.join(datalist, ",")).append("\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            log.error("文件分析错误", e);
        }
        return "";
    }
}
