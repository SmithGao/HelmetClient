package com.zskj.helmetclient.util;

import android.os.Message;

import com.zskj.helmetclient.bean.Msg;
import com.zskj.helmetclient.bean.User;
import com.zskj.helmetclient.ui.activity.ConnectActivity;
import com.zskj.helmetclient.ui.activity.MainActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;

/**
 * 作者：yangwenquan on 2016/11/22
 * 类描述：命令类
 */
public class Tools {
	//协议命令
	public static final int CMD_ONLINE = 10;// 上线
	public static final int CMD_REPLYONLINE = 11;// 回应上线
	public static final int CMD_CHECK = 12;// 心跳广播
	public static final int CMD_FILEREQUEST = 20;// 请求传送文件
	public static final int CMD_FILEACCEPT = 21;// 接受文件请求
	public static final int PORT_SEND = 2426;// 发送端口
	public static final int PORT_RECEIVE = 2425;// 接收端口

	//消息命令
	public static final int MAINACTIVITY = 7998;//当前是MAINACTIVITY
	public static final int CONNECTACTIVITY = 7999;//连接服务的CONNECTACTIVITY
	public static final int ACTIVITY_MAIN = 0;//客户端 构造函数专用
	public static final int ACTIVITY_CHART = 1;//服务端 构造函数专用
	public static final int SHOW = 8000;//显示消息
	public static final int FLUSH = 8001;//刷新界面
	public static final int ADDUSER = 8002;//添加用户
	public static final int DESTROYUSER = 8003;//删除用户
	public static MainActivity mainA = null;
	public static ConnectActivity ConnectB = null;
	public static int State = Tools.MAINACTIVITY;//状态，显示当前活跃activity
	public static String sign = ":";

	public static final int INTENT = 8004;//跳转到showiamgeactivity


	// 文件传送模块
	public static String newfileName = null;
	public static long newfileSize = 0;
	public static int byteSize = 1024 * 5;// 每次读写文件的字节数
	public static double sendProgress = -1;// 每次读写文件的字节数s
	public static String newsavepath = "/mnt/sdcard/A_AAAAFile";
	public static final int FILE_JINDU = 2001;// 进度命令
	public static final int PROGRESS_FLUSH = 2002;// 更新进度
	public static final int PROGRESS_COL = 2003;// 关闭进度条

	// 构造函数
	public Tools(Object o, int type) {
		switch (type) {
			case Tools.ACTIVITY_MAIN:
				this.mainA = (MainActivity) o;
				break;
			case Tools.ACTIVITY_CHART:
				this.ConnectB = (ConnectActivity) o;
				break;
		}

	}

	// 接收响应上线
	public void replyUpline(Msg msg) {
		LogUtil.printI("YWQ", "接受响应上线" + msg.getSendUserIp());
		if (!judgeUser(msg)) {// 如果不存在
			TipsMain(Tools.SHOW, msg.getSendUserName() + " 上线···");
			addUser(msg);// 添加此人
		}
	}

	// 获取当前时间
	public static long getTimel() {
		return (new Date()).getTime();
	}

	// 得到广播ip, 192.168.0.255之类的格式
	public static String getBroadCastIP() {
		String ip = getLocalHostIp().substring(0,
				getLocalHostIp().lastIndexOf(".") + 1)
				+ "255";
		return ip;
	}

	// 获取本机IP
	public static String getLocalHostIp() {
		try {
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();
			// 遍历所用的网络接口
			while (en.hasMoreElements()) {
				NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
				Enumeration<InetAddress> inet = nif.getInetAddresses();
				// 遍历每一个接口绑定的所有ip
				while (inet.hasMoreElements()) {
					InetAddress ip = inet.nextElement();
					if (!ip.isLoopbackAddress()
							&& ip instanceof Inet4Address) {
						return ip.getHostAddress();
					}
				}

			}
		} catch (SocketException e) {
			System.out.print("获取IP失败");
			e.printStackTrace();
		}
		return null;

	}

	//发送消息
	public void sendMsg(Msg msg) {
		(new UdpSend(msg)).start();
	}

	//发送消息线程
	class UdpSend extends Thread {
		Msg msg = null;

