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

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fluentd.jvmwatcher.data.JvmWatchState;
import org.fluentd.jvmwatcher.parser.AbstractStateParser;
import org.fluentd.jvmwatcher.parser.JsonSimpleLogParser;
import org.fluentd.jvmwatcher.proxy.JvmClientProxy;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * JVM Watcher Main Class
 * @author miyake
 *
 */
public class JvmWatcher
{
    private static  Log log = LogFactory.getLog(JvmWatcher.class);
    
    private     Map<Integer, JvmWatchThread>    jvmProcessorMap_ = new HashMap<Integer, JvmWatchThread>();
    private     OutputParseThread               jsonOutputParseTread_ = null;
    private     BlockingQueue<JvmWatchState>    queue_ = new LinkedBlockingQueue<JvmWatchState>();
    
    private     long        refindJvmInterval_ = 20000L;
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        JvmWatcher      jvmWatcher = new JvmWatcher();
        
        if (args.length < 1)
        {
            log.warn("not property. watch or targetlist ");
            // start JVM Watcher mode
            jvmWatcher.startJvmWatcher(null);
        }

        String  type = null;
        String  configPath = null;
        
        if (args.length == 1)
        {
            type = args[0];
        }
        else if (args.length >= 2)
        {
            type = args[0];
            configPath = args[1];
        }

        if ("watcher".compareTo(type) == 0)
        {
            jvmWatcher.startJvmWatcher(configPath);
        }
        else if ("targetlist".compareTo(type) == 0)
        {
            jvmWatcher.startTargetList(configPath);
        }
        else
        {
            log.warn("unknown type. type=" + type);
            // start JVM Watcher mode
            jvmWatcher.startJvmWatcher(configPath);
        }
    }

    /**
     * @param paramFilePath
     */
    public void startJvmWatcher(String paramFilePath)
    {
        
        // start OutputParseThread
        AbstractStateParser     parser = new JsonSimpleLogParser();

        parser.setHostName(getHostName(null));
        jsonOutputParseTread_ = new OutputParseThread(this, this.queue_, parser);
        // start thread
        Thread      thread = new Thread(this.jsonOutputParseTread_);
        thread.start();
        
        // loop
        this.runProcess();
    }

    /**
     * @param paramFilePath
     */
    public void startTargetList(String paramFilePath)
    {
        // load configuration
        this.loadProperty(paramFilePath);

        // get JVM process list
        Map<Integer, LocalJvmInfo>  allJvm = LocalJvmInfo.getAllLocalJvmInfos();
        
        System.out.println("-- process list start --");
        for(Map.Entry<Integer, LocalJvmInfo> elem : allJvm.entrySet())
        {
            int             key = elem.getKey();
            LocalJvmInfo    val = elem.getValue();
            
            System.out.println("Target prosecc  [pid]=" + key +  "  [ShortName]=" + val.getShortName() +  "  [CommandLine]=" + val.getCommandLine_());
        }
        System.out.println("-- process list end   --");

    }

    
    /**
     * @param paramFilePath
     */
    public void loadProperty(String paramFilePath)
    {
        if (null == paramFilePath)
        {
            return ;
        }
        
        try
        {
            // load JSON property file.
            File        file = new File(paramFilePath);
            
            JsonFactory factory = new JsonFactory();
            JsonParser  parser = factory.createParser(file);

            JsonToken   token = null;
            while ((token = parser.nextToken()) != null)
            {
                if (token == JsonToken.FIELD_NAME)
                {
                    if (parser.getText().compareTo("target") == 0)
                    {
                        this.loadTarget(parser);
                    }
                }
            }
            
            parser.close();
        }
        catch (JsonParseException e)
        {
            log.error("Property parse error.", e);
        }
        catch (IOException e)
        {
            log.error("Property file open error.", e);
        }
        catch (Exception e)
        {
            log.error("Property file open error.", e);
        }
    }

    /**
     * @param parser
     * @throws JsonParseException
     * @throws IOException
     */
    private void loadTarget(JsonParser parser) throws JsonParseException, IOException
    {
        if (parser.nextToken() == JsonToken.START_ARRAY)
        {
            while (parser.nextToken() != JsonToken.END_ARRAY)
            {
                if (parser.getCurrentToken() == JsonToken.START_OBJECT)
                {
                    String      shortName = null;
                    String      pattern = null;
                    while (parser.nextToken() != JsonToken.END_OBJECT)
                    {
                        if ((parser.getCurrentToken() == JsonToken.FIELD_NAME) && (parser.getText().compareTo("shortname") == 0))
                        {
                            if (parser.nextToken() == JsonToken.VALUE_STRING)
                            {
                                shortName = parser.getText();
                            }
                        }
                        if ((parser.getCurrentToken() == JsonToken.FIELD_NAME) && (parser.getText().compareTo("pattern") == 0))
                        {
                            if (parser.nextToken() == JsonToken.VALUE_STRING)
                            {
                                pattern = parser.getText();
                            }
                        }
                    }
                    
                    // add target pattern
                    Pattern     regexPattern = Pattern.compile(pattern);
                    LocalJvmInfo.addTargetProcessPattern(shortName, regexPattern);
                }
            }
        }
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
                log.error(ex);
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
            log.error(ex);
        }
        return ret;
    }
    
}
