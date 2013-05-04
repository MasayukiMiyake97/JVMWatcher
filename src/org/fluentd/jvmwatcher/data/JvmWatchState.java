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
import java.lang.management.CompilationMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Collection;

import org.fluentd.jvmwatcher.proxy.JvmClientProxy;

/**
 * This class holds a snapshot of the state of the JVM. Date and get the state, the CPU utilization, such as a command name is stored.
 * @author miyake
 *
 */
public final class JvmWatchState implements Cloneable
{
    /**
     *
     */
    public enum ProcessState {
        /**
         * 
         */
        START_PROCESS,
        /**
         * 
         */
        LIVE_PROCESS,
        /**
         * 
         */
        END_PROCESS
    }
    
    /**
     * 
     */
    private ProcessState    procState_ = ProcessState.LIVE_PROCESS;
    
    /**
     * Java program command line 
     */
    private String          commandLine_ = null;
    /**
     * Java program display name 
     */
    private String          displayName_ = null;
    /**
     * Java program short name 
     */
    private String          shortName_ = null;
    /**
     * Java VM ID (pid)
     */
    private int             jvmId_ = -1;

    // CompilationMXBean
    private String          jitName_ = null;
    // OperatingSystemMXBean
    private String          osArch_ = null;
    private String          osName_ = null;
    private String          osVersion_ = null;
    // RuntimeMXBean
    private long            jvmStartTime_ = -1L;
    private String          jvmRuntimeName_ = null;
    private String          vmName_ = null;
    private String          vmVender_ = null;
    private String          vmVersion_ = null;
    private String          specName_ = null;
    private String          specVender_ = null;
    private String          specVersion_ = null;

    private Collection<JvmStateLog>     stateLog_ = null;

    // CPU usage
    private long            prevUpTime_ = 0L;
    private long            prevProcessCpuTime_ = 0L;

    
    /**
     * Constructor<BR>
     * This constructor uses only by the unit test.
     * 
     * @param procState
     * @param commandLine
     * @param displayName
     * @param shortName
     * @param jvmId
     * @param jitName
     * @param osArch
     * @param osName
     * @param osVersion
     * @param jvmStartTime
     * @param jvmRuntimeName
     * @param vmName
     * @param vmVender
     * @param vmVersion
     * @param specName
     * @param specVender
     * @param specVersion
     * @param stateLog
     */
    public JvmWatchState(ProcessState procState,
                         String commandLine,
                         String displayName,
                         String shortName,
                         int jvmId,
                         String jitName,
                         String osArch,
                         String osName,
                         String osVersion,
                         long jvmStartTime,
                         String jvmRuntimeName,
                         String vmName,
                         String vmVender,
                         String vmVersion,
                         String specName,
                         String specVender,
                         String specVersion,
                         ArrayList<JvmStateLog> stateLog)
    {
        this.procState_ = procState;
        this.commandLine_ = commandLine;
        this.displayName_ = displayName;
        this.shortName_ = shortName;
        this.jvmId_ = jvmId;
        this.jitName_ = jitName;
        this.osArch_ = osArch;
        this.osName_ = osName;
        this.osVersion_ = osVersion;
        this.jvmStartTime_ = jvmStartTime;
        this.jvmRuntimeName_ = jvmRuntimeName;
        this.vmName_ = vmName;
        this.vmVender_ = vmVender;
        this.vmVersion_ = vmVersion;
        this.specName_ = specName;
        this.specVender_ = specVender;
        this.specVersion_ = specVersion;
        this.stateLog_ = stateLog;
    }
    
    /**
     * Default Constructor
     */
    private JvmWatchState()
    {
        
    }
    
