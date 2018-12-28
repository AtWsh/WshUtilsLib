package it.wsh.cn.componentbase;

import it.wsh.cn.componentbase.emptyservices.EmptyAccountService;
import it.wsh.cn.componentbase.services.IAccountService;

/**
 * author: wenshenghui
 * created on: 2018/8/10 16:41
 * description:
 */
public class ServiceFactory {

    private IAccountService mIAccountService;

    private ServiceFactory() {}

    private volatile static ServiceFactory sInstance = null;

    public static ServiceFactory getInstance() {
        if (sInstance == null) {
            synchronized (ServiceFactory.class) {
                if (sInstance == null) {
                    sInstance = new ServiceFactory();
                }
            }
        }
        return sInstance;
    }

    public void setAccountService(IAccountService service) {
        mIAccountService = service;
    }

    public IAccountService getAccountService() {
        if (mIAccountService == null) {
            mIAccountService = new EmptyAccountService();
        }
        return mIAccountService;
    }
}
