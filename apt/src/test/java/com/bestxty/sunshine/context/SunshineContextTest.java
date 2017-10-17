package com.bestxty.sunshine.context;

import com.bestxty.sunshine.bean.Object1;
import com.bestxty.sunshine.bean.Object2;
import com.bestxty.sunshine.bean.Object3;
import com.bestxty.sunshine.bean.Object4;
import com.bestxty.sunshine.bean.TestBeanDefinitionFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */
public class SunshineContextTest {

    private SunshineContext context;

    @org.junit.Before
    public void setUp() throws Exception {
        context = new DefaultSunshineContext(new TestBeanDefinitionFactory());
    }

    @org.junit.Test
    public void getBean() throws Exception {
        Object1 object1 = (Object1) context.getBean("object1");
        assertNotNull(object1);
        Object2 object2 = (Object2) context.getBean("object2");
        assertNotNull(object2);
        Object3 object3 = (Object3) context.getBean("object3");
        assertNotNull(object3);
        Object4 object4 = (Object4) context.getBean("object4");
        assertNotNull(object4);

        assertEquals(object1, object2.getObject1());
        assertEquals(10, object2.getAge());
        assertEquals("object2Name", object2.getName());


        assertEquals(object1, object3.getObject1());
        assertEquals(10, object3.getAge());
        assertEquals("object3Name", object3.getName());



    }

}