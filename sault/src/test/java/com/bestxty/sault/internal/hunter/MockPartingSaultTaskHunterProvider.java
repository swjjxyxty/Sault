package com.bestxty.sault.internal.hunter;

import com.bestxty.sault.internal.task.SaultTask;

import org.mockito.Mockito;

import java.util.concurrent.Future;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/18.
 */

public class MockPartingSaultTaskHunterProvider {

    public static class InternalPartingSaultTaskHunter extends PartingSaultTaskHunter {

        InternalPartingSaultTaskHunter(SaultTask task) {
            super(task);
        }

        @Override
        public void setFuture(Future<?> future) {
            super.setFuture(future);
        }
    }

    public InternalPartingSaultTaskHunter mockPartingSaultTaskHunter() {
        return Mockito.mock(InternalPartingSaultTaskHunter.class);
    }
}
