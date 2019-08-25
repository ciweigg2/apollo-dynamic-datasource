package com.example.apollodynamicdatasource;

import lombok.Data;

import java.io.Serializable;

/**
 * @NAME CommonResult
 * @USER Ciwei
 * @DATE 2019/8/25/025 18:44
 **/
@Data
public final class ResponseMessage<T> implements Serializable {

    private String code;

    private String msg;

    public T data;

    private static final String CODE_SUCCESS = "200";

    private static final String CODE_FAIL = "500";

    private static final String MSG_SUCCESS = "success";

    private static final String MSG_FAIL = "failed";

    public ResponseMessage() {
    }

    public ResponseMessage(String code) {
        this.code = code;
    }

    public ResponseMessage(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseMessage(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseMessage(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResponseMessage success() {
        return new ResponseMessage(CODE_SUCCESS, MSG_SUCCESS);
    }

    public static ResponseMessage success(Object data) {
        return new ResponseMessage(CODE_SUCCESS, MSG_SUCCESS, data);
    }

    public static ResponseMessage fail() {
        return new ResponseMessage(CODE_FAIL, MSG_FAIL);
    }

}
