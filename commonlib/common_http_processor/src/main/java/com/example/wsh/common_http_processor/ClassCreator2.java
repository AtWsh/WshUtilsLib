package com.example.wsh.common_http_processor;



import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * author: wenshenghui
 * created on: 2019/1/10 9:57
 * description: java class creator
 */
public class ClassCreator2 {
    private final static String TAB = "    ";
    private final static String CLASS_SUFFIX_NAME = "AsyncQuery";
    private final static String HTTP_BASE_REQUEST_PACKAGE_NAME = "it.wsh.cn.common_http.http.asyncquery";
    private final static String HTTP_BASE_REQUEST_CLASS_NAME = "HttpAsyncQuery";

    private String mReqBeanClassName;
    private String mRspBeanClassFullName;
    private String mRspBeanPackageName;
    private String mRspBeanSimpleName;
    private String mAsyncQueryClassName;
    private String mReqBeanClassPackageName;
    private TypeElement mTypeElement;
    private Messager mMessager;

    public ClassCreator2(Elements elementUtils, TypeElement classElement, Messager messager) {
        this.mTypeElement = classElement;
        mRspBeanClassFullName = AnnotationUtils.getAnnotationValueForClass(classElement, "resBean");
        initRspBeanClassSimlpleName();
        this.mMessager = messager;
        PackageElement packageElement = elementUtils.getPackageOf(mTypeElement);
        String packageName = packageElement.getQualifiedName().toString();
        mReqBeanClassName = mTypeElement.getSimpleName().toString();
        this.mReqBeanClassPackageName = packageName;
        messager.printMessage(Diagnostic.Kind.NOTE, mReqBeanClassPackageName);
        this.mAsyncQueryClassName = CLASS_SUFFIX_NAME;


        List<? extends Element> allMembers = elementUtils.getAllMembers(classElement);
        for (int i = 0; i < allMembers.size(); i++) {

            Element element = allMembers.get(i);
            String simpleName = element.getSimpleName().toString();
            System.out.printf("WSH_LOG ClassCreator2  simpleName = " + simpleName + "\n");
            ElementKind kind = element.getKind();
            System.out.printf("WSH_LOG ClassCreator2  kind = " + kind+ "\n");
            if (element.getKind() == ElementKind.FIELD) {
                System.out.printf("WSH_LOG ClassCreator2  hasField = " + "\n");
                VariableElement variableElement = (VariableElement) element;
                System.out.printf("WSH_LOG ClassCreator2  value = " + variableElement.toString() + "\n");
            }
        }
    }

    private void initRspBeanClassSimlpleName() {
        if (mRspBeanClassFullName != null) {
            int index = mRspBeanClassFullName.lastIndexOf(".");
            if (index > 0) {
                mRspBeanPackageName = mRspBeanClassFullName.substring(0, index);
                mRspBeanSimpleName = mRspBeanClassFullName.substring(index + 1);
            }
        }
    }


    //======================

    /**
     * rebuild httpReqInfo class
     * javapoet
     *
     * @return
     */
    public TypeSpec generateProxy() {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(mReqBeanClassName);
        classBuilder.addModifiers(Modifier.PUBLIC);
        classBuilder.addAnnotation(getAnnotationSpec());
        classBuilder.addType(getInnerType());
        return classBuilder.build();
    }

    public AnnotationSpec getAnnotationSpec() {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(HttpReqBean.class);
        builder.addMember("resBean", CodeBlock.builder().add(mRspBeanSimpleName + ".class").build());
        return builder.build();
    }


