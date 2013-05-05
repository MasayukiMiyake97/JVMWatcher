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
package org.fluentd.jvmwatcher.parser;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fluentd.jvmwatcher.data.GarbageCollectorState;
import org.fluentd.jvmwatcher.data.JvmStateLog;
import org.fluentd.jvmwatcher.data.JvmWatchState;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * @author miyake
 *
 */
public class JsonSimpleLogParser extends AbstractStateParser
{
    private static  Log log = LogFactory.getLog(AbstractStateParser.class);

    /* (非 Javadoc)
     * @see org.fluentd.jvmwatcher.parser.AbstractStateParser#parseState(java.io.PrintWriter, org.fluentd.jvmwatcher.data.JvmWatchState)
     */
    @Override
    public boolean parseState(PrintWriter out, JvmWatchState state)
    {
        boolean         ret = false;
        JsonFactory     jsonFactory = new JsonFactory();
        JsonGenerator   generator = null;
        try
        {
            generator = jsonFactory.createGenerator(out);
            // convert to JSON stream.
            this.outSimpleLog(generator, state);
            ret = true;
        }
        catch (IOException ex)
        {
            log.error("Parse output error.", ex);
            ret = false;
        }
        finally
        {
            if (null != generator)
            {
                try
                {
                    // flush to JSON stream.
                    generator.flush();
                }
                catch (IOException ex)
                {
                    log.error("writer flush error.", ex);
                }
            }
        }
        
        return ret;
    }
    
    /**
     * @param generator
     * @param state
     * @throws IOException 
     * @throws JsonGenerationException 
     */
    private void outSimpleLog(JsonGenerator generator, JvmWatchState state) throws JsonGenerationException, IOException
    {
        Collection<JvmStateLog>  logArray = state.getStateLog();
        
        // convert to JSON stream of JvmStateLog.
        for (JvmStateLog elem : logArray)
        {
            generator.writeStartObject();
            // Common 
            generator.writeNumberField(LOG_DATETIME, elem.getLogDateTime());
            generator.writeStringField(HOST_NAME, this.getHostName());
            generator.writeStringField(PROC_STATE, elem.getProcState().name());
            generator.writeStringField(SHORT_NAME, state.getShortName());
            generator.writeNumberField(JVM_ID, state.getJvmId());
            // runtime
            generator.writeNumberField(START_TIME, state.getJvmStartTime());
            generator.writeNumberField(LOG_RUN_UP_TIME, elem.getJvmUpTime());
            // cpu usage
            generator.writeNumberField(LOG_CPU_USAGE, elem.getCpuUsage());
            // Compilation
            generator.writeNumberField(LOG_COMPILE_TIME, elem.getCompileTime());
            // Class loading
            generator.writeNumberField(LOG_CLASS_LOAD_CNT, elem.getClassLoadedCount());
            generator.writeNumberField(LOG_CLASS_UNLOAD_CNT, elem.getClassUnloadedCount());
            generator.writeNumberField(LOG_CLASS_TOTAL_LOAD_CNT, elem.getClassTotalLoadedCount());
            // Thread
            generator.writeNumberField(LOG_THREAD_CNT, elem.getThreadCount());
            generator.writeNumberField(LOG_DAEMON_TH_CNT, elem.getDaemonThreadCount());
            generator.writeNumberField(LOG_PEAK_TH_CNT, elem.getPeakThreadCount());
            // Memory
            if (elem.getHeapSize() != null)
            {
                generator.writeNumberField(LOG_MEM_HEAP_INIT, elem.getHeapSize().getInit());
                generator.writeNumberField(LOG_MEM_HEAP_USED, elem.getHeapSize().getUsed());
                generator.writeNumberField(LOG_MEM_HEAP_COMMITED, elem.getHeapSize().getCommitted());
                generator.writeNumberField(LOG_MEM_HEAP_MAX, elem.getHeapSize().getMax());
            }
            if (elem.getNotheapSize() != null)
            {
                generator.writeNumberField(LOG_MEM_NOTHEAP_INIT, elem.getNotheapSize().getInit());
                generator.writeNumberField(LOG_MEM_NOTHEAP_USED, elem.getNotheapSize().getUsed());
                generator.writeNumberField(LOG_MEM_NOTHEAP_COMMITED, elem.getNotheapSize().getCommitted());
                generator.writeNumberField(LOG_MEM_NOTHEAP_MAX, elem.getNotheapSize().getMax());
            }
            generator.writeNumberField(LOG_MEM_PENDING_FIN_CNT, elem.getPendingFinalizationCount_());
            // OS Information
            generator.writeNumberField(LOG_OS_TOTAL_PHY_MEM_SIZE, elem.getTotalPhysicalMemorySize());
            generator.writeNumberField(LOG_OS_TOTAL_SWAP_MEM_SIZE, elem.getTotalSwapSpaceSize());
            generator.writeNumberField(LOG_OS_FREE_PHY_MEM_SIZE, elem.getFreePhysicalMemorySize());
            generator.writeNumberField(LOG_OS_FREE_SWAP_MEM_SIZE, elem.getFreeSwapSpaceSize());
            generator.writeNumberField(LOG_OS_COMMIT_VMEM_SIZE, elem.getCommittedVirtualMemorySize());

            Collection<GarbageCollectorState>   gcColl = elem.getGcStateCollection();
            if (null != gcColl)
            {
                // GC INformation (Array output)
                generator.writeFieldName(LOG_KEY_GC_COLLECT);
                generator.writeStartArray();
                for (GarbageCollectorState gcElem : gcColl)
                {
                    generator.writeStartObject();
                    generator.writeStringField(LOG_GC_MEM_MGR_NAME, gcElem.getMemoryManagerName());
                    generator.writeNumberField(LOG_GC_COLLECTION_CNT, gcElem.getCollectionCount());
                    generator.writeNumberField(LOG_GC_COLLECTION_TIME, gcElem.getCollectionTime());
                    generator.writeEndObject();
                }
                generator.writeEndArray();
            }
            
            generator.writeEndObject();
        }
    }

}
