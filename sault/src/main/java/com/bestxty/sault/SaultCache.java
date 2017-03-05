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

    private static Map<String, Sault> cachesMap = new HashMap<>();

    private SaultCache() {
    }

    static synchronized Sault createSaultOrGetFromCache(SaultConfiguration configuration, Context context) {
        Sault sault = cachesMap.get(configuration.getKey());
        if (sault == null) {
            sault = new Sault(configuration, context);
            cachesMap.put(configuration.getKey(), sault);
        }
        return sault;
    }


    static synchronized void release(Sault sault) {
        String key = sault.getKey();
        Sault cache = cachesMap.get(key);
        if (cache == null) {
            log("Sault " + key + " has been closed already.");
            return;
        }

        cache.shutdown();

        cachesMap.remove(key);
    }
    //    private static class RefAndCount {
//        // The Sault instance in this thread.
//        private final ThreadLocal<Sault> localSault = new ThreadLocal<>();
//        // How many references to this Sault instance in this thread.
//        private final ThreadLocal<Integer> localCount = new ThreadLocal<>();
//        // How many threads have instances refer to this configuration.
//        private int globalCount = 0;
//    }
//
//    private enum SaultCacheType {
//        TYPED_SAULT
//    }
//
//    private final EnumMap<SaultCacheType, RefAndCount> refAndCountMap;
//
//    private static Map<String, SaultCache> cachesMap = new HashMap<>();
//
//
//    private SaultCache() {
//        refAndCountMap = new EnumMap<>(SaultCacheType.class);
//        for (SaultCacheType type : SaultCacheType.values()) {
//            refAndCountMap.put(type, new RefAndCount());
//        }
//    }
//
//    static synchronized Sault createSaultOrGetFromCache(SaultConfiguration configuration, Context context) {
//        boolean isCacheInMap = true;
//        SaultCache cache = cachesMap.get(configuration.getKey());
//        if (cache == null) {
//            // Create a new cache
//            cache = new SaultCache();
//            // The new cache should be added to the map later.
//            isCacheInMap = false;
//
//        }
//
//        RefAndCount refAndCount = cache.refAndCountMap.get(SaultCacheType.TYPED_SAULT);
//
//        if (refAndCount.localSault.get() == null) {
//
//            Sault sault = Sault.getInstance(configuration, context);
//
//
//            // The cache is not in the map yet. Add it to the map after the Sault instance created successfully.
//            if (!isCacheInMap) {
//                cachesMap.put(configuration.getKey(), cache);
//            }
//            refAndCount.localSault.set(sault);
//            refAndCount.localCount.set(0);
//        }
//
//        Integer refCount = refAndCount.localCount.get();
//        if (refCount == 0) {
//            // This is the first instance in current thread, increase the global count.
//            refAndCount.globalCount++;
//        }
//        refAndCount.localCount.set(refCount + 1);
//
//
//        return refAndCount.localSault.get();
//    }
//
//    static synchronized void release(Sault sault) {
//        String key = sault.getKey();
//        SaultCache cache = cachesMap.get(key);
//        Integer refCount = null;
//        RefAndCount refAndCount = null;
//
//        if (cache != null) {
//            refAndCount = cache.refAndCountMap.get(SaultCacheType.TYPED_SAULT);
//            refCount = refAndCount.localCount.get();
//        }
//        if (refCount == null) {
//            refCount = 0;
//        }
//
//        if (refCount <= 0) {
//            log("Sault " + key + " has been closed already.");
//            return;
//        }
//
//        // Decrease the local counter.
//        refCount -= 1;
//
//        if (refCount == 0) {
//            // The last instance in this thread.
//            // Clear local ref & counter
//            refAndCount.localCount.set(null);
//            refAndCount.localSault.set(null);
//
//            // Clear global counter
//            refAndCount.globalCount--;
//            if (refAndCount.globalCount < 0) {
//                // Should never happen.
//                throw new IllegalStateException("Global reference counter of Sault" + key +
//                        " got corrupted.");
//            }
//
//
//            int totalRefCount = 0;
//            for (SaultCacheType type : SaultCacheType.values()) {
//                totalRefCount += cache.refAndCountMap.get(type).globalCount;
//            }
//            if (totalRefCount == 0) {
//                cachesMap.remove(key);
//            }
//
//            sault.shutdown();
//        } else {
//            refAndCount.localCount.set(refCount);
//        }
//    }

}
