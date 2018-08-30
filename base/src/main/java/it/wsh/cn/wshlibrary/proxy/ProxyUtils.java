package it.wsh.cn.wshlibrary.proxy;


import java.lang.reflect.Proxy;

/**
 * 动态代理工具
 * 1. 提供ProxyInterceptor接口，在实际操作执行执行的操作，返回值表示是否拦截
 * 2. weakRef表示对object的缓存是否需要采用弱引用方式
 * 3. postUI表示执行方法，是否需要在UI线程中执行
 * 4. 操作示例: response = ProxyUtils.getUIProxy(response);  表示response的所有方法都执行在UI线程中
 */
public class ProxyUtils {

    public static <T> T getProxy(Object object, Class<?>[] intfs, ProxyInterceptor interceptor, boolean weakRef, boolean postUI) {
        return (T) Proxy.newProxyInstance(object.getClass().getClassLoader(),
                intfs, new ProxyInvocationHandler(object, interceptor, weakRef, postUI));
    }

    public static <T> T getProxy(Object object, Class<?> clazz, ProxyInterceptor interceptor, boolean weakRef, boolean postUI) {
        return getProxy(object, new Class<?>[] { clazz }, interceptor, weakRef, postUI);
    }

    public static <T> T getProxy(Object object, Class<?> clazz, ProxyInterceptor interceptor) {
        return (T) getProxy(object, clazz, interceptor, false, false);
    }

    public static <T> T getProxy(Object object, ProxyInterceptor interceptor) {
        return (T) getUIProxy(object, object.getClass().getInterfaces(), interceptor);
    }

    public static <T> T getWeakUIProxy(Object object, Class<?> clazz) {
        return (T) getProxy(object, clazz, null, true, true);
    }

    public static <T> T getUIProxy(Object object) {
        return (T) getUIProxy(object, object.getClass().getInterfaces(), null);
    }

    public static <T> T getUIProxy(Object object, Class<?> clazz) {
        return (T) getUIProxy(object, new Class<?>[] { clazz }, null);
    }

    public static <T> T getUIProxy(Object object, Class<?> clazz, ProxyInterceptor interceptor) {
        return (T) getUIProxy(object, new Class<?>[] { clazz }, interceptor);
    }

    public static <T> T getUIProxy(Object object, Class<?>[] intfs, ProxyInterceptor interceptor) {
        return (T) getProxy(object, intfs, interceptor, false, true);
    }
}
