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

import java.io.PrintWriter;
import java.util.Collection;
import org.fluentd.jvmwatcher.data.JvmWatchState;

/**
 * @author miyake
 *
 */
public abstract class AbstractStateParser
{
    /*
     * JSON Key name
     */
    public static final String      HOST_NAME       = "host_name";
    public static final String      PROC_STATE      = "proc_state";
    public static final String      COMMAND_LINE    = "command_line";
    public static final String      DISPRAY_NAME    = "display_name";
    public static final String      SHORT_NAME      = "name";
    public static final String      JVM_ID          = "pid";
    public static final String      JIT_NAME        = "jit_name";
    public static final String      OS_ARCH         = "os_arch";
    public static final String      OS_NAME         = "os_name";
    public static final String      OS_VERSION      = "os_ver";
    public static final String      START_TIME      = "start_time";
    public static final String      RUN_TIME_NAME   = "run_time_name";
    public static final String      VM_NAME         = "vm_name";
    public static final String      VM_VENDER       = "vm_vend";
    public static final String      VM_VERSION      = "vm_ver";
    public static final String      SPEC_NAME       = "spec_name";
    public static final String      SPEC_VENDER     = "spec_vend";
    public static final String      SPEC_VERSION    = "spec_ver";
    public static final String      JVM_LOGS        = "logs";

    // Log data 
    public static final String      LOG_DATETIME                = "logtime";
    public static final String      LOG_DISPLAY_DATETIME        = "display_logtime";
    
    public static final String      LOG_KEY_CLASS_LOADER        = "class_loader";
    public static final String      LOG_CLASS_LOAD_CNT          = "c_load_cnt";
    public static final String      LOG_CLASS_UNLOAD_CNT        = "c_unload_cnt";
    public static final String      LOG_CLASS_TOTAL_LOAD_CNT    = "c_total_load_cnt";

    public static final String      LOG_KEY_COMILATION          = "compilation";
    public static final String      LOG_COMPILE_TIME            = "compile_time";

    public static final String      LOG_KEY_MEMORY              = "memory";
    public static final String      LOG_MEM_HEAP_INIT           = "heap_init";
    public static final String      LOG_MEM_HEAP_USED           = "heap_used";
    public static final String      LOG_MEM_HEAP_COMMITED       = "heap_commit";
    public static final String      LOG_MEM_HEAP_MAX            = "heap_max";
    public static final String      LOG_MEM_NOTHEAP_INIT        = "notheap_init";
    public static final String      LOG_MEM_NOTHEAP_USED        = "notheap_used";
    public static final String      LOG_MEM_NOTHEAP_COMMITED    = "notheap_commit";
    public static final String      LOG_MEM_NOTHEAP_MAX         = "notheap_max";
    public static final String      LOG_MEM_PENDING_FIN_CNT     = "pending_fin_cnt";

    public static final String      LOG_KEY_THREAD              = "thread";
    public static final String      LOG_THREAD_CNT              = "th_cnt";
    public static final String      LOG_DAEMON_TH_CNT           = "daemon_th_cnt";
    public static final String      LOG_PEAK_TH_CNT             = "peak_th_cnt";
    
    public static final String      LOG_KEY_OS                  = "os";
    public static final String      LOG_OS_AVAILABLE_PROCESS    = "avail_proc_num";
    public static final String      LOG_OS_SYS_LOAD_AVERAGE     = "sys_load_ave";
    public static final String      LOG_OS_COMMIT_VMEM_SIZE     = "commit_vmem_size";
    public static final String      LOG_OS_FREE_PHY_MEM_SIZE    = "free_phy_mem_size";
    public static final String      LOG_OS_FREE_SWAP_MEM_SIZE   = "free_swap_mem_size";
    public static final String      LOG_OS_PROC_CPU_TIME        = "proc_cpu_time";
    public static final String      LOG_OS_TOTAL_PHY_MEM_SIZE   = "total_phy_mem_size";
    public static final String      LOG_OS_TOTAL_SWAP_MEM_SIZE  = "total_swap_mem_size";

    public static final String      LOG_KEY_RUNTIME             = "runtime";
    public static final String      LOG_RUN_UP_TIME             = "up_time";

    // MemoryPoolState
    public static final String      LOG_KEY_MEM_POOL            = "mem_pool";
    public static final String      LOG_MP_POOL_NAME            = "pool_name";
    public static final String      LOG_MP_USAGE_THRESHOLD      = "usage_t_hold";

    public static final String      LOG_MP_USAGE_INIT           = "usage_init";
    public static final String      LOG_MP_USAGE_USED           = "usage_used";
    public static final String      LOG_MP_USAGE_COMMITED       = "usage_commit";
    public static final String      LOG_MP_USAGE_MAX            = "usage_max";

    public static final String      LOG_MP_LAST_GCID            = "last_gcid";
    public static final String      LOG_MP_LAST_GC_START_TIME   = "last_gc_s_time";
    public static final String      LOG_MP_LAST_GC_END_TIME     = "last_gc_e_time";
    public static final String      LOG_MP_COLLECT_THRESHOLD    = "collect_t_hold";

    public static final String      LOG_MP_BEFORE_GC_INIT       = "bef_gc_init";
    public static final String      LOG_MP_BEFORE_GC_USED       = "bef_gc_used";
    public static final String      LOG_MP_BEFORE_GC_COMMITED   = "bef_gc_commit";
    public static final String      LOG_MP_BEFORE_GC_MAX        = "bef_gc_max";

    public static final String      LOG_MP_AFTER_GC_INIT        = "aft_gc_init";
    public static final String      LOG_MP_AFTER_GC_USED        = "aft_gc_used";
    public static final String      LOG_MP_AFTER_GC_COMMITED    = "aft_gc_commit";
    public static final String      LOG_MP_AFTER_GC_MAX         = "aft_gc_max";

    // GarbageCollectorState
    public static final String      LOG_KEY_GC_COLLECT          = "gc_collect";
    public static final String      LOG_GC_MEM_MGR_NAME         = "gc_mgr_name";
    public static final String      LOG_GC_COLLECTION_CNT       = "gc_coll_cnt";
    public static final String      LOG_GC_COLLECTION_TIME      = "gc_coll_time";

    // The condition of JVM to have computed inside the program and for it to have been sought.
    public static final String      LOG_CPU_USAGE               = "cpu_usage";
    
    
    /**
     * Local host Node name
     */
    private     String  hostName_ = null;

    /**
     * @param name
     */
    public void setHostName(String name)
    {
        this.hostName_ = name;
    }

    /**
     * @return
     */
    public String getHostName()
    {
        return this.hostName_;
    }

    /**
     * @param out
     * @param src
     * @return
     */
    public abstract boolean parseState(PrintWriter out, Collection<JvmWatchState> srcColl);
}
