package com.bingo.framework.rpc.service;

/**
 * 通用服务接口
 * 
 * @author william.liangf
 * @export
 */
public interface GenericService {

    /**
     * 泛化调用
     * 
     * @param method 方法名，如：findPerson，如果有重载方法，需带上参数列表，如：findPerson(java.lang.String)
     * @param parameterTypes 参数类型
     * @param args 参数列表
     * @return 返回值
     * @throws Throwable 方法抛出的异常
     */
    Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException;

}