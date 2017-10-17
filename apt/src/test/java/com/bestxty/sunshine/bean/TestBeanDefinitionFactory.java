package com.bestxty.sunshine.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

public class TestBeanDefinitionFactory implements BeanDefinitionFactory {

    private Map<String, BeanDefinition> definitionMap = new HashMap<>();

    public TestBeanDefinitionFactory() {
        TestBeanDefinition object1 = new TestBeanDefinition();
        object1.setName("object1");
        object1.setClassName(Object1.class.getName());
        object1.setSingleton(true);

        TestBeanDefinition object2 = new TestBeanDefinition();
        object2.setName("object2");
        object2.setClassName(Object2.class.getName());
        object2.setSingleton(true);
        List<ConstructorParameter> object2ConstructorParameters = new ArrayList<>();
        object2ConstructorParameters.add(new ConstructorParameter("object2Name", String.class.getName()));
        object2ConstructorParameters.add(new ConstructorParameter(Integer.class.getName(), Integer.class.getName()));
        object2ConstructorParameters.add(new ConstructorParameter("object1", Object1.class.getName()));
        object2.setConstructorParameters(object2ConstructorParameters);

        TestBeanDefinition object3 = new TestBeanDefinition();
        object3.setName("object3");
        object3.setClassName(Object3.class.getName());
        object3.setSingleton(true);
        List<InjectField> injectFields = new ArrayList<>();
        injectFields.add(new InjectField("name", "object3Name", String.class.getName()));
        injectFields.add(new InjectField("age", Integer.class.getName(), Integer.class.getName()));
        injectFields.add(new InjectField("object1", "object1", String.class.getName()));
        object3.setInjectFields(injectFields);

        TestBeanDefinition object4 = new TestBeanDefinition();
        object4.setName("object4");
        object4.setClassName(Object4.class.getName());
        object4.setSingleton(true);

        TestBeanDefinition object2Name = new TestBeanDefinition();
        object2Name.setName("object2Name");
        object2Name.setInitMethod("object2Name");
        object2Name.setClassName(String.class.getName());
        object2Name.setSingleton(true);
        object2Name.setParentName("object4");

        TestBeanDefinition object3Name = new TestBeanDefinition();
        object3Name.setName("object3Name");
        object3Name.setInitMethod("object3Name");
        object3Name.setClassName(String.class.getName());
        object3Name.setSingleton(true);
        object3Name.setParentName("object4");

        TestBeanDefinition objectAge = new TestBeanDefinition();
        objectAge.setName(Integer.class.getName());
        objectAge.setInitMethod("objectAge");
        objectAge.setClassName(Integer.class.getName());
        objectAge.setSingleton(true);
        objectAge.setParentName("object4");

        definitionMap.put(object1.getId(), object1);
        definitionMap.put(object2.getId(), object2);
        definitionMap.put(object3.getId(), object3);
        definitionMap.put(object4.getId(), object4);
        definitionMap.put(object2Name.getId(), object2Name);
        definitionMap.put(object3Name.getId(), object3Name);
        definitionMap.put(objectAge.getId(), objectAge);
        System.out.println("definitionMap = " + definitionMap.keySet());
    }

    @Override
    public BeanDefinition getBeanDefinition(String id) {
        System.out.println("id = " + id);
        return definitionMap.get(id);
    }
}
