package com.zskj.helmetclient.util;

import android.app.Activity;

import com.zskj.helmetclient.ui.activity.ConnectActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTcpServer {
	ConnectActivity connectB;

	public FileTcpServer(Activity connectB) {
		this.connectB = (ConnectActivity) connectB;
	}

	public void start() {
		server s = new server();
		s.start();
	}

	class server extends Thread {

		public void run() {
			try {
				creatServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 接受数据 解析数据
	 */
	public void creatServer() throws Exception {
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

		/////----------------------------------------------------------
//		File filesDir = ConnectB.getFilesDir();
//		File file = new File(filesDir ,filename);
//		if (!file.exists()) {
//			file.mkdir();
//			file.createNewFile();
//		}
//		try {
//			BufferedInputStream is = new BufferedInputStream(s.getInputStream()); // 读进
//			BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));// 写出
//			is.close();
//			os.flush();
//			os.close();
//			Tools.Tips(Tools.INTENT, ConnectB.getFilesDir().getAbsolutePath());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}


		/////----------------------------------------------------------

//		File filesDir = ConnectB.getFilesDir();
//		if (!filesDir.exists()) {
//			filesDir.mkdirs();
//		}
//		String name = "/" + Tools.newfileName;
//		File file = new File(filesDir + name);
//		LogUtil.defLog("存数据--" + file.getAbsolutePath());
//		if (!file.exists()) {
//			file.createNewFile();
//		}
//		BufferedInputStream is = new BufferedInputStream(s.getInputStream()); // 读进
//		BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));// 写出
//



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
	}

}
