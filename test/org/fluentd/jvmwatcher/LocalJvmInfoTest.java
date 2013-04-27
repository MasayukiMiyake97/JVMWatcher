package org.fluentd.jvmwatcher;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class LocalJvmInfoTest
{
    Map<Integer, LocalJvmInfo>      jvmMap_ = null;

    @Before
    public void setUp() throws Exception
    {
        this.jvmMap_ = LocalJvmInfo.getAllLocalJvmInfos();
        
    }

    @Test
    public void testStartManagementAgent()
    {
        for(Map.Entry<Integer, LocalJvmInfo> elem : this.jvmMap_.entrySet())
        {
            int             key = elem.getKey();
            LocalJvmInfo    val = elem.getValue();

            try
            {
                val.startManagementAgent();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testGetAddress()
    {
        for(Map.Entry<Integer, LocalJvmInfo> elem : this.jvmMap_.entrySet())
        {
            int             key = elem.getKey();
            LocalJvmInfo    val = elem.getValue();
            
            System.out.println("pid=" + key + " get address =" + val.toString());
        }
    }

    @Test
    public void testGetCommandLine_()
    {
        for(Map.Entry<Integer, LocalJvmInfo> elem : this.jvmMap_.entrySet())
        {
            int             key = elem.getKey();
            LocalJvmInfo    val = elem.getValue();
            
            System.out.println("pid=" + key + " command line=" + val.getCommandLine_());
        }
    }

    @Test
    public void testGetDisplayName()
    {
        for(Map.Entry<Integer, LocalJvmInfo> elem : this.jvmMap_.entrySet())
        {
            int             key = elem.getKey();
            LocalJvmInfo    val = elem.getValue();
            
            System.out.println("pid=" + key + " DisplayName=" + val.getDisplayName());
        }
    }

    @Test
    public void testGetVmid()
    {
        for(Map.Entry<Integer, LocalJvmInfo> elem : this.jvmMap_.entrySet())
        {
            int             key = elem.getKey();
            LocalJvmInfo    val = elem.getValue();
            
            System.out.println("pid=" + key + " vmid=" + val.getJvmid());
        }
    }

    @Test
    public void testIsAttachSupported()
    {
        for(Map.Entry<Integer, LocalJvmInfo> elem : this.jvmMap_.entrySet())
        {
            int             key = elem.getKey();
            LocalJvmInfo    val = elem.getValue();
            
            System.out.println("pid=" + key + " AttachSupported=" + val.isAttachSupported());
        }
    }

    @Test
    public void testToString()
    {
        for(Map.Entry<Integer, LocalJvmInfo> elem : this.jvmMap_.entrySet())
        {
            int             key = elem.getKey();
            LocalJvmInfo    val = elem.getValue();
            
            System.out.println("pid=" + key + " val=" + val.toString());
        }
    }

}
