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
package org.fluentd.jvmwatcher;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import com.sun.management.HotSpotDiagnosticMXBean;


/**
 * This class manages the MXBens to obtain the status of the JVM. And I also do connection management of the JVM.<BR>
 * JvmClientProxy will correspond to only the local JVM.
 * @author miyake
 *
 */
public class JvmClientProxy
{
    /**
     * Java VM connection status.
     */
    private boolean             isConnect_ = false;
    private boolean             hasPlatformMXBeans_ = false;
    private boolean             hasHotSpotDiagnosticMXBean_ = false;
    private boolean             hasCompilationMXBean_ = false;
    private boolean             supportsLockUsage_ = false;

    /**
     * Local Java VM connection information.
     */
    private LocalJvmInfo        localJvmInfo_ = null;
    
    // JVM server connection information
    private JMXServiceURL       jmxUrl_ = null;
    private JMXConnector        jmxc_ = null;

    // JVM MBean Server connection
    private MBeanServerConnection   server_ = null;

    // JMX Beans
    private ClassLoadingMXBean  classLoadingMBean_ = null;
    private CompilationMXBean   compilationMBean_ = null;
    private MemoryMXBean        memoryMBean_ = null;
    private OperatingSystemMXBean   operatingSystemMBean_ = null;
    private RuntimeMXBean       runtimeMBean_ = null;
    private ThreadMXBean        threadMBean_ = null;

    private com.sun.management.OperatingSystemMXBean    sunOperatingSystemMXBean_ = null;
    private HotSpotDiagnosticMXBean                     hotspotDiagnosticMXBean_ = null;

    private List<MemoryPoolClientProxy>                memoryPoolList_ = null;
    private List<GarbageCollectorMXBean>    garbageCollectorMBeanList_ = null;
    private String                          detectDeadlocksOperation_ = null;

    final static private String HOTSPOT_DIAGNOSTIC_MXBEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";
    
    
    /**
     * @param localJvmInfo
     */
    public JvmClientProxy(LocalJvmInfo localJvmInfo)
    {
        this.localJvmInfo_ = localJvmInfo;
    }

    public boolean connect()
    {
        boolean     ret = false;

        // connect the JVM server
        
        return ret;
    }
    
    public boolean disconnect()
    {
        boolean     ret = false;

        // disconnect the JVM server
        
        return ret;
    }
    
    public boolean isConnect()
    {
        return this.isConnect_;
    }
    
}
