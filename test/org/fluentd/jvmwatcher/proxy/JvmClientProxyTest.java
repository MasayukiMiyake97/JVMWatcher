/**
 * 
 */
package org.fluentd.jvmwatcher.proxy;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.fluentd.jvmwatcher.LocalJvmInfo;
import org.junit.BeforeClass;
import org.junit.Test;

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

}
