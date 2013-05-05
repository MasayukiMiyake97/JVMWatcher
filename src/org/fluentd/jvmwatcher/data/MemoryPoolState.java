//
// A Java VM status Watcher for Fluent
//
// Copyright (C) 2013 Masayuki Miyake
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class that stores the status obtained from MemoryPool.
 * @author miyake
 *
 */
public final class MemoryPoolState
{
    private static  Log log = LogFactory.getLog(MemoryPoolState.class);

    private String      poolName_ = null;
    private long        usageThreshold_ = 0L;
    private MemoryUsage usage_ = null;
    private long        lastGcId_ = 0L;
    private long        lastGcStartTime_ = 0L;
    private long        lastGcEndTime_ = 0L;
    private long        collectThreshold_ = 0L;
    private MemoryUsage beforeGcUsage_ = null;
    private MemoryUsage afterGcUsage_ = null;

    /**
     * Constructor
     * 
     * @param name
     * @param usageThreshold
     * @param usage
     * @param lastGcId
     * @param lastGcStartTime
     * @param lastGcEndTime
     * @param collectThreshold
     * @param beforeGcUsage
     * @param afterGcUsage
     */
    public MemoryPoolState(String name,
                           long usageThreshold,
                           MemoryUsage usage,
                           long lastGcId,
                           long lastGcStartTime,
                           long lastGcEndTime,
                           long collectThreshold,
                           MemoryUsage beforeGcUsage,
                           MemoryUsage afterGcUsage)
    {
        this.poolName_ = name;
        this.usageThreshold_ = usageThreshold;
        this.usage_ = usage;
        this.lastGcId_ = lastGcId;
        this.lastGcStartTime_ = lastGcStartTime;
        this.lastGcEndTime_ = lastGcEndTime;
        this.collectThreshold_ = collectThreshold;
        this.beforeGcUsage_ = beforeGcUsage;
        this.afterGcUsage_ = afterGcUsage;
    }

    /**
     * @return poolName
     */
    public String getPoolName()
    {
        return poolName_;
    }

    /**
     * @return usageThreshold
     */
    public long getUsageThreshold()
    {
        return usageThreshold_;
    }

    /**
     * @return usage
     */
    public MemoryUsage getUsage()
    {
        return usage_;
    }

    /**
     * @return lastGcId
     */
    public long getLastGcId()
    {
        return lastGcId_;
    }

    /**
     * @return lastGcStartTime
     */
    public long getLastGcStartTime()
    {
        return lastGcStartTime_;
    }

    /**
     * @return lastGcEndTime
     */
    public long getLastGcEndTime()
    {
        return lastGcEndTime_;
    }

    /**
     * @return collectThreshold
     */
    public long getCollectThreshold()
    {
        return collectThreshold_;
    }

    /**
     * @return beforeGcUsage
     */
    public MemoryUsage getBeforeGcUsage()
    {
        return beforeGcUsage_;
    }

    /**
     * @return afterGcUsage
     */
    public MemoryUsage getAfterGcUsage()
    {
        return afterGcUsage_;
    }


}
