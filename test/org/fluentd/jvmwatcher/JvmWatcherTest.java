package org.fluentd.jvmwatcher;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.fluentd.jvmwatcher.data.JvmWatchState;
import org.fluentd.jvmwatcher.proxy.JvmClientProxy;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JvmWatcherTest
{
    
    @Test
    public void testGetHostName()
    {
        JvmWatcher  src = new JvmWatcher();
        
        String  hostname = src.getHostName("test");

        assertNotNull(hostname);
        System.out.println("hostname=" + hostname);
    }

    @Test
    public void testparseConfig()
    {
        JvmWatcher  src = new JvmWatcher();

        src.loadProperty("nfig.json");
        src.loadProperty("config/config.json");

        Map<Integer, LocalJvmInfo>  allJvm = LocalJvmInfo.getAllLocalJvmInfos();
        
        System.out.println("-- process filter -");
        for(Map.Entry<Integer, LocalJvmInfo> elem : allJvm.entrySet())
        {
            int             key = elem.getKey();
            LocalJvmInfo    val = elem.getValue();
            
            System.out.println("target shortname=" + val.getShortName() + " process name=" + val.getDisplayName());
        }
        System.out.println("-- process filter -");
    }
    
}
