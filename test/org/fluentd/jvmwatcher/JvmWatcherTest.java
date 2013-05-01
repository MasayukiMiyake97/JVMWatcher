package org.fluentd.jvmwatcher;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.fluentd.jvmwatcher.data.JvmWatchState;
import org.fluentd.jvmwatcher.proxy.JvmClientProxy;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JvmWatcherTest
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

    
    @Test
    public void testGetHostName()
    {
        String  hostname = JvmWatcher.getHostName("unknown");

        assertNotNull(hostname);
        System.out.println("hostname=" + hostname);
    }
    
    @Test
    public void testMakeJvmWatchState()
    {
        for (JvmClientProxy elem : proxyArray_)
        {
            JvmWatchState   state = JvmWatchState.makeJvmWatchState(elem);
            assertNotNull(state);
            
            System.out.println(" displayName=" + state.getDisplayName() + 
                               " getVmName=" + state.getVmName() + 
                               " getVmVender=" + state.getVmVender() + 
                               " getVmVersion=" + state.getVmVersion());
        }
    }

}
