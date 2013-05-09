JVMWatcher
==========

JVMWatcher is Java VM Status watcher.

JVMWatcher is the Java application which collects the CPU usage rate and the memory usage of more than one JavaVM to the constant period.
It developed this JVMWatcher to realize the fluentd plug-in ( jvmwatcher ) which collects the operating status of JVM.
JVMWatcher simple substance can be used but it is developing it basically for the jvmwatcher plug-in.

JVMWatcherは、複数のJavaVMの、CPU使用率やメモリ使用量を一定周期に収集するJavaアプリケーションです。
このJVMWatcherは、JVMの稼働状態を収集するfluentdプラグイン（jvmwatcher）を実現するために開発しました。
JVMWatcher単体でも利用することは可能ですが、基本的にjvmwatcherプラグインのために開発しています。

Originally, it thought that it would be realized in executing jps command and jstat command from the fluentd plug-in.
However, when collecting the condition of more than one JVM at the constant period, to execute jps which is a Java application and jstat to the constant period ( the interval of several seconds ), it has become the high implementing of a load fairly in the non- efficiency.
It decided developing the Java application which continues to output the condition of more than one JVM to the standard output at the constant period as one process to solve this problem.

元々、fluentdプラグインから、jpsコマンドとjstatコマンドを実行することで実現しようと考えていました。
しかし、一定周期で複数のJVMの状態を収集する場合、Javaアプリケーションであるjpsとjstatを一定周期（数秒間隔）に実行するため、かなり非効率で負荷の高い実装となってしまいます。
この問題を解決するため、一つのプロセスとして、一定周期で複数のＪＶＭの状態を標準出力に出力し続けるJavaアプリケーションを開発することにしました。

開発する際、ＯｐｅｎＪＤＫに含まれている、JConsoleのソースコードを参考にしています。


----------------------------------------------
2013/5/6
Java側のJVMWatcherは、ほぼ完成。
コンフィグレーションとかの細かい説明は、後で追加する予定です。

May 6th, 2013
JVMWatcher on the side of the Java is complete approximately.
It plans to add a configuration and a minute explanation later.

fluentdのプラグインも、定義を行う部分以外は完成している。
gemライブラリにはなっていないが、Java部分のJVMWatcherの定義ファイルを手作業で設定することで、fluentdプラグインとして動作するようになった。
jvmwatcher_log.tgzに、fluent-plugin-jvmwatcherによって収集した、JVMの稼働ログを保存しているので、興味があれば取得して観てください。

The plug-in of fluentd, too, has been complete except the part which gives a definition.
It didn't give off to the gem library but in setting the definition file of JVMWatcher in the Java part by the labour operation, it got to work as the fluentd plug-in.
Because it saves the operation log of JVM which was collected with fluent-plugin-jvmwatcher to jvmwatcher_log.tgz, acquire and see if interested.

--output Ex:

2013-05-06T17:26:08+09:00       jvmwatcher.log  {"logtime":1367828768073,"host_name":"nanoha","proc_state":"LIVE_PROCESS","pid":6750,"name":"jvmwatcher.test.TestJavaProcess 2048 1024 10 10","display_name":"jvmwatcher.test.TestJavaProcess 2048 1024 10 10","start_time":1367828702858,"up_time":65234,"cpu_usage":6.4870257,"compile_time":633,"c_load_cnt":1206,"c_unload_cnt":0,"c_total_load_cnt":1206,"th_cnt":20,"daemon_th_cnt":9,"peak_th_cnt":20,"heap_init":62766272,"heap_used":59079088,"heap_commit":91226112,"heap_max":892928000,"notheap_init":24313856,"notheap_used":9797496,"notheap_commit":24313856,"notheap_max":224395264,"pending_fin_cnt":0,"total_phy_mem_size":4017041408,"total_swap_mem_size":4160741376,"free_phy_mem_size":2854748160,"free_swap_mem_size":4160741376,"commit_vmem_size":2428276736,"gc_collect":[{"gc_mgr_name":"PS MarkSweep","gc_coll_cnt":2,"gc_coll_time":39},{"gc_mgr_name":"PS Scavenge","gc_coll_cnt":11,"gc_coll_time":55}]}
2013-05-06T17:26:08+09:00       jvmwatcher.log  {"logtime":1367828768607,"host_name":"nanoha","proc_state":"LIVE_PROCESS","pid":6694,"name":"jvmwatcher.test.TestJavaProcess 4096 512 5 10","display_name":"jvmwatcher.test.TestJavaProcess 4096 512 5 10","start_time":1367828676066,"up_time":92560,"cpu_usage":12.413108,"compile_time":942,"c_load_cnt":1205,"c_unload_cnt":0,"c_total_load_cnt":1205,"th_cnt":19,"daemon_th_cnt":8,"peak_th_cnt":19,"heap_init":62766272,"heap_used":32525552,"heap_commit":72286208,"heap_max":892928000,"notheap_init":24313856,"notheap_used":9971384,"notheap_commit":24313856,"notheap_max":224395264,"pending_fin_cnt":0,"total_phy_mem_size":4017041408,"total_swap_mem_size":4160741376,"free_phy_mem_size":2854748160,"free_swap_mem_size":4160741376,"commit_vmem_size":2427224064,"gc_collect":[{"gc_mgr_name":"PS MarkSweep","gc_coll_cnt":22,"gc_coll_time":497},{"gc_mgr_name":"PS Scavenge","gc_coll_cnt":410,"gc_coll_time":591}]}


----------------------------------------------
1つのログに含まれる要素

The element which is contained in one piece of log


{

"logtime":1367828768607,

"host_name":"nanoha",

"proc_state":"LIVE_PROCESS",

"pid":6694,

"name":"jvmwatcher.test.TestJavaProcess 4096 512 5 10",

"display_name":"jvmwatcher.test.TestJavaProcess 4096 512 5 10",

"start_time":1367828676066,

"up_time":92560,

"cpu_usage":12.413108,

"compile_time":942,

"c_load_cnt":1205,

"c_unload_cnt":0,

"c_total_load_cnt":1205,

"th_cnt":19,

"daemon_th_cnt":8,

"peak_th_cnt":19,

"heap_init":62766272,

"heap_used":32525552,

"heap_commit":72286208,

"heap_max":892928000,

"notheap_init":24313856,

"notheap_used":9971384,

"notheap_commit":24313856,

"notheap_max":224395264,

"pending_fin_cnt":0,

"total_phy_mem_size":4017041408,

"total_swap_mem_size":4160741376,

"free_phy_mem_size":2854748160,

"free_swap_mem_size":4160741376,

"commit_vmem_size":2427224064,

"gc_collect":[
{"gc_mgr_name":"PS MarkSweep","gc_coll_cnt":22,"gc_coll_time":497},
{"gc_mgr_name":"PS Scavenge","gc_coll_cnt":410,"gc_coll_time":591}
]
              
}

