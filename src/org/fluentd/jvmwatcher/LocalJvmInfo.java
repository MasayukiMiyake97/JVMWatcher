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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;

import sun.management.ConnectorAddressLink;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.VmIdentifier;

/**
 * Local Java VM Information class.
 * @author miyake
 *
 */
public class LocalJvmInfo
{
    /**
     * JVM connect address
     */
    private String  mbeanAddress_ = null;
    /**
     * Java program command line 
     */
    private String  commandLine_ = null;
    /**
     * Java program display name 
     */
    private String  displayName_ = null;
    /**
     * Java VM ID (pid)
     */
    private int     jvmId_ = -1;
    /**
     * The short process name for the log output.
     */
    private String  shortName_ = null;
    /**
     * is JVM attach supported
     */
    private boolean isAttachSupported_ = false;

    /**
     * Local JVM connect address.
     */
    private static final    String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";
    
    /**
     * Constructor
     * 
     * @param vmid
     * @param commandLine
     * @param canAttach
     * @param connectorAddress
     */
    public LocalJvmInfo(int vmid, String commandLine, boolean canAttach, String connectorAddress)
    {
        this.jvmId_ = vmid;
        this.commandLine_ = commandLine;
        this.isAttachSupported_ = canAttach;
        this.mbeanAddress_ = connectorAddress;
        this.displayName_ = getDisplayName(commandLine);
        this.shortName_ = this.displayName_;
    }
    
    /**
     * Default Constructor
     */
    private LocalJvmInfo()
    {
        
    }
    
    /**
     * get All local JVM information.
     * 
     * @return
     */
    public static Map<Integer, LocalJvmInfo> getAllLocalJvmInfos()
    {
        Map<Integer, LocalJvmInfo> map = new HashMap<Integer, LocalJvmInfo>();
        getMonitoredJvms(map);
        getAttachableJvms(map);
        return map;
    }
    
    /**
     * Monitored Local Java VM get information.
     * @param map
     */
    private static void getMonitoredJvms(Map<Integer, LocalJvmInfo> map)
    {
        MonitoredHost   monHost = null;
        Set<Integer>     activVmsSet = null;

        try 
        {
            // get monitored host jvms
            monHost = MonitoredHost.getMonitoredHost(new HostIdentifier((String)null));
            activVmsSet = monHost.activeVms();
        } 
        catch (java.net.URISyntaxException sx) 
        {
            System.err.println(sx.toString());
            return ;
        } 
        catch (MonitorException mx) 
        {
            System.err.println(mx.toString());
            return ;
        }
        
        for (Object jvmid: activVmsSet)
        {
            if (jvmid instanceof Integer)
            {
                int     pid = ((Integer) jvmid).intValue();
                String  name = jvmid.toString();
                boolean attachable = false;
                String  address = null;
                try 
                {
                     MonitoredVm mvm = monHost.getMonitoredVm(new VmIdentifier(name));
                     // use the command line as the display name
                     name =  MonitoredVmUtil.commandLine(mvm);
                     attachable = MonitoredVmUtil.isAttachable(mvm);
                     address = ConnectorAddressLink.importFrom(pid);
                     
                     mvm.detach();
                }
                catch (Exception ex) 
                {
                    System.err.println(ex.toString());
                }
                // put LocalJvmInfo
                map.put((Integer) jvmid, new LocalJvmInfo(pid, name, attachable, address));
            }
        }
    }

    /**
     * Attachable Local Java VM get information.
     * @param map
     */
    private static void getAttachableJvms(Map<Integer, LocalJvmInfo> map)
    {
        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        for (VirtualMachineDescriptor vmd : vms)
        {
            try
            {
                Integer vmid = Integer.valueOf(vmd.id());
                if (!map.containsKey(vmid))
                {
                    boolean attachable = false;
                    String address = null;
                    try 
                    {
                        VirtualMachine vm = VirtualMachine.attach(vmd);
                        attachable = true;
                        Properties agentProps = vm.getAgentProperties();
                        address = (String) agentProps.get(LOCAL_CONNECTOR_ADDRESS_PROP);
                        vm.detach();
                    }
                    catch (AttachNotSupportedException ex)
                    {
                        // not attachable
                        System.err.println(ex.toString());
                    }
                    catch (IOException ioex)
                    {
                        // ignore
                        System.err.println(ioex.toString());
                    }
                    // put LocalJvmInfo
                    map.put(vmid, new LocalJvmInfo(vmid.intValue(), vmd.displayName(), attachable, address));
                }
            }
            catch (NumberFormatException numex)
            {
                // do not support vmid different than pid
                System.err.println(numex.toString());
            }
        }
    }

