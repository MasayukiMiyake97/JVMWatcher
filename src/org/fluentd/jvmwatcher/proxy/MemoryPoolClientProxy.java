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
package org.fluentd.jvmwatcher.proxy;

import static java.lang.management.ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE;

import java.io.IOException;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.ObjectName;

import org.fluentd.jvmwatcher.data.MemoryPoolState;
import com.sun.management.GarbageCollectorMXBean;
import com.sun.management.GcInfo;

/**
 * This class manages the MXBesn of GarbageCollector and MemoryPool. And this, to get the state of MemoryPool.<BR>
 * 
 * @author miyake
 *
 */
public class MemoryPoolClientProxy
{
    /**
     * Local Java VM information.
     */
    private JvmClientProxy      clientProxy_ = null;

    private String              poolName_ = null;
    private ObjectName          objName_ = null;
    private MemoryPoolMXBean    pool_ = null;
    private Map<ObjectName,Long>    gcCountMap_ = null;
    private GcInfo              lastGcInfo_ = null;


    /**
     * @param client
     */
    public MemoryPoolClientProxy(JvmClientProxy client)
    {
        this.clientProxy_ = client;
        this.gcCountMap_ = new HashMap<ObjectName,Long>();
    }
    
    /**
     * @param poolName
     * @return
     */
    public boolean init(ObjectName poolName)
    {
        try
        {
            this.objName_ = poolName;
            this.pool_ = this.clientProxy_.getMXBean(poolName, MemoryPoolMXBean.class);
            this.poolName_ = this.pool_.getName();
        }
        catch (IOException ex)
        {
            System.err.println(ex.toString());
            return false;
        }
        
        if (null == this.pool_)
        {
            return false;
        }

        String[] mgrNames = this.pool_.getMemoryManagerNames();
        for (String name : mgrNames)
        {
            try
            {
                ObjectName gcMbeanName = new ObjectName(GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=" + name);
                if (this.clientProxy_.isRegistered(gcMbeanName))
                {
                    this.gcCountMap_.put(gcMbeanName, new Long(0));
                }
            }
            catch (Exception ex)
            {
                System.err.println("get Garbage collection MXBean error." + ex.toString());
            }
        }
        
        return true;
    }
    
    public boolean isCollectedMemoryPool() {
        return (this.gcCountMap_.size() != 0);
    }

    public ObjectName getObjectName() {
        return this.objName_;
    }

    public MemoryPoolState getStat() throws java.io.IOException {
        
        long    usageThreshold = -1L;
        long    collectThreshold = -1L;
        
        if (this.pool_.isUsageThresholdSupported() == true)
        {
            usageThreshold = this.pool_.getUsageThreshold();
        }
        if (this.pool_.isCollectionUsageThresholdSupported() == true)
        {
            collectThreshold = this.pool_.getCollectionUsageThreshold();
        }
        
        long lastGcStartTime = 0;
        long lastGcEndTime = 0;
        MemoryUsage beforeGcUsage = null;
        MemoryUsage afterGcUsage = null;
        long gcId = 0;

        // get last GC information.
        if (this.lastGcInfo_ != null)
        {
            gcId = this.lastGcInfo_.getId();
            lastGcStartTime = this.lastGcInfo_.getStartTime();
            lastGcEndTime = this.lastGcInfo_.getEndTime();
            beforeGcUsage = this.lastGcInfo_.getMemoryUsageBeforeGc().get(this.poolName_);
            afterGcUsage = this.lastGcInfo_.getMemoryUsageAfterGc().get(this.poolName_);
        }

        Set<Map.Entry<ObjectName,Long>> gcCountSet = this.gcCountMap_.entrySet();

        for (Map.Entry<ObjectName,Long> elem : gcCountSet)
        {
            GarbageCollectorMXBean gc = this.clientProxy_.getMXBean(elem.getKey(), com.sun.management.GarbageCollectorMXBean.class);
            Long gcCount = elem.getValue();
            Long newCount = gc.getCollectionCount();

            if (newCount > gcCount)
            {
                this.gcCountMap_.put(elem.getKey(), new Long(newCount));
                this.lastGcInfo_ = gc.getLastGcInfo();
                if (this.lastGcInfo_.getEndTime() > lastGcEndTime)
                {
                    gcId = this.lastGcInfo_.getId();
                    lastGcStartTime = this.lastGcInfo_.getStartTime();
                    lastGcEndTime = this.lastGcInfo_.getEndTime();
                    beforeGcUsage = this.lastGcInfo_.getMemoryUsageBeforeGc().get(this.poolName_);
                    afterGcUsage = this.lastGcInfo_.getMemoryUsageAfterGc().get(this.poolName_);
                    if (beforeGcUsage == null)
                    {
                        System.err.println("beforeGcUsage get error.");
                    }
                    if (afterGcUsage == null)
                    {
                        System.err.println("afterGcUsage get error.");
                    }
                }
            }
        }

        MemoryUsage usage = this.pool_.getUsage();

        // make return data. (MemoryPoolStat)
        MemoryPoolState  ret = new MemoryPoolState(this.poolName_,
                                                   usageThreshold,
                                                   usage,
                                                   gcId,
                                                   lastGcStartTime,
                                                   lastGcEndTime,
                                                   collectThreshold,
                                                   beforeGcUsage,
                                                   afterGcUsage);

        return ret;
    }

    
}
