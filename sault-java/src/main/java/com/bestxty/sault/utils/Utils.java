package com.bestxty.sault.utils;

import com.bestxty.sault.Log;
import com.bestxty.sault.SplitTask;
import com.bestxty.sault.Task;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * @author xty
 *         Created by xty on 2017/10/5.
 */
public final class Utils {

    private static final String TAG = "Sault";
    public static final String THREAD_PREFIX = "Sault-";
    public static final String DISPATCHER_THREAD_NAME = THREAD_PREFIX + "Dispatcher";
    public static final String THREAD_IDLE_NAME = THREAD_PREFIX + "Idle";


    private static Log log = new Log() {
        @Override
        public void log(String message) {
            System.out.println(message);
        }

        @Override
        public void error(String message, Throwable ex) {
            System.err.println(message + getStackTraceString(ex));
        }

        private String getStackTraceString(Throwable tr) {
            if (tr == null) {
                return "";
            }

            // This is to reduce the amount of log spew that apps do in the non-error
            // condition of the network being unavailable.
            Throwable t = tr;
            while (t != null) {
                if (t instanceof UnknownHostException) {
                    return "";
                }
                t = t.getCause();
            }

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, false);
            tr.printStackTrace(pw);
            pw.flush();
            return sw.toString();
        }
    };

    public static void setLog(Log log) {
        Utils.log = log;
    }

    public static void log(String message) {
        log.log(message);
    }

    public static void error(String message, Throwable ex) {
        log.error(message, ex);
    }

    public static boolean isEmpty(String str) {
        return str == null || "".endsWith(str);
    }

    public static String buildTaskId(Task task) {
        StringBuilder builder = new StringBuilder();
        URI uri = task.getUri();
        String path = uri.getPath();
        if (!isEmpty(path) && path.contains("/")) {
            path = path.substring(path.lastIndexOf("/") + 1, path.length());
        }
        builder.append(uri.getHost()).append("@").append(uri.getPort())
                .append("#")
                .append(path)
                .append("#");
        builder.append("[");
        builder
                .append("p:")
                .append(task.getPriority().name())
                .append("@")
                .append("b:")
                .append(task.getAdvancedProperty().isBreakPointEnabled())
                .append("@")
                .append("m:")
                .append(task.getAdvancedProperty().isMultiThreadEnabled())
                .append("@")
                .append("r:")
                .append(task.getAdvancedProperty().isRetryEnabled());

        if (task instanceof SplitTask) {
            SplitTask splitTask = ((SplitTask) task);
            builder.append("@")
                    .append("s:")
                    .append(splitTask.getStartPosition())
                    .append("@")
                    .append("e:")
                    .append(splitTask.getEndPosition());
        }
        builder.append("]");
        return builder.toString();
    }

    public static int calculateProgress(long finishedSize, long totalSize) {
        if (totalSize == 0) throw new IllegalArgumentException("total size must great than zero!");
        return (int) (finishedSize * 100 / totalSize);
    }

    public static void createTargetFile(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
    }
}
