package com.netease.mini.bietuola.web.util;

/**
 * @Description 返回码，用于描述通用的错误类型，需要和前端保持一致，后期再不断添加
 * @Auther ctl
 * @Date 2019/4/28
 */
public interface ResultCode {

    /** 操作成功 */
    int SUCCESS = 0;

    /** 未知错误/服务器异常 */
    int ERROR_UNKNOWN = -1;

    /** 未登录 */
    int ERROR_NOT_LOGGED_IN = -2;

    /** 不允许重复操作 */
    int ERROR_ACTION_REPEATED = -3;

    /** 缺少参数 */
    int ERROR_PARAMETER_MISSING = -4;

    int ERROR_FORBIDDEN = -5;

    /** 参数格式错误 */
    int ERROR_BAD_PARAMETER = -6;

    int ERROR_CHECK_RECORD_FAIL = -7;

    /** 待续 **/
    /** 验证码校验失败 */
    int VERIFY_CODE_FAIL = -1001;

    /** 服务抛异常，全局处理状态 */
    int ERROR_EXCEPTION_GLOBAL = -9001;

}
