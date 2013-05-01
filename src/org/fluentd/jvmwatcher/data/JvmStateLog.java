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

import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Collection;

import org.fluentd.jvmwatcher.proxy.JvmClientProxy;
import org.fluentd.jvmwatcher.proxy.MemoryPoolClientProxy;

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
    private long        jvmUpTime_ = -1L;
    // MemoryPoolMXBean
    private Collection<MemoryPoolState>         memoryPoolStateColl_ = null;
    // GarbageCollectorMXBean
    private Collection<GarbageCollectorState>   gcCollectorState_ = null;
    
    /**
     * 
     */
    private JvmStateLog()
    {
        
    }
    
    /**
     * @param clientPrixy
     * @return
     */
    public static JvmStateLog makeJvmStateLog(JvmClientProxy clientProxy)
    {
        JvmStateLog     ret = new JvmStateLog();

        try
        {
            // set log time
            ret.logDateTime_ = System.currentTimeMillis();
            
            // ClassLoadingMXBean
            ClassLoadingMXBean  classLoadingBean = clientProxy.getClassLoadingMXBean();
            if (null != classLoadingBean)
            {
                ret.classLoadedCount_ = classLoadingBean.getLoadedClassCount();
                ret.classUnloadedCount_ = classLoadingBean.getUnloadedClassCount();
                ret.classTotalLoadedCount_ = classLoadingBean.getTotalLoadedClassCount();
            }

            // CompilationMXBean
            CompilationMXBean   compilationBean = clientProxy.getCompilationMXBean();
            if (null != compilationBean)
            {
                ret.compileTime_ = compilationBean.getTotalCompilationTime();
            }

            // MemoryMXBean
            MemoryMXBean  memoryBean = clientProxy.getMemoryMXBean();
            if (null != memoryBean)
            {
                ret.heapSize_ = memoryBean.getHeapMemoryUsage();
                ret.notheapSize_ = memoryBean.getNonHeapMemoryUsage();
                ret.pendingFinalizationCount_ = memoryBean.getObjectPendingFinalizationCount();
            }

            // OperatingSystemMXBean
            OperatingSystemMXBean  OpeSysBean = clientProxy.getOperatingSystemMXBean();
            if (null != OpeSysBean)
            {
                ret.osAvailableProcessors_ = OpeSysBean.getAvailableProcessors();
                ret.osSystemLoadAverage_ = OpeSysBean.getSystemLoadAverage();
            }

            // com.sun.management.OperatingSystemMXBean
            com.sun.management.OperatingSystemMXBean  sunOpeSysBean = clientProxy.getSunOperatingSystemMXBean();
            if (null != sunOpeSysBean)
            {
                ret.committedVirtualMemorySize_ = sunOpeSysBean.getCommittedVirtualMemorySize();
                ret.freePhysicalMemorySize_ = sunOpeSysBean.getFreePhysicalMemorySize();
                ret.freeSwapSpaceSize_ = sunOpeSysBean.getFreeSwapSpaceSize();
                ret.processCpuTime_ = sunOpeSysBean.getProcessCpuTime();
                ret.totalPhysicalMemorySize_ = sunOpeSysBean.getTotalPhysicalMemorySize();
                ret.totalSwapSpaceSize_ = sunOpeSysBean.getTotalSwapSpaceSize();
            }

            // RuntimeMXBean
            RuntimeMXBean  runtimeBean = clientProxy.getRuntimeMXBean();
            if (null != runtimeBean)
            {
                ret.jvmUpTime_ = runtimeBean.getUptime();
            }

            // MemoryPoolMXBean
            Collection<MemoryPoolClientProxy>  memoryPoolBeansColl = clientProxy.getMemoryPoolClientProxies();
            if (null != memoryPoolBeansColl)
            {
                ret.memoryPoolStateColl_ = new ArrayList<MemoryPoolState>();
                for (MemoryPoolClientProxy elem : memoryPoolBeansColl)
                {
                    if (null != elem)
                    {
                        MemoryPoolState     state = elem.getStat();
                        if (null != state)
                        {
                            // add MemoryPoolState
                            ret.memoryPoolStateColl_.add(state);
                        }
                    }
                 }
            }

            // GarbageCollectorMXBean
            Collection<GarbageCollectorMXBean>  garbageCollBeansColl = clientProxy.getGarbageCollectorMXBeans();
            if (null != garbageCollBeansColl)
            {
                ret.gcCollectorState_ = new ArrayList<GarbageCollectorState>();
                for (GarbageCollectorMXBean elem : garbageCollBeansColl)
                {
                    if (null != elem)
                    {
                        long    collectionCount = elem.getCollectionCount();
                        long    collectionTime = elem.getCollectionTime();
                        String  memoryManagerName = elem.getName();
                        GarbageCollectorState   state = new GarbageCollectorState(memoryManagerName, collectionCount, collectionTime);
                        // add GarbageCollectorState
                        ret.gcCollectorState_.add(state);
                    }
                }
            }
        }
        catch (IOException ex)
        {
            System.err.println(ex.toString());
            // close JvmClientProxy
            clientProxy.disconnect();
            ret = null;
        }
        
        return ret;
    }

    /**
     * @return logDateTime
     */
    public long getLogDateTime()
    {
        return logDateTime_;
    }

    /**
     * @return classLoadedCount
     */
    public int getClassLoadedCount()
    {
        return classLoadedCount_;
    }

    /**
     * @return classUnloadedCount
     */
    public long getClassUnloadedCount()
    {
        return classUnloadedCount_;
    }

    /**
     * @return classTotalLoadedCount
     */
    public long getClassTotalLoadedCount()
    {
        return classTotalLoadedCount_;
    }

    /**
     * @return compileTime
     */
    public long getCompileTime()
    {
        return compileTime_;
    }

    /**
     * @return heapSize
     */
    public MemoryUsage getHeapSize()
    {
        return heapSize_;
    }

    /**
     * @return notheapSize
     */
    public MemoryUsage getNotheapSize()
    {
        return notheapSize_;
    }

    /**
     * @return pendingFinalizationCount
     */
    public int getPendingFinalizationCount_()
    {
        return pendingFinalizationCount_;
    }

    /**
     * @return osAvailableProcessors
     */
    public int getOsAvailableProcessors()
    {
        return osAvailableProcessors_;
    }

    /**
     * @return osSystemLoadAverage
     */
    public double getOsSystemLoadAverage()
    {
        return osSystemLoadAverage_;
    }

    /**
     * @return committedVirtualMemorySize
     */
    public long getCommittedVirtualMemorySize()
    {
        return committedVirtualMemorySize_;
    }

    /**
     * @return freePhysicalMemorySize
     */
    public long getFreePhysicalMemorySize()
    {
        return freePhysicalMemorySize_;
    }

    /**
     * @return freeSwapSpaceSize
     */
    public long getFreeSwapSpaceSize()
    {
        return freeSwapSpaceSize_;
    }

    /**
     * @return processCpuTime
     */
    public long getProcessCpuTime()
    {
        return processCpuTime_;
    }

    /**
     * @return totalPhysicalMemorySize
     */
    public long getTotalPhysicalMemorySize()
    {
        return totalPhysicalMemorySize_;
    }

    /**
     * @return totalSwapSpaceSize
     */
    public long getTotalSwapSpaceSize()
    {
        return totalSwapSpaceSize_;
    }

    /**
     * @return jvmUpTime
     */
    public long getJvmUpTime()
    {
        return jvmUpTime_;
    }

    /**
     * @return memoryPoolStateColl
     */
    public Collection<MemoryPoolState> getMemoryPoolStateCollection()
    {
        return memoryPoolStateColl_;
    }

    /**
     * @return gcCollectorState
     */
    public Collection<GarbageCollectorState> getGcStateCollection()
    {
        return gcCollectorState_;
    }

}
