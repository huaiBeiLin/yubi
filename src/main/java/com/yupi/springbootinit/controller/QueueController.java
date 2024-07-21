package com.yupi.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * packageName com.yupi.springbootinit.controller
 *
 * @author yuxin
 * @version JDK 8
 * @className QueueController (此处以class为例)
 * @date 2024/7/18
 * @description 线程池测试
 */

@RestController
@RequestMapping("/file")
@Slf4j
@Profile({"dev", "local"})
public class QueueController {
    @Resource
    ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("/add")
    public void add(String name) {
        CompletableFuture.runAsync(()->{
            System.out.println("任务执行中：" + name + " 执行人" + Thread.currentThread().getName());
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, threadPoolExecutor);
    }

    @GetMapping("/get")
    public String get() {
        Map<String , Object> map = new HashMap<>();
        int size = threadPoolExecutor.getQueue().size();
        map.put("队列长度", size);
        long taskCount = threadPoolExecutor.getTaskCount();
        map.put("执行任务数",taskCount);
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        map.put("已完成任务数",completedTaskCount);
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("活跃线程数", activeCount);
        return JSONUtil.toJsonStr(map);
    }
}
