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

import static org.junit.Assert.assertFalse;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.fluentd.jvmwatcher.data.JvmWatchState;
import org.fluentd.jvmwatcher.parser.AbstractStateParser;
import org.fluentd.jvmwatcher.parser.JsonSimpleLogParser;
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
    
    private     long        refindJvmInterval_ = 60000L;
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        JvmWatcher      jvmWatcher = new JvmWatcher();
        // load configuration
        
        // load target filter
        
        // start OutputParseThread
        AbstractStateParser     parser = new JsonSimpleLogParser();

        parser.setHostName(jvmWatcher.getHostName(null));
        jvmWatcher.jsonOutputParseTread_ = new OutputParseThread(jvmWatcher, jvmWatcher.queue_, parser);
        // start thread
        Thread      thread = new Thread(jvmWatcher.jsonOutputParseTread_);
        thread.start();
        
        // loop
        jvmWatcher.runProcess();

    }
    
    /**
     * @param pid
     */
    public void removeJvmWatchThread(int pid)
    {
        synchronized(this.jvmProcessorMap_)
        {
            this.jvmProcessorMap_.remove(pid);
        }
        return ;
    }

    /**
     * @param pid
     * @param info
     */
    private void startJvmWatchThread(int pid, LocalJvmInfo info)
    {
        synchronized(this.jvmProcessorMap_)
        {
            JvmWatchThread  watchThread = this.jvmProcessorMap_.get(pid);
            // is new JVM
            if (null == watchThread)
            {
                JvmClientProxy      clientProxy = new JvmClientProxy(info);
                // connect JVM
                boolean     connState = clientProxy.connect();
                if (connState == true)
                {
                    // start JvmWatchThread
                    JvmWatchThread  newWatchThread = new JvmWatchThread(this, this.queue_, clientProxy);
                    // set interval time
                    //
                    
                    // regist Process map.
                    this.jvmProcessorMap_.put(pid, newWatchThread);
                    // start thread
                    Thread      thread = new Thread(newWatchThread);
                    thread.start();
                }
            }
        }

        return;
    }
    
    /**
     * 
     */
    public void runProcess()
    {
        while (true)
        {
            long    startTime = System.currentTimeMillis();
            // get LocalJvmInfo
            Map<Integer, LocalJvmInfo>  jvmInfoMap = LocalJvmInfo.getAllLocalJvmInfos();
            for(Map.Entry<Integer, LocalJvmInfo> elem : jvmInfoMap.entrySet())
            {
                int             key = elem.getKey();
                LocalJvmInfo    val = elem.getValue();

                // create and start JvmWatchThread.
                this.startJvmWatchThread(key, val);
            }
            long    endTime = System.currentTimeMillis();
            long    watiTime = endTime - startTime;

            // wait interval time.
            try
            {
                Thread.sleep(this.refindJvmInterval_ - watiTime);
            }
            catch (InterruptedException ex)
            {
                System.exit(1);
            }
        }
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
