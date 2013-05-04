/**
 * 
 */
package org.fluentd.jvmwatcher.data;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.fluentd.jvmwatcher.LocalJvmInfo;
import org.fluentd.jvmwatcher.parser.JsonSimpleLogParser;
import org.fluentd.jvmwatcher.proxy.JvmClientProxy;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author miyake
 *
 */
public class JvmWatchStateTest
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
     * {@link org.fluentd.jvmwatcher.data.JvmWatchState#addStateLog(org.fluentd.jvmwatcher.data.JvmStateLog)} のためのテスト・メソッド。
     */
    @Test
    public void testAddStateLog()
    {

        JsonSimpleLogParser         parser = new JsonSimpleLogParser();
        ArrayList<JvmWatchState>    dataArray = new ArrayList<JvmWatchState>();
        PrintWriter                 writer = new PrintWriter(System.out);
        
        for (int cnt = 0; cnt < 3; cnt++)
        {
            for (JvmClientProxy elem : proxyArray_)
            {
                long    startTime = System.currentTimeMillis();
                JvmWatchState   state = JvmWatchState.makeJvmWatchState(elem);
                long    endTime = System.currentTimeMillis();
                assertNotNull(state);
                
                dataArray.add(state);
                
                System.out.println("makeJvmWatchState time=" + (endTime - startTime) + "(msec)");
                System.out.println("-----------------------------------------------------");
                System.out.println(" displayName=" + state.getDisplayName() + 
                                   " getVmName=" + state.getVmName() + 
                                   " getVmVender=" + state.getVmVender() + 
                                   " getVmVersion=" + state.getVmVersion());

                // make JvmStateLog
                startTime = System.currentTimeMillis();
                JvmStateLog     log = JvmStateLog.makeJvmStateLog(elem);
                endTime = System.currentTimeMillis();

                assertNotNull(log);
                assertNotNull(log.getNotheapSize());
                assertNotNull(log.getHeapSize());
                assertNotNull(log.getGcStateCollection());
                assertNotNull(log.getMemoryPoolStateCollection());

                System.out.println("makeJvmStateLog time=" + (endTime - startTime) + "(msec)");
                // add state log 
                state.addStateLog(log);

                Date    date = new Date(log.getLogDateTime());
                System.out.println("logtime=" + date.toString() + " getJvmUpTime=" + log.getJvmUpTime() + 
                        " getLogDateTime=" + log.getLogDateTime() + 
                        " getProcessCpuTime=" + log.getProcessCpuTime() + 
                        " getNotheapSize=" + log.getNotheapSize().toString() + 
                        " getHeapSize=" + log.getHeapSize().toString() +
                        " Cpu Usage=" + log.getCpuUsage());
                System.out.println("-----------------------------------------------------");
            }
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        long    startTime = System.currentTimeMillis();
        System.out.println("--parse---------------------------------------------------");
        parser.parseState(writer, dataArray);
        long    endTime = System.currentTimeMillis();
        System.out.println("--parse--- procTime = " + (endTime - startTime) + "(msec)");
    }

    /**
     * {@link org.fluentd.jvmwatcher.data.JvmWatchState#makeJvmWatchState(org.fluentd.jvmwatcher.proxy.JvmClientProxy)} のためのテスト・メソッド。
     */
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

    @Test
    public void testClone()
    {
        for (JvmClientProxy elem : proxyArray_)
        {
            JvmWatchState   state = JvmWatchState.makeJvmWatchState(elem);
            assertNotNull(state);

            JvmWatchState   clone = state.clone();
            
            assertNotNull(clone);
            assertNotSame(state,clone);
            assertEquals(state.getJitName(), clone.getJitName());
        }
        
    }
    
}
