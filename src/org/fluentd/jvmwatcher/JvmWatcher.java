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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.fluentd.jvmwatcher.data.JvmWatchState;
import org.fluentd.jvmwatcher.proxy.JvmClientProxy;

/**
 * JVM Watcher Main Class
 * @author miyake
 *
 */
public class JvmWatcher
{
    private     Map<Integer, JvmWatchThread>    jvmProcessorMap_ = new HashMap<Integer, JvmWatchThread>();
    private     OutputParseThread               jsonOutputParseTread_ = null;
    private     BlockingQueue<JvmWatchState>    queue_ = new LinkedBlockingQueue<JvmWatchState>();
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // load configuration
        
        // load target filter
        
        // loop
        
        
        

    }

    
    /**
     * @param jvmInfo
     * @return
     */
    public int addJvmWatchThread(LocalJvmInfo jvmInfo)
    {
        return 0;
    }
    
    /**
     * @param pid
     * @return
     */
    public int removeJvmWatchThread(int pid)
    {
        return 0;
    }

    /**
     * 
     */
    public void runProcess()
    {
        
    }
    
    
    /**
     * @return
     */
    public String getHostName(String defaultName)
    {
        String      ret = null;
        
        try
        {
            ret = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException ex)
        {
            if (defaultName != null)
            {
                ret = defaultName;
            }
            System.err.println("unknown hostname. check to /etc/hosts.  Exception=" + ex);
        }
        return ret;
    }

    
    
    
}
