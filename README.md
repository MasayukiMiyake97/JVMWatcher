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
2013年のGWには完成させたい....
