package com.bestxty.sunshine.core;

import com.bestxty.sunshine.annotation.Autowired;
import com.bestxty.sunshine.annotation.Component;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/13.
 */
@AutoService(Processor.class)
public class AutowiredAnnotationProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new HashSet<>(2);
        supportedAnnotationTypes.add(Component.class.getCanonicalName());
        supportedAnnotationTypes.add(Autowired.class.getCanonicalName());
        return supportedAnnotationTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> components
                = roundEnvironment.getElementsAnnotatedWith(Component.class);

        for (Element component : components) {
            if (component.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "....");
            }
              /*生成方法*/
            MethodSpec creaedMethod = MethodSpec.methodBuilder("createApt")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(String[].class, "parameters")
                    .addStatement("System.out.println($S)", "this`s java source is created by dynamic")
                    .build();


            TypeSpec createdClass = TypeSpec.classBuilder("AptGenerator")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(creaedMethod)
                    .build();//指定生成的类
            JavaFile javaFile = JavaFile.builder("com.coca.apt", createdClass).build();

            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