		UdpSend(Msg msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			try {
				LogUtil.defLog("Running--" + msg);
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

	// 对象转换为字节
	public static byte[] toByteArray(Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
			oos.close();
			bos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return bytes;
	}

	// 字节解析成对象
	public static Object toObject(byte[] bytes) {
		Object obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = ois.readObject();
			ois.close();
			bis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return obj;
	}

	// 解析接收的消息对象
	public void parse(Msg msg) {
		switch (msg.getMsgType()) {
			case Tools.CMD_ONLINE://上线
				upline(msg);
				break;
			case Tools.CMD_CHECK:// 心跳接收
				updateHeart(msg);
				break;
			case Tools.CMD_REPLYONLINE:// 响应上线
				replyUpline(msg);
				break;
			case Tools.CMD_FILEREQUEST:
				// 传送文件
				String[] newfileInfo = ((String) msg.getBody()).split(Tools.sign);
				Tools.newfileName = newfileInfo[0];// 记录下文件名称
				LogUtil.printI("xsxsxsxsxsxs", "取数据---" + Tools.newfileName + "---Tools.newfileName ");
				Tools.newfileSize = Long.parseLong(newfileInfo[1].trim());// 文件大小
				LogUtil.defLog(newfileName + "--" + newfileSize);
				Tools.TipsConnect(Tools.CMD_FILEREQUEST, msg);
				break;

			case Tools.CMD_FILEACCEPT:
				//对方文件确定接收
				// 收到确认接受
				String path = Tools.ConnectB.choosePath;//文件的url
				Tools.TipsConnect(Tools.SHOW, "正在发送G文件:" + new File(path).getName());
				Tools.sendProgress = 0;
				FileTcpClient tc0 = new FileTcpClient(msg, path);
				tc0.start();
				Tools.TipsConnect(Tools.FILE_JINDU, "发送G文件" + Tools.sign + "正G在发送：" + new File(path).getName()
						+ Tools.sign + new File(path).length());
				fileProgress();// 启动进度条线程
				break;

			default:
				break;
		}
	}

	// 接收到上线广播
	public void upline(Msg msg) {
		LogUtil.printI("YWQ", "收到广播");
		if (!judgeUser(msg)) {// 如果不存在
			LogUtil.printI("YWQ", "添加人");
			addUser(msg);// 添加此人
		}
		LogUtil.printI("YWQ", "没添加人");
		// 发送响应上线
		Msg msgsend = new Msg();
		msgsend.setSendUserName(mainA.user.getName());
		msgsend.setSendUserIp(mainA.user.getIp());
		msgsend.setMsgType(Tools.CMD_REPLYONLINE);
		msgsend.setReceiveUserIp(msg.getSendUserIp());
		msgsend.setDate(Tools.getTimel());
		LogUtil.printI("YWQ", mainA.user.getIp() + "回复广播" + msg.getSendUserIp());
		// 发送消息
		sendMsg(msgsend);
	}

	// 判断是否有此人 更新
	public boolean judgeUser(Msg msg) {// false 表示不存在
		for (int i = 0; i < mainA.userList.size(); i++) {
			if (mainA.userList.get(i).getIp().equals(msg.getSendUserIp())) {
				// 如果存在 改名字
				if (!mainA.userList.get(i).getName().equals(msg.getSendUserName())) {
					mainA.adapterList.get(i).setName(msg.getSendUserName());// 该在线列表的名字
					//刷新列表
					TipsMain(Tools.FLUSH, null);
				}
				return true;
			}
		}
		return false;
	}

	// 添加在线用户
	public void addUser(Msg msg) {
		User user = new User(msg.getSendUserName(), msg.getSendUserIp(), System.currentTimeMillis());
		// 在线列表加人
		mainA.userList.add(user);
		User user1 = new User(msg.getSendUserName(), "IIP" + msg.getSendUserIp(), System.currentTimeMillis());
		// 刷新列表
		TipsMain(Tools.ADDUSER, user1);
	}

	//发消息到主界面 MainActivity
	public static void TipsMain(int cmd, Object str) {
		Message m = new Message();
		m.what = cmd;
		m.obj = str;
		Tools.mainA.handler.sendMessage(m);
	}

	//发消息到连接界面 ConnectActivity
	public static void TipsConnect(int cmd, Object str) {
		Message m = new Message();
		m.what = cmd;
		m.obj = str;
		Tools.ConnectB.handler.sendMessage(m);
	}


	// 开启心跳检查
	public void startCheck() {
		new HeartBroadCast().start();
		new CheckUserOnline().start();
	}

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
	class CheckUserOnline extends Thread {
		@Override
		public void run() {
			while (!mainA.isPaused) {
				for (int i = 0; i < mainA.userList.size(); i++) {
					long cm = System.currentTimeMillis() - mainA.userList.get(i).getOnlineTime();

					if (cm > 15000) {
						//刷新列表
						TipsMain(Tools.DESTROYUSER, i);

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

	// 接收心跳广播
	public void updateHeart(Msg msg) {
		for (int i = 0; i < mainA.userList.size(); i++) {
			if (mainA.userList.get(i).getIp().equals(msg.getSendUserIp())) {
				mainA.userList.get(i).setOnlineTime(System.currentTimeMillis());
			}
		}
	}


	//启动线程进度条
	public void fileProgress() {
		new Thread() {
			public void run() {

				while (Tools.sendProgress != -1) {
					Message m = new Message();
					m.what = Tools.PROGRESS_FLUSH;
					Tools.ConnectB.handler.sendMessage(m);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// 关闭进度条
				Message m1 = new Message();
				m1.what = Tools.PROGRESS_COL;
				Tools.ConnectB.handler.sendMessage(m1);
			}
		}.start();
	}
}
