package com.bestxty.sault;

import com.bestxty.sault.internal.OkHttpDownloader;
import com.bestxty.sault.internal.SaultExecutorService;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import okhttp3.OkHttpClient;

/**
 * @author xty
 *         Created by xty on 2017/3/5.
 */
public final class SaultConfiguration {

    private final String key;
    private final File saveDir;
    private final ExecutorService service;
    private final Downloader downloader;
    private final Boolean loggingEnabled;
    private final Boolean breakPointEnabled;
    private final Boolean multiThreadEnabled;
    private final Boolean autoAdjustThreadEnabled;

    private SaultConfiguration(Builder builder) {
        key = UUID.randomUUID().toString();
        saveDir = builder.saveDir;
        service = builder.service;
        downloader = builder.downloader;
        loggingEnabled = builder.loggingEnabled;
        breakPointEnabled = builder.breakPointEnabled;
        multiThreadEnabled = builder.multiThreadEnabled;
        autoAdjustThreadEnabled = builder.autoAdjustThreadEnabled;
    }

    public String getKey() {
        return key;
    }

    public File getSaveDir() {
        return saveDir;
    }

    public ExecutorService getService() {
        return service;
    }

    public Downloader getDownloader() {
        return downloader;
    }

    public Boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public Boolean isBreakPointEnabled() {
        return breakPointEnabled;
    }

    public Boolean isMultiThreadEnabled() {
        return multiThreadEnabled;
    }

    public Boolean isAutoAdjustThreadEnabled() {
        return autoAdjustThreadEnabled;
    }

    public static final class Builder {

        private File saveDir;
        private ExecutorService service;
        private Downloader downloader;
        private OkHttpClient httpClient;
        private boolean loggingEnabled = false;
        private boolean breakPointEnabled = true;
        private boolean multiThreadEnabled = true;
        private boolean autoAdjustThreadEnabled = true;


        public Builder saveDir(String saveDir) {
            return saveDir(new File(saveDir));
        }


        public Builder saveDir(File saveDir) {
            this.saveDir = saveDir;
            return this;
        }

        public Builder client(OkHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder downloader(Downloader downloader) {
            this.downloader = downloader;
            return this;
        }

        public Builder executor(ExecutorService service) {
            this.service = service;
            return this;
        }


        /**
         * Toggle whether debug logging is enabled.
         * <p>
         * <b>WARNING:</b> Enabling this will result in excessive object allocation. This should be only
         * be used for debugging purposes. Do NOT pass {@code BuildConfig.DEBUG}.
         *
         * @param enabled enable logging
         * @return builder
         */
        public Builder loggingEnabled(boolean enabled) {
            this.loggingEnabled = enabled;
            return this;
        }

        public Builder breakPointEnabled(boolean enabled) {
            this.breakPointEnabled = enabled;
            return this;
        }

        public Builder multiThreadEnabled(boolean enabled) {
            this.multiThreadEnabled = enabled;
            return this;
        }

        public Builder autoAdjustThreadEnabled(boolean enabled) {
            this.autoAdjustThreadEnabled = enabled;
            return this;
        }


        public SaultConfiguration build() {
            if (service == null) {
                service = new SaultExecutorService();
            }
            if (downloader == null) {
                downloader = httpClient == null ? new OkHttpDownloader() : new OkHttpDownloader(httpClient);
            }
            return new SaultConfiguration(this);
        }
    }

}
