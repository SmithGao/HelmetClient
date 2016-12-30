## 你不知道的UDP传输(局域网)

  近日闲来无事 特将工作中研究过的小功能提出来，分享给大家。是一篇有关于UDP传输文件、视频的demo。当然 网上肯定已经存在很多了，我也只是将我自己研究的心得来在这里给大家讲一下，说的不好 大家多多担待 !
---
 首先 先为大家简单的讲一下TCP与UDP的区别(真的是简单的讲一下 做个铺垫嘛)
 
 相同：都是传输层 
 
 不同：使用TCP协议传输数据，当数据从A端传到B端后，B端会发送一个确认包（ACK包）给A端，告知A端数据我已收到！有重传机制，UDP协议就没有这种确认机制！
 
 UDP 协议是无线连接的数据传输并且无重传机制，很大的可能会造成丢包、收到重复包、乱序的情况，并且无法做这种情况作出处理 只能选择再次发送(如非特殊要求，估计大家不会使用的-但是 我遇到了 哎...)
 

---
好了 上面简单的介绍了TCP与UDP的区别，那么下面 咱们该切入正题了

首先 基本配置

 ```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.MOUNT_FORMAT_FILESYSTEMS" />
    <uses-permission android:name="android.permission.CAMERA" />
    
    ``` 


链接网络(局域网) 、SD卡写入写出、照相机等。

此demo只有一个APP即可 即是客户端也是服务端

首先在MainActivity中启动线程发送自己的信息让另一个手机的demo接收。 整体框架全部基于Thread,Handler,Socket发送与接收消息

``` 
	//广播上线(自己)
	private void reBroad() {
		Msg msg = new Msg();
		msg.setSendUserName(user.getName());
		msg.setSendUserIp(user.getIp());
		msg.setReceiveUserIp(Tools.getBroadCastIP());
		msg.setMsgType(Tools.CMD_ONLINE);//通知上线命令
		msg.setDate(Tools.getTimel());
		// 发送广播通知上线
		tools.sendMsg(msg);
	}
``` 
``` 
  // 心跳响应广播
    class HeartBroadCast extends Thread {
        public void run() {
            while (!mainA.isPaused) {
                try {
                    sleep(10000);
                } catch (InterruptedException e) {
                }
                Msg msgBroad = new Msg();
                msgBroad.setSendUserName(mainA.user.getName());
                msgBroad.setSendUserIp(mainA.user.getIp());
                msgBroad.setMsgType(Tools.CMD_CHECK);
                msgBroad.setReceiveUserIp(Tools.getBroadCastIP());
                msgBroad.setDate(Tools.getTimel());
                // 发送消息
                sendMsg(msgBroad);
            }
        }
    }

    // 检测用户是否在线，如果超过15说明用户已离线，秒则从列表中清除该用户
    class CheckUserOnline extends Thread{
        @Override
        public void run()
        {
            while(!mainA.isPaused)
            {
                for (int i = 0; i < mainA.userList.size(); i++)
                {
                    long cm=System.currentTimeMillis()-mainA.userList.get(i).getOnlineTime();
                    if(cm>15000)
                    {
                        //刷新列表
                        TipsMain(Tools.DESTROYUSER,i);
                    }
                }
                try {
                    sleep(8000);
                    //防掉线，广播
                    //Tips(Tools.CONSTANTBROAD,null);
                } catch (InterruptedException e) {
                }
            }
        }
    }
    ``` 
    

