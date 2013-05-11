JVMWatcher
==========

JVMWatcher is the application which outputs the log of the JSON format at the constant period, watching in the condition of JavaVM.

JVMWatcher is the Java application which collects the CPU usage rate and the memory usage of more than one JavaVM to the constant period.
It developed this JVMWatcher to realize the fluentd plug-in ( jvmwatcher ) which collects the operating status of JVM.
JVMWatcher simple substance can be used but it is developing it basically for the jvmwatcher plug-in.

Originally, it thought that it would be realized in executing jps command and jstat command from the fluentd plug-in.
However, when collecting the condition of more than one JVM at the constant period, to execute jps which is a Java application and jstat to the constant period ( the interval of several seconds ), it has become the high implementing of a load fairly in the non- efficiency.
It decided developing the Java application which continues to output the condition of more than one JVM to the standard output at the constant period as one process to solve this problem.

Because it is preserving the operation logfile of JavaVM to have incorporated into the fluentd plug-in and to have made output it in jvmwatcher_log.tgz, attempt to acquire if interested.

-----------------------------------------------
JVMWatcherは、JavaVMの状態の監視を行い、一定周期でJSON形式のログを出力するアプリケーションです。

JVMWatcherは、複数のJavaVMの、CPU使用率やメモリ使用量を一定周期に収集するJavaアプリケーションです。
このJVMWatcherは、JVMの稼働状態を収集するfluentdプラグイン（jvmwatcher）を実現するために開発しました。
JVMWatcher単体でも利用することは可能ですが、基本的にjvmwatcherプラグインのために開発しています。

元々、fluentdプラグインから、jpsコマンドとjstatコマンドを実行することで実現しようと考えていました。
しかし、一定周期で複数のJVMの状態を収集する場合、Javaアプリケーションであるjpsとjstatを一定周期（数秒間隔）に実行するため、かなり非効率で負荷の高い実装となってしまいます。
この問題を解決するため、一つのプロセスとして、一定周期で複数のＪＶＭの状態を標準出力に出力し続けるJavaアプリケーションを開発することにしました。

fluentdプラグインに組み込んで出力させたJavaVMの稼働ログファイルを、jvmwatcher_log.tgzに保存しているので、興味があれば取得してみてください。

##output Ex:

    2013-05-06T17:26:08+09:00       jvmwatcher.log  {"logtime":1367828768073,"host_name":"nanoha","proc_state":"LIVE_PROCESS","pid":6750,"name":"jvmwatcher.test.TestJavaProcess 2048 1024 10 10","display_name":"jvmwatcher.test.TestJavaProcess 2048 1024 10 10","start_time":1367828702858,"up_time":65234,"cpu_usage":6.4870257,"compile_time":633,"c_load_cnt":1206,"c_unload_cnt":0,"c_total_load_cnt":1206,"th_cnt":20,"daemon_th_cnt":9,"peak_th_cnt":20,"heap_init":62766272,"heap_used":59079088,"heap_commit":91226112,"heap_max":892928000,"notheap_init":24313856,"notheap_used":9797496,"notheap_commit":24313856,"notheap_max":224395264,"pending_fin_cnt":0,"total_phy_mem_size":4017041408,"total_swap_mem_size":4160741376,"free_phy_mem_size":2854748160,"free_swap_mem_size":4160741376,"commit_vmem_size":2428276736,"gc_collect":[{"gc_mgr_name":"PS MarkSweep","gc_coll_cnt":2,"gc_coll_time":39},{"gc_mgr_name":"PS Scavenge","gc_coll_cnt":11,"gc_coll_time":55}]}
    2013-05-06T17:26:08+09:00       jvmwatcher.log  {"logtime":1367828768607,"host_name":"nanoha","proc_state":"LIVE_PROCESS","pid":6694,"name":"jvmwatcher.test.TestJavaProcess 4096 512 5 10","display_name":"jvmwatcher.test.TestJavaProcess 4096 512 5 10","start_time":1367828676066,"up_time":92560,"cpu_usage":12.413108,"compile_time":942,"c_load_cnt":1205,"c_unload_cnt":0,"c_total_load_cnt":1205,"th_cnt":19,"daemon_th_cnt":8,"peak_th_cnt":19,"heap_init":62766272,"heap_used":32525552,"heap_commit":72286208,"heap_max":892928000,"notheap_init":24313856,"notheap_used":9971384,"notheap_commit":24313856,"notheap_max":224395264,"pending_fin_cnt":0,"total_phy_mem_size":4017041408,"total_swap_mem_size":4160741376,"free_phy_mem_size":2854748160,"free_swap_mem_size":4160741376,"commit_vmem_size":2427224064,"gc_collect":[{"gc_mgr_name":"PS MarkSweep","gc_coll_cnt":22,"gc_coll_time":497},{"gc_mgr_name":"PS Scavenge","gc_coll_cnt":410,"gc_coll_time":591}]}

##Requirements
  JDK >= 1.6
  
  * On the Linux ( CentOS ), it is OpenJDK In 1.6 pieces of environment, it is doing an operations check.The operations check doesn't go but probably, it works on Ubuntu.

##Copyright
Copyright (c) 2013 MasayukiMiyake

##License
Apache License, Version 2.0