    /**
     * create inner class
     * javapoet
     *
     * @return
     */
    public TypeSpec getInnerType() {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(mAsyncQueryClassName);
        classBuilder.addJavadoc(" THIS CLASS WILL BE REMODIFIED BY common_http_processor WHEN YOU ADD OR REMOVE FIELD.  \n" +
                                "by : wenshenghui\n")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(HTTP_BASE_REQUEST_PACKAGE_NAME, HTTP_BASE_REQUEST_CLASS_NAME),
                        TypeVariableName.get(mReqBeanClassName), ClassName.get(mRspBeanPackageName, mRspBeanSimpleName)));

        List<? extends Element> elements = mTypeElement.getEnclosedElements();
        List<String> bodyKeys = new ArrayList<>(5);
        List<String> paramKeys = new ArrayList<>(5);
        List<String> headerKeys = new ArrayList<>(5);
        for (Element element : elements) {
            if (element.getKind() == ElementKind.FIELD) {
                VariableElement variableElement = (VariableElement) element;
                String methodName = variableElement.getSimpleName().toString();
                mMessager.printMessage(Diagnostic.Kind.NOTE, "variableElement " + methodName);
                //找到被HttpBody注解的值
                HttpBody httpBody = variableElement.getAnnotation(HttpBody.class);
                if (httpBody != null) {
                    bodyKeys.add(methodName);
                }
                //找到被HttpParam注解的函数
                HttpParam httpParam = variableElement.getAnnotation(HttpParam.class);
                if (httpParam != null) {
                    paramKeys.add(methodName);
                }

                //找到被HttpParam注解的函数
                HttpHeader httpHeader = variableElement.getAnnotation(HttpHeader.class);
                if (httpHeader != null) {
                    headerKeys.add(methodName);
                }
            }
        }
        classBuilder.addMethod(generatePrepareDataMethod(bodyKeys, paramKeys, headerKeys));
        return classBuilder.build();
    }

    private MethodSpec generatePrepareDataMethod(List<String> bodyKeys, List<String> paramKeys, List<String> headerKeys) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("prepareData")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(void.class)
                .addParameter(ClassName.get(mReqBeanClassPackageName, mReqBeanClassName), "req");

        methodBuilder.addCode(TAB + "if (req == null) {\n" +
                              TAB + TAB + "return;\n" +
                              TAB + "}\n");

        methodBuilder.addCode("\n");

        methodBuilder.addStatement(TAB + "mBaseUrl = req.getBaseUrl();");
        methodBuilder.addStatement(TAB + "mPath = req.getPath();");
        methodBuilder.addStatement(TAB + "mHttpMethod = req.getMethod();");

        methodBuilder.addStatement("\n" + "$T<$T, $T> bodyMap = new $T<>();", Map.class, String.class, Object.class, HashMap.class);
        methodBuilder.addStatement(TAB + "$T<$T, $T> headerMap = new $T<>();", Map.class, String.class, String.class, HashMap.class);
        methodBuilder.addStatement(TAB + "$T<$T, $T> paramsMap = new $T<>();", Map.class, String.class, String.class, HashMap.class);

        methodBuilder.addCode("\n");

        if (bodyKeys != null && bodyKeys.size() > 0) {
            for (String bodyKey : bodyKeys) {
                methodBuilder.addCode(TAB + "bodyMap.put(\"" + bodyKey +"\", req." + bodyKey + ");\n");
            }
        }

        if (paramKeys != null && paramKeys.size() > 0) {
            for (String paramKey : paramKeys) {
                methodBuilder.addCode(TAB + "bodyMap.put(\"" + paramKey +"\", req." + paramKey + ");\n");
            }
        }

        if (headerKeys != null && headerKeys.size() > 0) {
            for (String headerKey : headerKeys) {
                methodBuilder.addCode(TAB + "bodyMap.put(\"" + headerKey +"\", req." + headerKey + ");\n");
            }
        }

        methodBuilder.addCode("\n" + TAB + "addBodyObj(bodyMap);\n" +
                TAB + "addParamsMap(headerMap);\n" +
                TAB + "addParamsMap(paramsMap);\n");

        methodBuilder.addCode("\n");

        MethodSpec methodSpec = methodBuilder.build();
        return methodSpec;
    }

    public String getPackageName() {
        return mReqBeanClassPackageName;
    }
}

