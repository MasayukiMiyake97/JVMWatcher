/**
 * 
 */
package org.fluentd.jvmwatcher.data;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.fluentd.jvmwatcher.LocalJvmInfo;
import org.fluentd.jvmwatcher.proxy.JvmClientProxy;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author miyake
 *
 */
public class JvmStateLogTest
{
    static Map<Integer, LocalJvmInfo>       jvmMap_ = null;
    static ArrayList<JvmClientProxy>        proxyArray_ = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        jvmMap_ = LocalJvmInfo.getAllLocalJvmInfos();
        proxyArray_ = new ArrayList<JvmClientProxy>();

        for(Map.Entry<Integer, LocalJvmInfo> elem : jvmMap_.entrySet())
        {
            LocalJvmInfo    val = elem.getValue();

            try
            {
                val.startManagementAgent();
                JvmClientProxy      clientProxy = new JvmClientProxy(val);
                assertFalse(clientProxy.isConnect());
                
                boolean     connState = clientProxy.connect();
                
                assertTrue(connState);
                assertTrue(clientProxy.isConnect());
                
                proxyArray_.add(clientProxy);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
        for (JvmClientProxy elem : proxyArray_)
        {
            boolean     disconnState = elem.disconnect();
            assertTrue(disconnState);
            assertFalse(elem.isConnect());
        }
    }

    /**
     * {@link org.fluentd.jvmwatcher.data.JvmStateLog#makeJvmStateLog(org.fluentd.jvmwatcher.proxy.JvmClientProxy)} のためのテスト・メソッド。
     */
    @Test
    public void testMakeJvmStateLog()
    {
        for (JvmClientProxy elem : proxyArray_)
        {
            JvmStateLog     log = JvmStateLog.makeJvmStateLog(elem);
            assertNotNull(log);
            assertNotNull(log.getNotheapSize());
            assertNotNull(log.getHeapSize());
            assertNotNull(log.getGcStateCollection());
            assertNotNull(log.getMemoryPoolStateCollection());
            
            System.out.println(" getJvmUpTime=" + log.getJvmUpTime() + 
                    " getLogDateTime=" + log.getLogDateTime() + 
                    " getProcessCpuTime=" + log.getProcessCpuTime() + 
                    " getNotheapSize=" + log.getNotheapSize().toString() + 
                    " getHeapSize=" + log.getHeapSize().toString());

        }
    }

}
