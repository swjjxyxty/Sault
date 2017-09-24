package com.bestxty.sault;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author xty
 *         Created by xty on 2017/9/24.
 */
public class SaultCacheTest extends ApplicationTestCase {

    @Test
    public void createSaultOrGetFromCache() throws Exception {
        Sault sault = SaultCache.createSaultOrGetFromCache(new SaultConfiguration.Builder().build(), getContext());
        assertNotNull(sault);

        assertEquals(1, SaultCache.size());

        sault.close();
    }

    @Test
    public void release() throws Exception {
        Sault sault = SaultCache.createSaultOrGetFromCache(new SaultConfiguration.Builder().build(), getContext());
        assertNotNull(sault);

        assertEquals(1, SaultCache.size());

        sault.close();

        assertEquals(0, SaultCache.size());
    }

}