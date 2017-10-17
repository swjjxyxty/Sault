package com.bestxty.sunshine.bean;

import com.bestxty.sunshine.annotation.Bean;
import com.bestxty.sunshine.annotation.Component;

import javax.inject.Named;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */
@Component
public class Object4 {

    @Named("object2Name")
    @Bean
    public String object2Name() {
        return "object2Name";
    }

    @Named("object3Name")
    @Bean
    public String object3Name() {
        return "object3Name";
    }

    @Bean
    public Integer objectAge() {
        return 10;
    }
}
