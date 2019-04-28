package it.wsh.cn.wshutilslib.rx;

import io.reactivex.disposables.Disposable;

/**
 * author:     xumin
 * date:       2018/5/4
 * email:      xumin2@evergrande.cn
 *
 * observeOn(Schedulers.newThread() 控制onComplete，onError和Onext当前所在线程
 * 整个事件传递过程中，catch了所有异常，最终将异常封账传递给了oError方法中。所有使用过程中，不会出现机器崩溃现象
 * subscribeOn(Schedulers.newThread())指定call（）方法在哪个线程运行，当指定了 订阅线程没有指定观察者执行事件响应线程，最终事件响应线程和订阅线程在一个线程中
 * onStart() 总是在 subscribe 所发生的线程中被调用，而不能指定线程，发生错误不会回调onError方法。他是整个时间传递之前产生的方法。
 */
public abstract class RxTaskCallBack<T> implements BaseRxTask<T> {
    private Disposable disposable;

    public RxTaskCallBack(){

    }
    @Override
    public void onStart(Disposable disposable) {
        //开始订阅，这里可以做取消操作，也可以做显示dialog操作（注意，这个函数是在订阅调用线程，确保是在主线程调用
        // 才可以显示dialog）
        this.disposable=disposable;
    }

    @Override
    public void onComplete() {
        //逻辑处理完成，可以取消dialog
        if(disposable!=null&&!disposable.isDisposed()){
            disposable.dispose();
        }
    }

    @Override
    public void onFailed(Throwable e) {
        //任务逻辑处理发生了异常
        //取消订阅
        if(disposable!=null&&!disposable.isDisposed()){
            disposable.dispose();
        }
    }

    @Override
    public void onSuccess(T t) {

    }

    /**
     * 取消任务，当页面关闭时，调用这个方法，防止内存泄露
     */
    public void cancel(){
        if(disposable!=null){
            disposable.dispose();
        }
    }
}
