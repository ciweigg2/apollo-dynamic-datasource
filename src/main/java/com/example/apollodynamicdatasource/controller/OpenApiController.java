package com.example.apollodynamicdatasource.controller;

import com.alibaba.fastjson.JSON;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.NamespaceGrayDelReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenReleaseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Ciwei
 * @Date 2019/8/10/010
 */
@RestController
public class OpenApiController {

    @Autowired
    private ApolloOpenApiClient client;

    @Value("${app.id}")
    private String appId;

    @GetMapping("/envclusters")
    public Object getEnvclusters() {
        return JSON.toJSONString(client.getEnvClusterInfo(appId));
    }

    //新增配置
    @PostMapping("/add")
    public Object addParam() {
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey("timeout");
        openItemDTO.setValue("100");
        openItemDTO.setComment("超时时间");
        openItemDTO.setDataChangeCreatedBy("apollo");
        OpenItemDTO item = client.createItem(appId, "DEV", "default", "application", openItemDTO);
        return JSON.toJSONString(item);
    }

    //修改配置
    @PostMapping("/update")
    public Object updateParam() {
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey("timeout");
        openItemDTO.setValue("200");
        openItemDTO.setComment("超时时间");
        openItemDTO.setDataChangeCreatedBy("apollo");
        client.createOrUpdateItem(appId, "DEV", "default", "application", openItemDTO);
        return JSON.toJSONString(openItemDTO);
    }

    //删除配置
    @PostMapping("/removeItem")
    public ResponseEntity removeItem() {
        String key = "timeout";
        client.removeItem(appId, "DEV", "default", "application", key ,"apollo");
        return ResponseEntity.ok("删除成功");
    }

    //发布配置
    @PostMapping("/release")
    public Object releaseParam() {
        NamespaceGrayDelReleaseDTO namespaceGrayDelReleaseDTO = new NamespaceGrayDelReleaseDTO();
        namespaceGrayDelReleaseDTO.setReleaseTitle("2019-03-27 17:38 release");
        namespaceGrayDelReleaseDTO.setReleaseComment("test release");
        namespaceGrayDelReleaseDTO.setReleasedBy("apollo");
        OpenReleaseDTO openReleaseDTO = client.publishNamespace(appId, "DEV", "default", "application", namespaceGrayDelReleaseDTO);
        return JSON.toJSONString(openReleaseDTO);
    }

    //获取名称空间下所有的配置
    @GetMapping("/namespace")
    public Object getAllNameSpace() {
        return JSON.toJSONString(client.getNamespace(appId, "DEV", "default", "application"));
    }

}
