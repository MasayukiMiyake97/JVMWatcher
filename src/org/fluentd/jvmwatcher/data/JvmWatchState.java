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

import org.fluentd.jvmwatcher.proxy.JvmClientProxy;

/**
 * This class holds a snapshot of the state of the JVM. Date and get the state, the CPU utilization, such as a command name is stored.
 * @author miyake
 *
 */
public class JvmWatchState
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
    private String      commandLine_ = null;
    /**
     * Java program display name 
     */
    private String      displayName_ = null;
    /**
     * Java program short name 
     */
    private String      shortName_ = null;
    /**
     * Java VM ID (pid)
     */
    private int         jvmId_ = -1;

    // CompilationMXBean
    private String      jitName_ = null;
    // OperatingSystemMXBean
    private String      osArch_ = null;
    private String      osName_ = null;
    private String      osVersion_ = null;
    // RuntimeMXBean
    private long        jvmStartTime = -1L;
    private String      jvmRuntimeName = null;
    private String      vmName = null;
    private String      vmVender = null;
    private String      vmVersion = null;
    private String      specName = null;
    private String      specVender = null;
    private String      specVersion = null;

    private ArrayList<JvmStateLog>  stateLog_ = null;

    // CPU usage
    private long        prevUpTime_ = 0L;
    private long        prevProcessCpuTime_ = 0L;
    
    /**
     * 
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
                ret.jvmStartTime = runtimeBean.getStartTime();
                ret.jvmRuntimeName = runtimeBean.getName();
                ret.vmName = runtimeBean.getVmName();
                ret.vmVender = runtimeBean.getVmVendor();
                ret.vmVersion = runtimeBean.getVmVersion();
                ret.specName = runtimeBean.getSpecName();
                ret.specVender = runtimeBean.getSpecVendor();
                ret.specVersion = runtimeBean.getSpecVersion();
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
        return jvmStartTime;
    }

    /**
     * @return jvmRuntimeName
     */
    public String getJvmRuntimeName()
    {
        return jvmRuntimeName;
    }

    /**
     * @return vmName
     */
    public String getVmName()
    {
        return vmName;
    }

    /**
     * @return vmVender
     */
    public String getVmVender()
    {
        return vmVender;
    }

    /**
     * @return vmVersion
     */
    public String getVmVersion()
    {
        return vmVersion;
    }

    /**
     * @return specName
     */
    public String getSpecName()
    {
        return specName;
    }

    /**
     * @return specVender
     */
    public String getSpecVender()
    {
        return specVender;
    }

    /**
     * @return specVersion
     */
    public String getSpecVersion()
    {
        return specVersion;
    }

    /**
     * @return stateLog
     */
    public ArrayList<JvmStateLog> getStateLog()
    {
        return stateLog_;
    }
    
}
