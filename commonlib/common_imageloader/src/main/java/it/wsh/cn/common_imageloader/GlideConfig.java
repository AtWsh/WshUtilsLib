package it.wsh.cn.common_imageloader;

import com.bumptech.glide.load.model.ModelLoaderFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * author: wenshenghui
 * created on: 2018/12/10 14:53
 * description:
 */
public class GlideConfig {

    private static List<ModelLoaderFactory> sFactories = new ArrayList<>();
    public static void addModelLoaderFactory(ModelLoaderFactory factory) {
        sFactories.add(factory);
    }

    public static List<ModelLoaderFactory> getModelLoaderFactories() {
        return sFactories;
    }
}
