package com.yuxin.springbootinit.controller;

import cn.hutool.core.io.FileUtil;
import com.github.rholder.retry.RetryException;
import com.yuxin.springbootinit.bizmq.MyMessageProducer;
import com.yuxin.springbootinit.common.BaseResponse;
import com.yuxin.springbootinit.common.BiResponse;
import com.yuxin.springbootinit.common.ErrorCode;
import com.yuxin.springbootinit.common.ResultUtils;
import com.yuxin.springbootinit.exception.BusinessException;
import com.yuxin.springbootinit.exception.ThrowUtils;
import com.yuxin.springbootinit.manager.AiManager;
import com.yuxin.springbootinit.manager.RedisLimiterManager;
import com.yuxin.springbootinit.mapper.ChartMapper;
import com.yuxin.springbootinit.model.dto.chart.GenChartByAiRequest;
import com.yuxin.springbootinit.model.entity.Chart;
import com.yuxin.springbootinit.model.entity.User;
import com.yuxin.springbootinit.model.enums.FileUploadBizEnum;
import com.yuxin.springbootinit.service.ChartService;
import com.yuxin.springbootinit.service.UserService;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yuxin.springbootinit.utils.DivideChartUtils;
import com.yuxin.springbootinit.utils.ExcelUtils;
import com.yuxin.springbootinit.utils.RetryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件接口
 *
*/
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private ChartService chartService;

    @Resource
    private ChartMapper chartMapper;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private MyMessageProducer myMessageProducer;



    /**
     * 智能分析
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<BiResponse> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                                 GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) throws IOException {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        ThrowUtils.throwIf(StringUtils.isEmpty(name), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isEmpty(goal), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isEmpty(chartType), ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(loginUser), ErrorCode.NOT_LOGIN_ERROR);

        RedisLimiterManager redisLimiterManager = new RedisLimiterManager();
        redisLimiterManager.doRateLimiter("genChartByAi_" + loginUser.getId().toString());

        Long ONE_MB = 1024 * 1024L;
        if (multipartFile.getSize() > ONE_MB) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "文件长度过长");
        }
        String[] validSuffix = {"xls", "xlsx", "xlsm", "xltx", "xltm", "csv", "pdf"};
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        boolean flag = false;
        for (String s : validSuffix) {
            if (s.equalsIgnoreCase(suffix)) {
                flag = true;
                break;
            }
        }
        ThrowUtils.throwIf(!flag, ErrorCode.PARAMS_ERROR, "文件后缀不符合条件");

        String data = ExcelUtils.ExcelToCsv(multipartFile);
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析目标:").append(goal).append("\n");
        userInput.append("图表类型:").append(goal).append("\n");
        userInput.append("数据:").append(data).append("\n");

        Chart chart = new Chart();
        chart.setGoal(goal);
        chart.setChartType(chartType);
        chart.setStatus("wait");
        chart.setUserId(loginUser.getId());
        chart.setUserRole(loginUser.getUserRole());
        chart.setName(name);

        boolean saveResult = this.chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR);

        DivideChartUtils divideChartUtils = new DivideChartUtils();
        divideChartUtils.divideChart(chart, data, chartMapper);

        CompletableFuture.runAsync(()->{
            Chart updateChart = new Chart();
            updateChart.setId(chart.getId());
            updateChart.setChartData(chart.getId().toString());
            updateChart.setStatus("running");
            Boolean save = null;

            RetryUtil.retry(save = this.chartService.updateById(updateChart), false);
            if (!save) {
                ErrorHandler(chart, "更新图表运行中状态失败");
            }

            StringBuilder stringBuilder = new StringBuilder();
            RetryUtil.retry(stringBuilder.append(AiManager.doChat(userInput.toString())).toString(), null);

            String result = stringBuilder.toString();

            String[] splits = result.split("【【【【【");
            if (splits.length < 3) {
                ErrorHandler(chart, "AI生成失败");
            }
            String genChartData = splits[1];
            genChartData.replace("\n", "").replace("\"","");
            String genChartResult = splits[2];

            Chart updateChartResult = new Chart();
            updateChartResult.setId(chart.getId());
            updateChartResult.setStatus("succeed");
            updateChartResult.setGenChart(genChartData);
            updateChartResult.setGenResult(genChartResult);

            RetryUtil.retry(save = this.chartService.updateById(updateChart), false);
            if (!save) {
                ErrorHandler(chart, "更新图表成功状态失败");
            }

        }, threadPoolExecutor);

        BiResponse biResponse = new BiResponse(chart.getId());
        return ResultUtils.success(biResponse);
    }

    /**
     * 智能分析(消息队列改造版)
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen/mq")
    public BaseResponse<BiResponse> genChartByAiMq(@RequestPart("file") MultipartFile multipartFile,
                                                 GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) throws IOException {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        ThrowUtils.throwIf(StringUtils.isEmpty(name), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isEmpty(goal), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isEmpty(chartType), ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(loginUser), ErrorCode.NOT_LOGIN_ERROR);

        RedisLimiterManager redisLimiterManager = new RedisLimiterManager();
        redisLimiterManager.doRateLimiter("genChartByAi_" + loginUser.getId().toString());

        Long ONE_MB = 1024 * 1024L;
        if (multipartFile.getSize() > ONE_MB) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "文件长度过长");
        }
        String[] validSuffix = {"xls", "xlsx", "xlsm", "xltx", "xltm", "csv", "pdf"};
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        boolean flag = false;
        for (String s : validSuffix) {
            if (s.equalsIgnoreCase(suffix)) {
                flag = true;
                break;
            }
        }
        ThrowUtils.throwIf(!flag, ErrorCode.PARAMS_ERROR, "文件后缀不符合条件");

        String data = ExcelUtils.ExcelToCsv(multipartFile);

        Chart chart = new Chart();
        chart.setGoal(goal);
        chart.setChartType(chartType);
        chart.setStatus("wait");
        chart.setUserId(loginUser.getId());
        chart.setUserRole(loginUser.getUserRole());
        chart.setName(name);

        boolean saveResult = this.chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR);

        DivideChartUtils divideChartUtils = new DivideChartUtils();
        divideChartUtils.divideChart(chart, data, chartMapper);
        divideChartUtils.queryData(chart,"1", "1", chartMapper);

        StringBuilder message = new StringBuilder();
        message.append(StringUtils.join(chart.getId(), "/")).append(data);
        myMessageProducer.sendMessage("code_exchange","my_routingKey", message.toString());

        BiResponse biResponse = new BiResponse(chart.getId());
        return ResultUtils.success(biResponse);
    }

    public void ErrorHandler(Chart chart, String execMessage) {
        Chart updateChart = new Chart();
        updateChart.setStatus("fail");
        updateChart.setId(chart.getId());
        updateChart.setExecMessage(execMessage);
        log.error("更新图表失败" + chart.getId() + "," + execMessage);
        boolean save = this.chartService.updateById(updateChart);
        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新图标失败状态失败");
        }
    }



    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }
}
