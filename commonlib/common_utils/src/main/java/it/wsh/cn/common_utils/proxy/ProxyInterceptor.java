package it.wsh.cn.common_utils.proxy;

import java.lang.reflect.Method;

public interface ProxyInterceptor {
    boolean onIntercept(Object object, Method method, Object[] args);
}
