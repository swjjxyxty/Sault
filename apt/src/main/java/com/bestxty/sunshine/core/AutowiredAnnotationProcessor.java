//package com.bestxty.sunshine.core;
//
//import com.bestxty.sunshine.annotation.Autowired;
//import com.bestxty.sunshine.annotation.Bean;
//import com.bestxty.sunshine.annotation.Component;
//import com.bestxty.sunshine.bean.DefaultBeanDefinition;
//import com.google.auto.service.AutoService;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import javax.annotation.processing.AbstractProcessor;
//import javax.annotation.processing.Messager;
//import javax.annotation.processing.Processor;
//import javax.annotation.processing.RoundEnvironment;
//import javax.lang.model.SourceVersion;
//import javax.lang.model.element.Element;
//import javax.lang.model.element.ElementKind;
//import javax.lang.model.element.ExecutableElement;
//import javax.lang.model.element.TypeElement;
//import javax.tools.Diagnostic;
//
///**
// * @author 姜泰阳
// *         Created by 姜泰阳 on 2017/10/13.
// */
//@AutoService(Processor.class)
//public class AutowiredAnnotationProcessor extends AbstractProcessor {
//
//
//    @Override
//    public SourceVersion getSupportedSourceVersion() {
//        return SourceVersion.RELEASE_7;
//    }
//
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        Set<String> supportedAnnotationTypes = new HashSet<>(2);
//        supportedAnnotationTypes.add(Component.class.getCanonicalName());
//        supportedAnnotationTypes.add(Autowired.class.getCanonicalName());
//        return supportedAnnotationTypes;
//    }
//
//    @Override
//    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
//
//        List<DefaultBeanDefinition> beanDefinitions = new ArrayList<>();
//
//        Messager messager = processingEnv.getMessager();
//        Set<? extends Element> components
//                = env.getElementsAnnotatedWith(Component.class);
//        for (Element component : components) {
//            messager.printMessage(Diagnostic.Kind.NOTE, "Printing: " + component.toString());
//            if (component.getKind() != ElementKind.CLASS) {
//                messager.printMessage(Diagnostic.Kind.ERROR,
//                        "只支持类文件.");
//            }
//            Component annotation = component.getAnnotation(Component.class);
//            messager.printMessage(Diagnostic.Kind.NOTE, "annotation.value() = " + annotation.value());
//            TypeElement typeElement = ((TypeElement) component);
//            DefaultBeanDefinition beanDefinition =
//                    new DefaultBeanDefinition(typeElement.getQualifiedName().toString());
//            beanDefinition.setName(annotation.value());
//            beanDefinitions.add(beanDefinition);
//        }
//        Set<? extends Element> beans
//                = env.getElementsAnnotatedWith(Bean.class);
//        for (Element bean : beans) {
//            messager.printMessage(Diagnostic.Kind.NOTE, "Printing: " + bean.toString());
//            if (bean.getKind() != ElementKind.METHOD) {
//                messager.printMessage(Diagnostic.Kind.ERROR,
//                        "只支持方法.");
//            }
//            Bean annotation = bean.getAnnotation(Bean.class);
//            ExecutableElement executableElement = ((ExecutableElement) bean);
//            String returnType =
//                    ((TypeElement) executableElement.getReturnType()).getQualifiedName().toString();
//            DefaultBeanDefinition beanDefinition =
//                    new DefaultBeanDefinition(returnType);
//            beanDefinition.setName(annotation.value());
//            beanDefinitions.add(beanDefinition);
//        }
//
//        Set<? extends Element> autoWireds
//                = env.getElementsAnnotatedWith(Autowired.class);
//        for (Element autoWire : autoWireds) {
//            messager.printMessage(Diagnostic.Kind.NOTE, "Printing: " + autoWire.toString());
//            ElementKind kind = autoWire.getKind();
//            switch (kind) {
//                case CONSTRUCTOR: {
////                    TypeElement typeElement = ((TypeElement) autoWire.getEnclosingElement());
//
//                    break;
//                }
//                case FIELD:
//                    break;
//                case METHOD:
//                    break;
//                default:
//                    messager.printMessage(Diagnostic.Kind.ERROR,
//                            "只支持构造器,字段和方法.");
//                    break;
//            }
//        }
//
//
////        for (Element component : components) {
////            if (component.getKind() != ElementKind.CLASS) {
////                messager.printMessage(Diagnostic.Kind.ERROR,
////                        "....");
////            }
////            /*生成方法*/
////            MethodSpec creaedMethod = MethodSpec.methodBuilder("createApt")
////                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
////                    .returns(void.class)
////                    .addParameter(String[].class, "parameters")
////                    .addStatement("System.out.println($S)", "this`s java source is created by dynamic")
////                    .build();
////
////
////            TypeSpec createdClass = TypeSpec.classBuilder("AptGenerator")
////                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
////                    .addMethod(creaedMethod)
////                    .build();//指定生成的类
////            JavaFile javaFile = JavaFile.builder("com.coca.apt", createdClass).build();
////
////            try {
////                javaFile.writeTo(processingEnv.getFiler());
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
//
//        return false;
//    }
//}
