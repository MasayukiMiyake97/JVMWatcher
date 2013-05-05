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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author miyake
 *
 */
public final class GarbageCollectorState
{
    private static  Log log = LogFactory.getLog(GarbageCollectorState.class);

    private String  memoryManagerName_ = null;
    private long    collectionCount_ = -1L;
    private long    collectionTime_ = -1L;

    /**
     * @param name
     * @param count
     * @param time
     */
    public GarbageCollectorState(String name, long count, long time)
    {
        this.memoryManagerName_ = name;
        this.collectionCount_ = count;
        this.collectionTime_ = time;
    }

    /**
     * @return
     */
    public String getMemoryManagerName()
    {
        return memoryManagerName_;
    }

    /**
     * @return
     */
    public long getCollectionCount()
    {
        return collectionCount_;
    }

    /**
     * @return
     */
    public long getCollectionTime()
    {
        return collectionTime_;
    }
    
}
