package com.example.wsh.common_http_processor;

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * author: wenshenghui
 * created on: 2019/1/11 9:57
 * description:
 */
public class AnnotationUtils {

    /**
     * 在编译期获取注解当中Class类型的值
     * @param element
     * @param key
     * @return
     */
    public static String  getAnnotationValueForClass(TypeElement element, String key) {
        AnnotationMirror annotationMirror = getAnnotationMirror(element, HttpReqBean.class);
        AnnotationValue annotationValue = getAnnotationValue(annotationMirror, key);

        System.out.printf("WSH_LOG annotationValue" + annotationValue + "\n");
        TypeMirror typeMirror = (TypeMirror) annotationValue.getValue();
        System.out.printf("WSH_LOG typeMirror" + typeMirror + "\n");

        String className = typeMirror.toString();
        System.out.printf("WSH_LOG className" +  className + "\n");
        return className;
    }

    public static AnnotationMirror getAnnotationMirror(TypeElement element, Class<?> clazz) {
        String clazzName = clazz.getName();
        for(AnnotationMirror m : element.getAnnotationMirrors()) {
            if(m.getAnnotationType().toString().equals(clazzName)) {
                return m;
            }
        }
        return null;
    }

    public static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for(Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet() ) {
            if(entry.getKey().getSimpleName().toString().equals(key)) {
                AnnotationValue value = entry.getValue();
                return value;
            }
        }
        return null;
    }
}
