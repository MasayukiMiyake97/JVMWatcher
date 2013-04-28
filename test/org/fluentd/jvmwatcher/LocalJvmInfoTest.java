package org.fluentd.jvmwatcher;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class LocalJvmInfoTest
{

    @Test
    public void testStartManagementAgent()
    {
        Map<Integer, LocalJvmInfo>      jvmMap = LocalJvmInfo.getAllLocalJvmInfos();
        for(Map.Entry<Integer, LocalJvmInfo> elem : jvmMap.entrySet())
        {
            int             key = elem.getKey();
            LocalJvmInfo    val = elem.getValue();

            try
            {
                System.out.println("key=" + key + " isManageable=" + val.isManageable() +
                                   " isAttachSupported=" + val.isAttachSupported() +
                                   " isCommandLine=" + val.getCommandLine_() +
                                   " address=" + val.getAddress());
                
                val.startManagementAgent();

                System.out.println("-- after startManagementAgent ");
                System.out.println("key=" + key + " isManageable=" + val.isManageable() +
                        " isAttachSupported=" + val.isAttachSupported() +
                        " isCommandLine=" + val.getCommandLine_() +
                        " address=" + val.getAddress());
     
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

}
