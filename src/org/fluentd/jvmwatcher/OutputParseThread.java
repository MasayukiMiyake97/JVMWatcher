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

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import org.fluentd.jvmwatcher.data.JvmWatchState;
import org.fluentd.jvmwatcher.parser.AbstractStateParser;

/**
 * @author miyake
 *
 */
public class OutputParseThread implements Runnable
{
    private     BlockingQueue<JvmWatchState>    queue_ = null;
    private     JvmWatcher              parent_ = null;
    private     AbstractStateParser     parser_ = null;

    /**
     * @param parent
     * @param queue
     * @param parser
     */
    public OutputParseThread(JvmWatcher parent, BlockingQueue<JvmWatchState> queue, AbstractStateParser parser)
    {
        this.parent_ = parent;
        this.queue_ = queue;
        this.parser_ = parser;
        // set host name
        this.parser_.setHostName(this.parser_.getHostName());
    }
    
    /* (非 Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        PrintWriter     writer = new PrintWriter(System.out);
        while (true)
        {
            try
            {
                JvmWatchState   state = this.queue_.take();
                // parse Log
                this.parser_.parseState(writer, state);
            }
            catch (InterruptedException ex)
            {
                break ;
            }
        }
    }

}
