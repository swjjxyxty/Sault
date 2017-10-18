package com.bestxty.sault.internal.hunter;

import com.bestxty.sault.internal.task.PartedSaultTask;
import com.bestxty.sault.internal.task.SaultTask;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/18.
 */

public final class TaskHunterFactory {
    private TaskHunterFactory() {
    }

    public static TaskHunter newTaskHunter(SaultTask task) {
        if (task instanceof PartedSaultTask) {
            return new DefaultSaultTaskHunter(((PartedSaultTask) task));
        }
        return new PartingSaultTaskHunter(task);
    }

    public static boolean isPartingSaultTaskHunter(TaskHunter taskHunter) {
        return taskHunter instanceof PartingSaultTaskHunter;
    }
}
