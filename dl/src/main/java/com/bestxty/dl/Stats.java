package com.bestxty.dl;

/**
 * @author xty
 *         Created by xty on 2016/12/9.
 */
class Stats {
    int hunterMapSize;

    int pausedTaskSize;
    int pausedTagSize;
    int failedTaskSize;
    int batchSize;
    int taskSize;

    int activeCount;
    long taskCount;
    long completeTaskCount;

    int corePoolSize;
    int largestPoolSize;
    int maximumPoolSize;
    int poolSize;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("********************Dump Start*******************").append("\n");
        builder.append("\t\t\t\t\t").append("hunter count=").append(hunterMapSize).append("\n");
        builder.append("\t\t\t\t\t").append("paused task count=").append(pausedTaskSize).append("\n");
        builder.append("\t\t\t\t\t").append("paused tag count=").append(pausedTagSize).append("\n");
        builder.append("\t\t\t\t\t").append("failed task count=").append(failedTaskSize).append("\n");
        builder.append("\t\t\t\t\t").append("batch count=").append(batchSize).append("\n");
        builder.append("\t\t\t\t\t").append("task count=").append(taskSize).append("\n");
        builder.append("\t\t\t\t\t").append("active count=").append(activeCount).append("\n");
        builder.append("\t\t\t\t\t").append("task count=").append(taskCount).append("\n");
        builder.append("\t\t\t\t\t").append("complete task count=").append(completeTaskCount).append("\n");
        builder.append("\t\t\t\t\t").append("core pool size=").append(corePoolSize).append("\n");
        builder.append("\t\t\t\t\t").append("largest pool size=").append(largestPoolSize).append("\n");
        builder.append("\t\t\t\t\t").append("maximum pool size=").append(maximumPoolSize).append("\n");
        builder.append("\t\t\t\t\t").append("pool size=").append(poolSize).append("\n");
        builder.append("********************Dump End*********************");


        return builder.toString();
    }
}
