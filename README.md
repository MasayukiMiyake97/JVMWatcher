JVMWatcher
==========

Java VM Status watcher.
JVMWatcher is a Java application that collects a certain period in the JavaVM, a plurality of memory usage and CPU utilization. 
This JVMWatcher, has developed in order to realize (jvmwatcher) fluentd plug-in to collect the health of the JVM. 
It is possible to use in JVMWatcher alone, but it has developed for jvmwatcher plug-in basically.

JVMWatcherは、複数のJavaVMの、CPU使用率やメモリ使用量を一定周期に収集するJavaアプリケーションです。
このJVMWatcherは、JVMの稼働状態を収集するfluentdプラグイン（jvmwatcher）を実現するために開発しました。
JVMWatcher単体でも利用することは可能ですが、基本的にjvmwatcherプラグインのために開発しています。

元々、jps+jstatコマンドをfluentdプラグインからforkして実現しようと考えていたのですが、複数のJVMの状態を一定周期で収集
しようとすると、Javaアプリケーションであるjpsをjstatを頻繁にｆｏｒｋするという、かなり非効率で負荷の高い実装となってしまうため、
1プロセスで、一度起動したら、一定周期で複数のＪＶＭの状態を標準出力に出力し続けるＪａｖａアプリケーションを開発することに
しました....

開発する際、ＯｐｅｎＪＤＫに含まれている、JConsoleのソースコードを参考にしています。
