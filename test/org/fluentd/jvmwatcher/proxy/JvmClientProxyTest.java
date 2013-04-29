/**
 * 
 */
package org.fluentd.jvmwatcher.proxy;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Collection;
import java.util.Map;

import javax.management.ObjectName;

import org.fluentd.jvmwatcher.LocalJvmInfo;
import org.fluentd.jvmwatcher.data.MemoryPoolState;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.management.HotSpotDiagnosticMXBean;

/**
 * @author miyake
 *
 */
public class JvmClientProxyTest
{
    static Map<Integer, LocalJvmInfo>       jvmMap_ = null;
    static LocalJvmInfo                     target_ = null;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        jvmMap_ = LocalJvmInfo.getAllLocalJvmInfos();
        for(Map.Entry<Integer, LocalJvmInfo> elem : jvmMap_.entrySet())
        {
            int             key = elem.getKey();
            LocalJvmInfo    val = elem.getValue();

            try
            {
                val.startManagementAgent();
                target_ = val;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
            if (target_ != null)
            {
                System.out.println("DisplayName=" + target_.getDisplayName());
                System.out.println("CommandLine=" + target_.getCommandLine_());
                System.out.println("vmid=" + target_.getJvmid());
                
                break;
            }
        }
    }

    /**
     * {@link org.fluentd.jvmwatcher.proxy.JvmClientProxy#connect()} のためのテスト・メソッド。
     */
    @Test
    public void testConnect()
    {
        if (null == target_)
        {
            fail("LocalJvmInfo is NULL!!!!");
            return ;
        }

        JvmClientProxy      clientProxy = new JvmClientProxy(target_);

        assertFalse(clientProxy.isConnect());
        
        boolean     connState = clientProxy.connect();
        
        assertTrue(connState);
        assertTrue(clientProxy.isConnect());

        try
        {
            String[]    domains = clientProxy.getDomains();
            
            if (domains != null)
            {
                for (String elem : domains)
                {
                    System.out.println("domain=" + elem);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        boolean     disconnState = clientProxy.disconnect();
        assertTrue(disconnState);
        assertFalse(clientProxy.isConnect());
    }

    @Test
    public void testGetClassLoadingMXBean()
    {
        if (null == target_)
        {
            fail("LocalJvmInfo is NULL!!!!");
            return ;
        }

        JvmClientProxy      clientProxy = new JvmClientProxy(target_);

        assertFalse(clientProxy.isConnect());
        
        boolean     connState = clientProxy.connect();
        
        assertTrue(connState);
        assertTrue(clientProxy.isConnect());

        try
        {
            ClassLoadingMXBean  bean = clientProxy.getClassLoadingMXBean();
            
            int loadedCount = bean.getLoadedClassCount();
            long totalLoadedCount = bean.getTotalLoadedClassCount();
            long unloadedCount = bean.getUnloadedClassCount();
            
            System.out.print("[ClassLoadingMXBean] loadedCount=" + loadedCount);
            System.out.print(" totalLoadedCount=" + totalLoadedCount);
            System.out.println(" totalLoadedCount=" + unloadedCount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
        
        boolean     disconnState = clientProxy.disconnect();
        assertTrue(disconnState);
        assertFalse(clientProxy.isConnect());
    }

    @Test
    public void testGetCompilationMXBean()
    {
        if (null == target_)
        {
            fail("LocalJvmInfo is NULL!!!!");
            return ;
        }

        JvmClientProxy      clientProxy = new JvmClientProxy(target_);

        assertFalse(clientProxy.isConnect());
        
        boolean     connState = clientProxy.connect();
        
        assertTrue(connState);
        assertTrue(clientProxy.isConnect());

        try
        {
            CompilationMXBean  bean = clientProxy.getCompilationMXBean();
            
            String      jitName = bean.getName();
            long        compileTime = bean.getTotalCompilationTime();

            System.out.print("[CompilationMXBean] getName=" + jitName);
            System.out.println(" getTotalCompilationTime=" + compileTime);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
        
        boolean     disconnState = clientProxy.disconnect();
        assertTrue(disconnState);
        assertFalse(clientProxy.isConnect());
    }

    @Test
    public void testGetMemoryMXBean()
    {
        if (null == target_)
        {
            fail("LocalJvmInfo is NULL!!!!");
            return ;
        }

        JvmClientProxy      clientProxy = new JvmClientProxy(target_);

        assertFalse(clientProxy.isConnect());
        
        boolean     connState = clientProxy.connect();
        
        assertTrue(connState);
        assertTrue(clientProxy.isConnect());

        try
        {
            MemoryMXBean  bean = clientProxy.getMemoryMXBean();

            MemoryUsage     heapSize = bean.getHeapMemoryUsage();
            MemoryUsage     notheapSize = bean.getNonHeapMemoryUsage();
            int             pendingFinalizationCount = bean.getObjectPendingFinalizationCount();

            System.out.print("[MemoryMXBean] heapSize=" + heapSize.toString());
            System.out.print(" notheapSize=" + notheapSize.toString());
            System.out.println(" pendingFinalizationCount=" + pendingFinalizationCount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
        
        boolean     disconnState = clientProxy.disconnect();
        assertTrue(disconnState);
        assertFalse(clientProxy.isConnect());
    }

    @Test
    public void testGetRuntimeMXBean()
    {
        if (null == target_)
        {
            fail("LocalJvmInfo is NULL!!!!");
            return ;
        }

        JvmClientProxy      clientProxy = new JvmClientProxy(target_);

        assertFalse(clientProxy.isConnect());
        
        boolean     connState = clientProxy.connect();
        
        assertTrue(connState);
        assertTrue(clientProxy.isConnect());

        try
        {
            RuntimeMXBean  bean = clientProxy.getRuntimeMXBean();
            
            String      bootClassPath = bean.getBootClassPath();
            String      classPath = bean.getClassPath();
            String      name = bean.getName();
            long        startTime = bean.getStartTime();
            long        upTime = bean.getUptime();
            String      vmname = bean.getVmName();
            String      vender = bean.getVmVendor();
            String      version = bean.getVmVersion();
            String      specname = bean.getSpecName();
            String      specvender = bean.getSpecVendor();
            String      specversion = bean.getSpecVersion();

            System.out.print("[RuntimeMXBean] getName=" + name);
            System.out.print(" bootClassPath=" + bootClassPath);
            System.out.print("          classPath=" + classPath);
            System.out.print(" startTime=" + startTime);
            System.out.print(" upTime=" + upTime);
            System.out.print(" vmname=" + vmname);
            System.out.print(" vender=" + vender);
            System.out.print(" version=" + version);
            System.out.print(" specname=" + specname);
            System.out.print(" specvender=" + specvender);
            System.out.println(" specversion=" + specversion);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
        
        boolean     disconnState = clientProxy.disconnect();
        assertTrue(disconnState);
        assertFalse(clientProxy.isConnect());
    }

    
    @Test
    public void testGetThreadMXBean()
    {
        if (null == target_)
        {
            fail("LocalJvmInfo is NULL!!!!");
            return ;
        }

        JvmClientProxy      clientProxy = new JvmClientProxy(target_);

        assertFalse(clientProxy.isConnect());
        
        boolean     connState = clientProxy.connect();
        
        assertTrue(connState);
        assertTrue(clientProxy.isConnect());

        try
        {
            ThreadMXBean  bean = clientProxy.getThreadMXBean();

            int     daemonThreadCount = bean.getDaemonThreadCount();
            int     peakThreadCount = bean.getPeakThreadCount();
            int     threadCount = bean.getThreadCount();

            System.out.print("[ThreadMXBean] daemonThreadCount=" + daemonThreadCount);
            System.out.print(" peakThreadCount=" + peakThreadCount);
            System.out.println(" threadCount=" + threadCount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
        
        boolean     disconnState = clientProxy.disconnect();
        assertTrue(disconnState);
        assertFalse(clientProxy.isConnect());
    }


    @Test
    public void testGetOperatingSystemMXBean()
    {
        if (null == target_)
        {
            fail("LocalJvmInfo is NULL!!!!");
            return ;
        }

        JvmClientProxy      clientProxy = new JvmClientProxy(target_);

        assertFalse(clientProxy.isConnect());
        
        boolean     connState = clientProxy.connect();
        
        assertTrue(connState);
        assertTrue(clientProxy.isConnect());

        try
        {
            OperatingSystemMXBean  bean = clientProxy.getOperatingSystemMXBean();

            String      arch = bean.getArch();
            int         availableProcessors = bean.getAvailableProcessors();
            String      name = bean.getName();
            double      systemLoadAverage = bean.getSystemLoadAverage();
            String      version = bean.getVersion();

            System.out.print("[OperatingSystemMXBean] arch=" + arch);
            System.out.print(" availableProcessors=" + availableProcessors);
            System.out.print(" name=" + name);
            System.out.print(" systemLoadAverage=" + systemLoadAverage);
            System.out.println(" version=" + version);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
        
        boolean     disconnState = clientProxy.disconnect();
        assertTrue(disconnState);
        assertFalse(clientProxy.isConnect());
    }

    @Test
    public void testGetSunOperatingSystemMXBean()
    {
        if (null == target_)
        {
            fail("LocalJvmInfo is NULL!!!!");
            return ;
        }

        JvmClientProxy      clientProxy = new JvmClientProxy(target_);

        assertFalse(clientProxy.isConnect());
        
        boolean     connState = clientProxy.connect();
        
        assertTrue(connState);
        assertTrue(clientProxy.isConnect());

        try
        {
            com.sun.management.OperatingSystemMXBean  bean = clientProxy.getSunOperatingSystemMXBean();

            String      arch = bean.getArch();
            int         availableProcessors = bean.getAvailableProcessors();
            String      name = bean.getName();
            double      systemLoadAverage = bean.getSystemLoadAverage();
            String      version = bean.getVersion();

            long        committedVirtualMemorySize = bean.getCommittedVirtualMemorySize();
            long        freePhysicalMemorySize = bean.getFreePhysicalMemorySize();
            long        freeSwapSpaceSize = bean.getFreeSwapSpaceSize();
            long        processCpuTime = bean.getProcessCpuTime();
            long        totalPhysicalMemorySize = bean.getTotalPhysicalMemorySize();
            long        totalSwapSpaceSize = bean.getTotalSwapSpaceSize();

            System.out.print("[SunOperatingSystemMXBean] arch=" + arch);
            System.out.print(" availableProcessors=" + availableProcessors);
            System.out.print(" name=" + name);
            System.out.println(" systemLoadAverage=" + systemLoadAverage);
            System.out.print("[SunOperatingSystemMXBean] committedVirtualMemorySize=" + committedVirtualMemorySize);
            System.out.print(" freePhysicalMemorySize=" + freePhysicalMemorySize);
            System.out.print(" freeSwapSpaceSize=" + freeSwapSpaceSize);
            System.out.print(" processCpuTime=" + processCpuTime);
            System.out.print(" totalPhysicalMemorySize=" + totalPhysicalMemorySize);
            System.out.print(" totalSwapSpaceSize=" + totalSwapSpaceSize);
            System.out.println(" version=" + version);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
        
        boolean     disconnState = clientProxy.disconnect();
        assertTrue(disconnState);
        assertFalse(clientProxy.isConnect());
    }

    @Test
    public void testGetHotSpotDiagnosticMXBean()
    {
        if (null == target_)
        {
            fail("LocalJvmInfo is NULL!!!!");
            return ;
        }

        JvmClientProxy      clientProxy = new JvmClientProxy(target_);

        assertFalse(clientProxy.isConnect());
        
        boolean     connState = clientProxy.connect();
        
        assertTrue(connState);
        assertTrue(clientProxy.isConnect());

        try
        {
            HotSpotDiagnosticMXBean  bean = clientProxy.getHotSpotDiagnosticMXBean();

            assertNotNull(bean);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
        
        boolean     disconnState = clientProxy.disconnect();
        assertTrue(disconnState);
        assertFalse(clientProxy.isConnect());
    }

    @Test
    public void testGetGarbageCollectorMXBeans()
    {
        if (null == target_)
        {
            fail("LocalJvmInfo is NULL!!!!");
            return ;
        }

        JvmClientProxy      clientProxy = new JvmClientProxy(target_);

        assertFalse(clientProxy.isConnect());
        
        boolean     connState = clientProxy.connect();
        
        assertTrue(connState);
        assertTrue(clientProxy.isConnect());

        try
        {
            Collection<GarbageCollectorMXBean>  beans = clientProxy.getGarbageCollectorMXBeans();

            for (GarbageCollectorMXBean elem : beans)
            {
                long    collectionCount = elem.getCollectionCount();
                long    collectionTime = elem.getCollectionTime();
                String  memoryManagerName = elem.getName();

                System.out.print("[GarbageCollectorMXBean] memoryManagerName=" + memoryManagerName);
                System.out.print(" collectionCount=" + collectionCount);
                System.out.println(" collectionTime=" + collectionTime);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
        
        boolean     disconnState = clientProxy.disconnect();
        assertTrue(disconnState);
        assertFalse(clientProxy.isConnect());
    }

    @Test
    public void testGetMemoryPoolClientProxies()
    {
        if (null == target_)
        {
            fail("LocalJvmInfo is NULL!!!!");
            return ;
        }

        JvmClientProxy      clientProxy = new JvmClientProxy(target_);

        assertFalse(clientProxy.isConnect());
        
        boolean     connState = clientProxy.connect();
        
        assertTrue(connState);
        assertTrue(clientProxy.isConnect());

        try
        {
            Collection<MemoryPoolClientProxy>  beans = clientProxy.getMemoryPoolClientProxies();

            for (MemoryPoolClientProxy elem : beans)
            {
                MemoryPoolState     state = elem.getStat();
                
                assertNotNull(state);

                System.out.print("[MemoryPoolClientProxy] poolName=" + state.getPoolName());

                System.out.print(" getCollectThreshold=" + state.getCollectThreshold());
                System.out.print(" getLastGcStartTime=" + state.getLastGcStartTime());
                System.out.print(" getLastGcEndTime=" + state.getLastGcEndTime());
                System.out.print(" getLastGcId=" + state.getLastGcId());
                System.out.println(" getUsageThreshold=" + state.getUsageThreshold());
                
                MemoryUsage     afterGcUsage = state.getAfterGcUsage();
                MemoryUsage     beforeGcUsage = state.getBeforeGcUsage();
                if (afterGcUsage != null)
                    System.out.println("[MemoryPoolClientProxy] afterGcUsage=" + afterGcUsage.toString());
                if (beforeGcUsage != null)
                    System.out.println("[MemoryPoolClientProxy] beforeGcUsage=" + beforeGcUsage.toString());
                
                System.out.println("-----");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
        
        boolean     disconnState = clientProxy.disconnect();
        assertTrue(disconnState);
        assertFalse(clientProxy.isConnect());
    }
    
}
