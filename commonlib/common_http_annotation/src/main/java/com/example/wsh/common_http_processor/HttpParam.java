package com.example.wsh.common_http_processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author: wenshenghui
 * created on: 2019/1/8 11:44
 * description:
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface HttpParam {
}
