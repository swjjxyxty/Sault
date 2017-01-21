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
        builder.append("********************Dump End*********************");


        return builder.toString();
    }
}
