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
 * This class holds a snapshot of the state of the JVM. Date and get the state, the CPU utilization, such as a command name is stored.
 * @author miyake
 *
 */
public class JvmWatchState
{
    /**
     * Java program command line 
     */
    private String      commandLine_ = null;
    /**
     * Java program display name 
     */
    private String      displayName_ = null;
    /**
     * Java VM ID (pid)
     */
    private int         jvmId_ = -1;

    /**
     * 
     */
    private long        DateTime_ = 0L;

    // ClassLoadingMXBean
    private int         classLoadedCount_ = 0;
    private long        classUnloadedCount_ = 0L;
    private long        classTotalLoadedCount_ = 0L;
    // CompilationMXBean
    private String      jitName_ = null;
    private long        compileTime_ = 0L;
    // MemoryMXBean
    private MemoryUsage heapSize_ = null;
    private MemoryUsage notheapSize_ = null;
    private int         pendingFinalizationCount_ = 0;
    // OperatingSystemMXBean
    private String      osArch_ = null;
    private int         osAvailableProcessors_ = 0;
    private String      osName_ = null;
    private double      osSystemLoadAverage_ = 0.0;
    private String      osVersion_ = null;
    // com.sun.management.OperatingSystemMXBean
    private long        committedVirtualMemorySize_ = -1L;
    private long        freePhysicalMemorySize_ = -1L;
    private long        freeSwapSpaceSize_ = -1L;
    private long        processCpuTime_ = -1L;
    private long        totalPhysicalMemorySize_ = -1L;
    private long        totalSwapSpaceSize_ = -1L;
    // RuntimeMXBean
    private String      jvmRuntimeName = null;
    private long        jvmStartTime = -1L;
    private long        jvmUpTime = -1L;
    private String      vmName = null;
    private String      vmVender = null;
    private String      vmVersion = null;
    private String      specName = null;
    private String      specVender = null;
    private String      specVersion = null;
    // MemoryPoolMXBean
    private Collection<MemoryPoolState>         memoryPoolStateColl_ = null;
    // GarbageCollectorMXBean
    private Collection<GarbageCollectorState>   gcCollectorColl_ = null;
    
    /**
     * @param clientPrixy
     * @return
     */
    public static JvmWatchState makeJvmWatchState(JvmClientProxy clientPrixy)
    {
        JvmWatchState   ret = new JvmWatchState();
        
        // set Local JVM Information
        ret.commandLine_ = clientPrixy.getLocalJvmInfo().getCommandLine_();
        ret.displayName_ = clientPrixy.getLocalJvmInfo().getDisplayName();
        ret.jvmId_ = clientPrixy.getLocalJvmInfo().getJvmid();

        // ClassLoadingMXBean
        if (setClassLoadingMXBeanState(ret, clientPrixy) == false)
        {
            return null;
        }
        // CompilationMXBean
        if (setCompilationMXBeanState(ret, clientPrixy) == false)
        {
            return null;
        }
        // MemoryMXBean
        if (setMemoryMXBeanState(ret, clientPrixy) == false)
        {
            return null;
        }
        // OperatingSystemMXBean
        // com.sun.management.OperatingSystemMXBean
        if (setOperatingSystemMXBeanState(ret, clientPrixy) == false)
        {
            return null;
        }
        // RuntimeMXBean
        if (setRuntimeMXBeanState(ret, clientPrixy) == false)
        {
            return null;
        }
        // MemoryPoolMXBean
        if (setMemoryPoolMXBeanState(ret, clientPrixy) == false)
        {
            return null;
        }
        // GarbageCollectorMXBean
        if (setGarbageCollectorMXBeanState(ret, clientPrixy) == false)
        {
            return null;
        }
        
        return ret;
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
