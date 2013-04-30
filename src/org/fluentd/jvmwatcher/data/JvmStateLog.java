//
// A Java VM status Watcher for Fluent
//
// Copyright (C) 2013 - 2013 Masayuki Miyake
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.fluentd.jvmwatcher.data;

import java.lang.management.MemoryUsage;
import java.util.Collection;

import org.fluentd.jvmwatcher.proxy.JvmClientProxy;

/**
 * @author miyake
 *
 */
public class JvmStateLog
{
    private long        logDateTime_ = 0L;    
    
    // ClassLoadingMXBean
    private int         classLoadedCount_ = -1;
    private long        classUnloadedCount_ = -1L;
    private long        classTotalLoadedCount_ = -1L;
    // CompilationMXBean
    private long        compileTime_ = -1L;
    // MemoryMXBean
    private MemoryUsage heapSize_ = null;
    private MemoryUsage notheapSize_ = null;
    private int         pendingFinalizationCount_ = -1;
    // OperatingSystemMXBean
    private int         osAvailableProcessors_ = 0;
    private double      osSystemLoadAverage_ = 0.0;
    // com.sun.management.OperatingSystemMXBean
    private long        committedVirtualMemorySize_ = -1L;
    private long        freePhysicalMemorySize_ = -1L;
    private long        freeSwapSpaceSize_ = -1L;
    private long        processCpuTime_ = -1L;
    private long        totalPhysicalMemorySize_ = -1L;
    private long        totalSwapSpaceSize_ = -1L;
    // RuntimeMXBean
    private long        jvmStartTime = -1L;
    private long        jvmUpTime = -1L;
    // MemoryPoolMXBean
    private Collection<MemoryPoolState>         memoryPoolStateColl_ = null;
    // GarbageCollectorMXBean
    private Collection<GarbageCollectorState>   gcCollectorColl_ = null;

    
    /**
     * @param clientPrixy
     * @return
     */
    public static JvmStateLog makeJvmStateLog(JvmClientProxy clientPrixy)
    {
        return null;
    }

    /**
     * @param state
     * @param clientPrixy
     * @return
     */
    private static boolean setClassLoadingMXBeanState(JvmWatchState state, JvmClientProxy clientPrixy)
    {
        return false;
    }

    /**
     * @param state
     * @param clientPrixy
     * @return
     */
    private static boolean setCompilationMXBeanState(JvmWatchState state, JvmClientProxy clientPrixy)
    {
        return false;
    }

    /**
     * @param state
     * @param clientPrixy
     * @return
     */
    private static boolean setMemoryMXBeanState(JvmWatchState state, JvmClientProxy clientPrixy)
    {
        return false;
    }

    /**
     * @param state
     * @param clientPrixy
     * @return
     */
    private static boolean setOperatingSystemMXBeanState(JvmWatchState state, JvmClientProxy clientPrixy)
    {
        return false;
    }

    /**
     * @param state
     * @param clientPrixy
     * @return
     */
    private static boolean setRuntimeMXBeanState(JvmWatchState state, JvmClientProxy clientPrixy)
    {
        return false;
    }

    /**
     * @param state
     * @param clientPrixy
     * @return
     */
    private static boolean setMemoryPoolMXBeanState(JvmWatchState state, JvmClientProxy clientPrixy)
    {
        return false;
    }

    /**
     * @param state
     * @param clientPrixy
     * @return
     */
    private static boolean setGarbageCollectorMXBeanState(JvmWatchState state, JvmClientProxy clientPrixy)
    {
        return false;
    }

}
