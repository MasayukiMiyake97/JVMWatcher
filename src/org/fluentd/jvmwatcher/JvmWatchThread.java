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

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fluentd.jvmwatcher.data.JvmStateLog;
import org.fluentd.jvmwatcher.data.JvmWatchState;
import org.fluentd.jvmwatcher.data.JvmStateLog.ProcessState;
import org.fluentd.jvmwatcher.proxy.JvmClientProxy;

/**
 * @author miyake
 *
 */
public class JvmWatchThread implements Runnable
{
    private static  Log log = LogFactory.getLog(JvmWatchThread.class);
    
    private     BlockingQueue<JvmWatchState>    queue_ = null;
    private     JvmClientProxy                  jvmClient_ = null;
    
    private     long        watchInterval_ = 1000L; // msec
    private     int         logBuffNum_ = 3;
    
    private     JvmWatchState   watchState_ = null;
    private     JvmWatcher      parent_ = null;
    private     int             pid_ = -1;;
    
    /**
     * 
     */
    private JvmWatchThread()
    {
        
    }
    
    /**
     * @param parent
     * @param queue
     * @param proxy
     */
    public JvmWatchThread(JvmWatcher parent, BlockingQueue<JvmWatchState> queue, JvmClientProxy proxy)
    {
        this.parent_ = parent;
        this.queue_ = queue;
        this.jvmClient_ = proxy;
        
        this.pid_ = this.jvmClient_.getLocalJvmInfo().getJvmid();
    }
    
    /**
     * @param interval
     * @param buffNum
     */
    public void setInterval(long interval, int buffNum)
    {
        this.watchInterval_ = interval;
        this.logBuffNum_ = buffNum;
    }
    
    /* (非 Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        boolean         isProcess = true;
        int             logBuffCnt = 0;
        ProcessState    procState = ProcessState.START_PROCESS;
        
        // JVM watch start
        this.watchState_ = JvmWatchState.makeJvmWatchState(this.jvmClient_);
        // disconnect
        if (this.jvmClient_.isConnect() == false)
        {
            return ;
        }
        
        while (isProcess)
        {
            long    startTime = System.currentTimeMillis();
            JvmStateLog     stateLog = JvmStateLog.makeJvmStateLog(this.jvmClient_);
            
            //  set JVM Process state
            stateLog.setProcState(procState);
            
            // disconnect
            if (this.jvmClient_.isConnect() == false)
            {
                // set end flag
                stateLog.setProcState(ProcessState.END_PROCESS);
                isProcess = false;
            }

            // add JvmStateLog
            this.watchState_.addStateLog(stateLog);
            
            // parse JSON & output stream
            logBuffCnt++;
            if ((logBuffCnt >= logBuffNum_) || (isProcess == false))
            {
                // call parse
                // create clone
                JvmWatchState   sendState = this.watchState_.clone();
                try
                {
                    // send to parser
                    this.queue_.put(sendState);
                }
                catch (InterruptedException e)
                {
                    log.error(e);
                    isProcess = false;
                }
                // clear StateLog
                this.watchState_.clearStateLog();
                logBuffCnt = 0;
            }
            
            // next Process State
            procState = ProcessState.LIVE_PROCESS;

            // calc wait time
            long    procTime = System.currentTimeMillis() - startTime;
            long    waitTime = watchInterval_ - procTime;

            // wait to next process.
            if (waitTime > 0)
            {
                try
                {
                    Thread.sleep(waitTime);
                }
                catch (InterruptedException ex)
                {
                    log.error(ex);
                }
            }
        }
        // reject this JvmWatchThread.
        this.parent_.removeJvmWatchThread(pid_);
    }

}
