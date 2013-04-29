package org.fluentd.jvmwatcher;

import static org.junit.Assert.*;

import org.junit.Test;

public class JvmWatcherTest
{

    @Test
    public void testGetHostName()
    {
        String  hostname = JvmWatcher.getHostName("unknown");

        assertNotNull(hostname);
        System.out.println("hostname=" + hostname);
    }

}
