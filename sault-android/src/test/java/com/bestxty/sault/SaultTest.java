package com.bestxty.sault;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;


public class SaultTest extends ApplicationTestCase {

    @Test
    public void getDefaultInstanceWithOutDefaultConfiguration() throws Exception {
        try {
            Sault.getInstance(getContext());
        } catch (NullPointerException ex) {
            assertEquals("No default SaultConfiguration was found. Call setDefaultConfiguration() first.", ex.getMessage());
        }
    }

    @Test
    public void getDefaultInstanceWithDefaultConfiguration() throws Exception {
        Sault.setDefaultConfiguration(new SaultConfiguration.Builder().build());
        Sault sault = Sault.getInstance(getContext());
        assertNotNull(sault);
    }

    @Test
    public void getInstanceWithConfiguration() throws Exception {
        Sault.setDefaultConfiguration(new SaultConfiguration.Builder().build());
        Sault defaultInstance = Sault.getInstance(getContext());
        assertNotNull(defaultInstance);
        Sault sault = Sault.getInstance(new SaultConfiguration.Builder().build(), getContext());
        assertNotEquals(defaultInstance, sault);
    }
}