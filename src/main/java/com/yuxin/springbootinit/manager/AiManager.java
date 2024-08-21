package com.yuxin.springbootinit.manager;

import com.yuxin.springbootinit.common.ErrorCode;
import com.yuxin.springbootinit.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;

/**
 * packageName com.yuxin.springbootinit.manager
 * @author yuxin
 * @version JDK 8
 * @className AiManager (此处以class为例)
 * @date 2024/7/11
 */
public class AiManager {
    private static Long modelId = 1811304637730324482L;

    static String accessKey = "icitmm98m5fr0owiwdfw57rrdrxsxgjy";
    static String secretKey = "wgm6oqrjchbohln0n2kcfybqig8spey8";

    static YuCongMingClient client = new YuCongMingClient(accessKey, secretKey);
    public static String doChat(String message) {
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        System.out.println(response);
        if (response.getData() == null) {
            System.out.println("抛出异常");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return response.getData().getContent();
    }
}
