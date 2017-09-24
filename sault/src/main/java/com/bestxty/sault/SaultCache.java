package com.bestxty.sault;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import static com.bestxty.sault.Utils.log;

/**
 * @author xty
 *         Created by xty on 2017/3/5.
 */
final class SaultCache {

    private enum Cache {
        INSTANCE;
        private SaultCache saultCache;

        Cache() {
            saultCache = new SaultCache();
        }

        public SaultCache getSaultCache() {
            return saultCache;
        }
    }

    private Map<String, Sault> cachesMap = new HashMap<>();

    private SaultCache() {
    }

    private Sault getFromCache(String key) {
        return cachesMap.get(key);
    }

    private void putToCache(String key, Sault sault) {
        cachesMap.put(key, sault);
    }

    private void removeFromCache(String key) {
        cachesMap.remove(key);
    }

    static synchronized Sault createSaultOrGetFromCache(SaultConfiguration configuration, Context context) {
        SaultCache cache = Cache.INSTANCE.getSaultCache();
        Sault sault = cache.getFromCache(configuration.getKey());
        if (sault == null) {
            sault = new Sault(configuration, context);
            cache.putToCache(configuration.getKey(), sault);
        }
        return sault;
    }


    static synchronized void release(Sault sault) {
        SaultCache cache = Cache.INSTANCE.getSaultCache();
        String key = sault.getKey();
        Sault cacheSault = cache.getFromCache(key);
        if (cacheSault == null) {
            log("Sault " + key + " has been closed already.");
            return;
        }

        cacheSault.shutdown();

        cache.removeFromCache(key);
    }

}