---

  依照上面大家可以清楚的看到 在起始的MainActivity中首先就去发送自己上线的通知，另一端同样的启动线程随时的接收---保持自己能够随时和B端连接上。大家可以看到几乎所有的发送全部由Thread中进行操作(原因很简单 我就不说明了)。
  
  在之后的操作 就会全部通过message发送到主线程去进行UI(数据)的更新，给大家看下在发送和接收消息的时候应该如何对的数据进行处理吧。
 ``` 
 //发送消息线程
    class UdpSend extends Thread {
        Msg msg = null;

        UdpSend(Msg msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                LogUtil.defLog("Running--"+msg);
                byte[] data = Tools.toByteArray(msg);
                //1、创建DatagramSocket用于UDP数据传送
                DatagramSocket ds = new DatagramSocket(Tools.PORT_SEND);
                //2、创建需要发送的数据包
                DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(msg.getReceiveUserIp()), Tools.PORT_RECEIVE);
                //3、发送
                packet.setData(data);
                ds.send(packet);
                //4、关闭连接
                ds.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //接收消息
    public void receiveMsg() {
        new UdpReceive().start();
    }

    class UdpReceive extends Thread {
        Msg msg = null;

        UdpReceive() {
        }

        public void run() {
            //消息循环
            while (true) {
                try {
                    //1、创建DatagramSocket;
                    DatagramSocket ds = new DatagramSocket(Tools.PORT_RECEIVE);
                    //2、创建数据包，用于接收内容。
                    byte[] data = new byte[1024 * 4];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    //3、接收数据
                    packet.setData(data);
                    ds.receive(packet);
                    byte[] data2 = new byte[packet.getLength()];
                    System.arraycopy(data, 0, data2, 0, data2.length);// 得到接收的数据
                    Msg msg = (Msg) Tools.toObject(data2);
                    ds.close();
                    //解析消息
                    parse(msg);
                } catch (Exception e) {
                }
            }
        }
    }
    ``` 
  
  同样需要启动线程，前面介绍了 防止数据在传输过程中出现问题，当然 假如真的出现了问题 我可以告诉你一个更简单的方法 ---重新发送！![](emoji/smile)
  我现在依旧觉得我每一次的成功都是我实验前的祈祷造成的，哈哈 废话不多说了 继续讲解。
  
  Socket是链接中一定要存在的桥梁传送数据全靠他了啊。
  
  然后接到的数据就要开始解析啦
  ``` 
  发送数据将数据压缩成流文件，Socket创建(要接受的id，链接号)
	public void creatClient() throws Exception {
		Socket s = new Socket(msg.getSendUserIp(), 2222);
		// 读文件w
		File file = new File(path);
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
		BufferedOutputStream os =new BufferedOutputStream( s.getOutputStream());
		// 读文件
		double n = 1;
		byte[] data = new byte[Tools.byteSize];// 每次读取的字节数
		int len=-1;
		while ((len=is.read(data))!= -1) {
			os.write(data,0,len); 
			Tools.sendProgress+=len;//进度
		}
		Tools.sendProgress=-1;
		is.close();
		os.flush();
		os.close();
	}
	``` 
   
   ``` 
   ServerSocket接收(连接号) 开始对文件进行解压
   
	ServerSocket ss = new ServerSocket(2222);
		Socket s = new Socket();
		s = ss.accept();
		File filesDir = connectB.getExternalFilesDir(null);
		if (!filesDir.exists()) {
			filesDir.mkdirs();
		}
		String name =Tools.newfileName;
		File file = new File(filesDir + name);
		LogUtil.defLog("存数据--" + file.getAbsolutePath());
		if (!file.exists()) {
			file.createNewFile();
		}
		BufferedInputStream is = new BufferedInputStream(s.getInputStream()); // 读进
		BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));// 写出

		Thread.sleep(1000);
		byte[] data = new byte[Tools.byteSize];// 每次读取的字节数
		int len = -1;
		while ((len = is.read(data)) != -1) {
			os.write(data, 0, len);
			Tools.sendProgress += len;// 进度
		}
		Tools.sendProgress = -1;
		is.close();
		os.flush();
		os.close();
		Tools.TipsMain(Tools.INTENT, connectB.getFilesDir().getAbsolutePath());
		s.close();
		Tools.TipsMain(Tools.SHOW, "接收完成" + Tools.newfileName);
		``` 

---
 大家有木有注意到我将文件存放的位置，没错 我存在了内存中的包里面
 简单的原因 存放在这里 我才能将里面的数据显示出来(其他位置可能也行 不过我没有试过)
 
** 总结：其实UDP传输很简单的 只要将里面的逻辑关系搞清楚 整套的东西很好出来 我会在明年的时候 将点对点的视频通话研究出来 到时这篇文章我在进行更新现在的代码我放进我的[guyhub](https://github.com/SmithGao/HelmetClient)上了 有需要的童鞋可以下载看看 记得拿两个手机哈 一个是没法测试的 .希望你们可以有所学习，另外你会发现更多地惊喜哦 欢迎start**
