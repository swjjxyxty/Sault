package org.mockito.configuration;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/18.
 */

public class MockitoConfiguration extends DefaultMockitoConfiguration {

    @Override
    public boolean enableClassCache() {
        return false;
    }
}