    /**
     * CommandLine string convert to display name.
     * @param commandLine
     * @return
     */
    private String getDisplayName(String commandLine)
    {
        // trim the pathname of jar file if it's a jar
        String[] res = commandLine.split(" ", 2);
        if (res[0].endsWith(".jar"))
        {
           File     jarfile = new File(res[0]);
           String   displayName = jarfile.getName();
           if (res.length == 2)
           {
               displayName += " " + res[1];
           }
           return displayName;
        }
        return commandLine;
    }

    /**
     * Start JVM management agent.
     * @throws IOException
     */
    public void startManagementAgent() throws IOException
    {
        if (this.mbeanAddress_ != null)
        {
            // already started
            return;
        }

        if (!this.isAttachSupported_)
        {
            throw new IOException("This virtual machine \"" + this.jvmId_ + "\" does not support dynamic attach.");
        }

        // load management agent.
        loadManagementAgent();

        // fails to load or start the management agent
        if (this.mbeanAddress_ == null)
        {
            // should never reach here
            throw new IOException("Fails to find connector address");
        }
    }
    
    /**
     * Load JVM management agent.
     * @throws IOException
     */
    private void loadManagementAgent() throws IOException {
        VirtualMachine  vm = null;
        String          name = String.valueOf(this.jvmId_);
        try 
        {
            vm = VirtualMachine.attach(name);
        }
        catch (AttachNotSupportedException x)
        {
            IOException ioe = new IOException(x.getMessage());
            ioe.initCause(x);
            throw ioe;
        }

        // create management-agent.jar path
        String home = vm.getSystemProperties().getProperty("java.home");

        // ${java.home}/jre/lib/management-agent.jar or ${java.home}/lib in build environments.
        String agent = home + File.separator + "jre" + File.separator + "lib" + File.separator + "management-agent.jar";
        File file = new File(agent);
        if (!file.exists())
        {
            agent = home + File.separator +  "lib" + File.separator + "management-agent.jar";
            file = new File(agent);
            if (!file.exists())
            {
                throw new IOException("Management agent not found");
            }
        }

        agent = file.getCanonicalPath();
        try
        {
            vm.loadAgent(agent, "com.sun.management.jmxremote");
        }
        catch (AgentLoadException ex)
        {
            IOException ioe = new IOException(ex.getMessage());
            ioe.initCause(ex);
            throw ioe;
        }
        catch (AgentInitializationException ex)
        {
            IOException ioe = new IOException(ex.getMessage());
            ioe.initCause(ex);
            throw ioe;
        }

        // get the connector address
        Properties agentProps = vm.getAgentProperties();
        this.mbeanAddress_ = (String) agentProps.get(LOCAL_CONNECTOR_ADDRESS_PROP);

        vm.detach();
    }
    
    /**
     * @return MBean Address
     */
    public String getAddress()
    {
        return mbeanAddress_;
    }

    /**
     * @return commandLine
     */
    public String getCommandLine_()
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
     * @return JavaVM ID
     */
    public int getJvmid()
    {
        return jvmId_;
    }

    /**
     * @return isAttachSupported
     */
    public boolean isAttachSupported()
    {
        return isAttachSupported_;
    }
    
    /**
     * Manageable check
     * @return
     */
    public boolean isManageable()
    {
        return (this.mbeanAddress_ != null);
    }

    /**
     * set short name.
     * @param name
     */
    public void setShortName(String name)
    {
        this.shortName_ = name;
    }
    
    /**
     * get short name.
     * @return
     */
    public String getShortName()
    {
        return this.shortName_;
    }
    
    /* (非 Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer    buff = new StringBuffer();
        
        buff.append("vmid=").append(this.jvmId_).append(" address=").append(this.mbeanAddress_);
        buff.append(" commanLine=").append(this.commandLine_).append(" displayName=").append(this.displayName_);
        buff.append(" isAttachSuppoted=").append(this.isAttachSupported_);

        return buff.toString();
    }
}
