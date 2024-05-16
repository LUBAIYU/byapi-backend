package com.example.common.constant;

/**
 * 通用常量
 *
 * @author by
 */
public interface CommonConsts {

    String IMAGE_UPLOAD_ERROR = "文件上传错误！";
    String IMAGE_FORMAT_ERROR = "文件格式错误！";
    String IMAGE_READ_ERROR = "文件读取失败！";
    String PAGE_PARAMS_ERROR = "分页参数异常！";
    String EXIST_ERROR = "已开通接口调用权限！无需再次开通！";
    String BODY_KEY = "body-key";
    String SDK_DOWNLOAD_ERROR = "SDK下载失败！";
    String GET_INTERFACE_BY_ID_KEY = "byapi:server:getInterfaceById:%s:%s";
    String LIST_INVOKE_RECORDS_KEY = "byapi:server:listInvokeRecords:%s";
    Integer TIME_GAP = 10 * 60 * 1000;
    String NONCE_KEY = "byapi:gateway:filter:nonce";
    String CACHE_SET_ERROR = "缓存设置失败！";
    String CACHE_DEL_ERROR = "缓存删除失败！";
}
