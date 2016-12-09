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
        builder.append("\t\t\t\t\t").append("hunterMapSize=").append(hunterMapSize).append("\n");
        builder.append("\t\t\t\t\t").append("pausedTaskSize=").append(pausedTaskSize).append("\n");
        builder.append("\t\t\t\t\t").append("pausedTagSize=").append(pausedTagSize).append("\n");
        builder.append("\t\t\t\t\t").append("failedTaskSize=").append(failedTaskSize).append("\n");
        builder.append("\t\t\t\t\t").append("batchSize=").append(batchSize).append("\n");
        builder.append("\t\t\t\t\t").append("taskSize=").append(taskSize).append("\n");
        builder.append("********************Dump End*********************");


        return builder.toString();
    }
}
