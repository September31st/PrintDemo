package com.print.enums;

/**
 * Created by mylo on 2017/7/5.
 * 状态码
 */

public enum StatusCode {
    OpenPortFailed,
    OpenPortSuccess,
    ClosePortFailed,
    ClosePortSuccess,
    WriteDataFailed,
    WriteDataSuccess,
    ReadDataSuccess,
    ReadDataFailed,
    UnknownError;

    StatusCode() {
    }
}
