# dirmap_plus
dirmap增加web服务功能


一、	解决什么需求
为什么还要做升级版的dirmap，是dirmap不够强悍么？（dirmap号称功能将会强于DirBuster、Dirsearch、cansina、御剑）。

常规的目录爆破，输入点通常是单个url或者多个url批量进行。但这都是固定的目标。当前这个方式对于简单明确url的资产可以进行。

在日常渗透的时候，时常会出现如下情况：
1、	一些多层级的url。例如：有个入口地址需要爆破，但是入口地址是多层级的地址（比如：http://xx.xx.xx/a/b/c/d/e/f.html），类似这种多层级地址，又需要每一级都进行爆破，那么如何节省人力的去爆破（每次手动拆分也不是个事吧），
2、	在进行API接口测试时出现的url地址也想进行爆破，还需要手动复制该地址出来再拿到扫描工具中进行工作。这也是一个费时费力费思绪的苦力劳动。

这种动态扫描的方式。在少量目标地址的情况下，勉强还能节省。如果有大量动态地址需要进行，那就是一个很头痛的事情。

那么如何在出现多层级和动态发现新地址的情况下，有效的进行目录爆破，同时又能节省人力。那就搞个升级版dirmap的玩玩。

	我们的目标：节省人力，把精力投入到脑力活动中去。

二、	对原版dirmap进行升级
本着不重复造轮子宗旨，我们就加改吧。
当前版本的dirmap已经是一个功能非常丰富特别强的目录爆破工具了。基本使用可以去大佬的GitHub端详https://github.com/H4ckForJob/dirmap
2.1、dirmap新增部分
	当前dirmap添加目标的方式：
1、在命令行中使用-i参数添加单个目标地址；
2、在命令行中使用-iD参数以读取文件的方式添加多个目标。
以上两种方式都是基于固定目标url地址运行的。
运行方式为：添加目标地址 -》 运行dirmap -〉等待运行结束 -》 查看结果
下一波地址重新在命令行手动再运行一个轮回。

加整：
新增一个运行方式，循环运行。
新增一种从sqlite中加载目标的方式。

基本思路：
	循环运行从sqlite中读取新目标，发现有新目标就加载并进行爆破
	
	给dirmap增加了一个命令行参数-iD 该参数为sqlite名称。

2.2、附加脚本
	2.2.1、services.py
		开启web服务，提供API接口供外部远程添加待扫描地址，并将其写入到sqlite中，待dirmap加载运行。
		同时提供查询API接口供远程查询扫描结果。
	2.2.2、test.py
		该脚本主要为辅助脚本，提供查询当前sqlite内部数据，以及可创建新db功能。（可用可不用）

2.3、这样运行
	首先常规的使用方式还是可以正常使用的。具体见dirmap作者的GitHub端详https://github.com/H4ckForJob/dirmap
	第一步：使用 -iD 参数运行dirmap
		python3 dirmap.py -iD ddv.db -lcf
![image](https://user-images.githubusercontent.com/97394404/148688449-998390e1-8e53-4354-9a2a-9969f7f088b1.png)