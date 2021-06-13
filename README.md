ColorDbRes

紧贴应用开发，实现异步(reactor)和同步(经典jdbc)两种模式数据库连接池。

简介
1、子项目r2dbc
2021年3月17日Oracle公司发布了Oracle数据库的Oracle R2DBC Driver, 实现了用于 Oracle 数据库的 R2DBC SPI 版本 0.9.0.M1。
依赖2021年1月16日Oracle发布的Ojdbc11 21.1.0.0的Reactive Extensions APIs和jdk11、Reactive Streams 1.0.3、Project Reactor 3.0.0。
本项目是第一个基于spring boot的oracle r2dbc连接池实现，同时也提供非spring boot的线程安全的共享连接对象实现。
子项目还提供了mysql的基于spring boot的oracle r2dbc连接池实现。
2、子项目sync2dbc
子模块druid实现了基于spring boot、durid、jpa、hibernate的多数据源连接池和基于getbean方法直接获取连接资源的实现。
子模块hikaricp实现了基于hikaricp和jdbc直接实现多数据源连接池的实现。