package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * packageName com.yupi.springbootinit.bizmq
 *
 * @author yuxin
 * @version JDK 8
 * @className MyMessageConsumer (此处以class为例)
 * @date 2024/7/21
 * @description 消费者*/
@Component
@Slf4j
public class MyMessageConsumer {
    @Resource
    private ChartService chartService;

    @SneakyThrows
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        // 拆解消息
        String[] strings = message.split("/");
        Long id = Long.parseLong(strings[0]);
        Chart chart = chartService.getById(id);
        String goal = chart.getGoal();

        // 构造输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析目标:").append(goal).append("\n");
        userInput.append("图表类型:").append(goal).append("\n");
        userInput.append("数据:").append(strings[1]).append("\n");

        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setChartData(chart.getId().toString());
        updateChart.setStatus("running");
        boolean save = this.chartService.updateById(updateChart);
        if (!save) {
            ErrorHandler(chart, "更新图表运行中状态失败");
        }

        String result = AiManager.doChat(userInput.toString());
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
        save = this.chartService.updateById(updateChartResult);
        if (!save) {
            ErrorHandler(chart, "更新图表成功状态失败");
        }
        log.info("receiveMessage message = {}", message);
        channel.basicAck(deliveryTag, false);
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
}