    /**
     * @param clientPrixy
     * @return
     */
    public static JvmWatchState makeJvmWatchState(JvmClientProxy clientProxy)
    {
        // null check
        if (null == clientProxy)
        {
            return null;
        }
        if (null == clientProxy.getLocalJvmInfo())
        {
            return null;
        }
        
        // create data object
        JvmWatchState   ret = new JvmWatchState();
        
        // set Local JVM Information
        ret.commandLine_ = clientProxy.getLocalJvmInfo().getCommandLine_();
        ret.displayName_ = clientProxy.getLocalJvmInfo().getDisplayName();
        ret.jvmId_ = clientProxy.getLocalJvmInfo().getJvmid();
        ret.shortName_ = clientProxy.getLocalJvmInfo().getShortName();

        // create log line array
        ret.stateLog_ = new ArrayList<JvmStateLog>();

        // set JVM information
        try
        {
            // CompilationMXBean
            CompilationMXBean   compilationBean = clientProxy.getCompilationMXBean();
            if (null != compilationBean)
            {
                ret.jitName_ = compilationBean.getName();
            }

            // OperatingSystemMXBean
            OperatingSystemMXBean  OpeSysBean = clientProxy.getOperatingSystemMXBean();
            if (null != OpeSysBean)
            {
                ret.osArch_ = OpeSysBean.getArch();
                ret.osName_ = OpeSysBean.getName();
                ret.osVersion_ = OpeSysBean.getVersion();
            }
            
            // RuntimeMXBean
            RuntimeMXBean  runtimeBean = clientProxy.getRuntimeMXBean();
            if (null != runtimeBean)
            {
                ret.jvmStartTime_ = runtimeBean.getStartTime();
                ret.jvmRuntimeName_ = runtimeBean.getName();
                ret.vmName_ = runtimeBean.getVmName();
                ret.vmVender_ = runtimeBean.getVmVendor();
                ret.vmVersion_ = runtimeBean.getVmVersion();
                ret.specName_ = runtimeBean.getSpecName();
                ret.specVender_ = runtimeBean.getSpecVendor();
                ret.specVersion_ = runtimeBean.getSpecVersion();
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
     * @param stateLog
     */
    public void addStateLog(JvmStateLog stateLog)
    {
        if (this.stateLog_ != null)
        {
            // calc CPU usage 
            float   cpuUsage = this.calsCpuUsage(stateLog);
            // set CPU usage
            stateLog.setCpuUsage(cpuUsage);

            this.stateLog_.add(stateLog);
        }
    }
    
    /**
     * @param stateLog
     * @return
     */
    private float calsCpuUsage(JvmStateLog stateLog)
    {
        long elapsedCpu = stateLog.getProcessCpuTime() - this.prevProcessCpuTime_;
        long elapsedTime = stateLog.getJvmUpTime() - this.prevUpTime_;
        
        // calc CPU usage
        float cpuUsage = Math.min(99F, elapsedCpu / (elapsedTime * 10000F * stateLog.getOsAvailableProcessors()));

        // set old ProcessCpuTime and UpTime.
        this.prevProcessCpuTime_ = stateLog.getProcessCpuTime();
        this.prevUpTime_ = stateLog.getJvmUpTime();
        
        return cpuUsage;
    }
    
    /**
     * 
     */
    public void clearStateLog()
    {
        if (this.stateLog_ != null)
        {
            this.stateLog_.clear();
        }
    }

    /**
     * @param procState
     */
    public void setProcState(ProcessState procState)
    {
        this.procState_ = procState;
    }

    /**
     * @return procState
     */
    public ProcessState getProcState()
    {
        return procState_;
    }

    /**
     * @return commandLine
     */
    public String getCommandLine()
    {
        return commandLine_;
    }

    /**
     * @return displayName
     */
    public String getDisplayName()
    {
        return displayName_;
    }

    /**
     * @return shortName
     */
    public String getShortName()
    {
        return shortName_;
    }

    /**
     * @return jvmId
     */
    public int getJvmId()
    {
        return jvmId_;
    }

    /**
     * @return jitName
     */
    public String getJitName()
    {
        return jitName_;
    }

    /**
     * @return osArch
     */
    public String getOsArch()
    {
        return osArch_;
    }

    /**
     * @return osName
     */
    public String getOsName()
    {
        return osName_;
    }

    /**
     * @return osVersion
     */
    public String getOsVersion()
    {
        return osVersion_;
    }

    /**
     * @return jvmStartTime
     */
    public long getJvmStartTime()
    {
        return jvmStartTime_;
    }

    /**
     * @return jvmRuntimeName
     */
    public String getJvmRuntimeName()
    {
        return jvmRuntimeName_;
    }

    /**
     * @return vmName
     */
    public String getVmName()
    {
        return vmName_;
    }

    /**
     * @return vmVender
     */
    public String getVmVender()
    {
        return vmVender_;
    }

    /**
     * @return vmVersion
     */
    public String getVmVersion()
    {
        return vmVersion_;
    }

    /**
     * @return specName
     */
    public String getSpecName()
    {
        return specName_;
    }

    /**
     * @return specVender
     */
    public String getSpecVender()
    {
        return specVender_;
    }

    /**
     * @return specVersion
     */
    public String getSpecVersion()
    {
        return specVersion_;
    }

    /**
     * @return stateLog
     */
    public Collection<JvmStateLog> getStateLog()
    {
        return stateLog_;
    }

    /* (非 Javadoc)
     * @see java.lang.Object#clone()
     */
    public JvmWatchState clone()
    {
        JvmWatchState   ret = null;
        try
        {
            ret = (JvmWatchState)super.clone();
            
            // value copy
            ret.commandLine_ = this.commandLine_;
            ret.displayName_ = this.displayName_;
            ret.jitName_ = this.jitName_;
            ret.jvmId_ = this.jvmId_;
            ret.jvmRuntimeName_ = this.jvmRuntimeName_;
            ret.jvmStartTime_ = this.jvmStartTime_;
            ret.osArch_ = this.osArch_;
            ret.osName_ = this.osName_;
            ret.osVersion_ = this.osVersion_;
            ret.prevProcessCpuTime_ = this.prevProcessCpuTime_;
            ret.prevUpTime_ = this.prevUpTime_;
            ret.procState_ = this.procState_;
            ret.shortName_ = this.shortName_;
            ret.specName_ = this.specName_;
            ret.specVender_ = this.specVender_;
            ret.specVersion_ = this.specVersion_;
            ret.stateLog_ = new ArrayList<JvmStateLog>(this.stateLog_);
            ret.vmName_ = this.vmName_;
            ret.vmVender_ = this.vmVender_;
            ret.vmVersion_ = this.vmVersion_;
        }
        catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException(ex);
        }
        
        return ret;
    }
    
}
