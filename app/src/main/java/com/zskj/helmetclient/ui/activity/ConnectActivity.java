package com.zskj.helmetclient.ui.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.zskj.helmetclient.R;
import com.zskj.helmetclient.app.Constant;
import com.zskj.helmetclient.bean.Msg;
import com.zskj.helmetclient.bean.User;
import com.zskj.helmetclient.util.FileTcpServer;
import com.zskj.helmetclient.util.LogUtil;
import com.zskj.helmetclient.util.Tools;

import java.io.File;
import java.math.BigDecimal;

/**
 * 作者：yangwenquan on 2016/11/25
 * 类描述：连接界面
 */
public class ConnectActivity extends SimpleBaseActivity {
	private User person = null;
	private User me = null;
	private Button btn_send_file;
	public Tools tools = null;

	private static final int PHOTO_GALLERY = 1;// 从相册中选择
	private static final int PHOTO_CAMERA = 2;// 拍照
	private static final int FLAG_REQUEST_CAMERA_VIDEO = 3;//视频文件

	private static final String PHOTO_FILE_NAME = "zskj.jpg";// 文件名称
	private File tempFile;
	public String choosePath = null;// 选中的文件

	private TextView text;
	private ProgressDialog proDia = null;
	private Double fileSize = 0.0;
	private Msg m = null;

	private String mediaFilePath;
	private PopupWindow btnPopupWindow;

	@Override
	protected void onActivityCreated(Bundle savedInstanceState) {
		setContentView(R.layout.activity_connect);
	}

	@Override
	protected void initView() {
		text = (TextView) findViewById(R.id.text);
		btn_send_file = (Button) findViewById(R.id.btn_send_file);
		initProgressDialog();
	}

	private void initProgressDialog() {
		proDia = new ProgressDialog(this);
		proDia.setTitle("文件发送");// 设置标题
		proDia.setMessage("文件");// 设置显示信息
		proDia.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 水平进度条
		proDia.setMax(100);// 设置最大进度指
		proDia.setProgress(10);// 开始点
	}

	@Override
	protected void initData() {
		Tools.State = Tools.CONNECTACTIVITY;
		Tools.ConnectB = this;
		Intent intent = getIntent();
		person = (User) intent.getExtras().getSerializable("person");
		me = (User) intent.getExtras().getSerializable("me");
		text.setText("person : " + person.getName() + "---" + person.getIp());
		tools = new Tools(ConnectActivity.this, Tools.ACTIVITY_CHART);
	}


	@Override
	protected void setListener() {
		btn_send_file.setOnClickListener(this);
	}

	@Override
	protected void onClickEvent(View v) {
		switch (v.getId()) {
			case R.id.btn_send_file:
				btnUploadPressed();
				break;
			case R.id.btn_tupian_upload:// 图片上传
				openImage();
				break;
			case R.id.btn_bendi_upload:// 本地视频上传
				openLocationVideo();
				break;
			case R.id.btn_paishe_upload:// 拍摄视频上传
				takenCamaraVideo();
			default:
				break;
		}

	}

	private void btnUploadPressed() {
		View view = LayoutInflater.from(this).inflate(R.layout.popupwindow, null);

		TextView btn_tupian_upload = (TextView) view.findViewById(R.id.btn_tupian_upload);
		TextView btn_bendi_upload = (TextView) view.findViewById(R.id.btn_bendi_upload);
		TextView btn_paishe_upload = (TextView) view.findViewById(R.id.btn_paishe_upload);

		btn_tupian_upload.setOnClickListener(this);
		btn_bendi_upload.setOnClickListener(this);
		btn_paishe_upload.setOnClickListener(this);

		btnPopupWindow = new PopupWindow(view, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
		btnPopupWindow.setTouchable(true);// 默认为true

		btnPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		btnPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// 点击外部不消失
		// BtnpPpupWindow.setOutsideTouchable(false); //不好使
		// 设置弹出的位置
		btnPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

	}

	// 打开相册
	private void openImage() {
		//点击消失
		btnPopupWindow.dismiss();
		// 激活系统图库，选择一张图片
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(intent, PHOTO_GALLERY);
	}

	//本地的视频文件
	private void openLocationVideo() {
		//点击消失
		btnPopupWindow.dismiss();
		// 激活系统图库，
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("video/*");
		startActivityForResult(intent, FLAG_REQUEST_CAMERA_VIDEO);
	}

	//
	private void takenCamaraVideo() {
		btnPopupWindow.dismiss();
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// 判断存储卡是否可以用，可用进行存储
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
		}
		startActivityForResult(intent, PHOTO_CAMERA);
	}



	// 发送文件
	private void SendFile(String UriFile) {
		// Tools.CMD_FILEREQUEST, 特别标注 发送文件
		Object body = new File(UriFile).getName() + Tools.sign + (new File(UriFile)).length();

		LogUtil.printI(Constant.G_TAG, new File(UriFile).length() + "gggg");
		Msg m = new Msg(me.getName(), me.getIp(), person.getName(), person.getIp(), Tools.CMD_FILEREQUEST,
				body);
		LogUtil.defLog("MSG" + m);
		tools.sendMsg(m);

	}

	//获取视频文件的路径
	protected String getVideoPath(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		if (cursor.moveToFirst()) {
			mediaFilePath = cursor.getString(cursor.getColumnIndex("_data"));
		}
		return mediaFilePath;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PHOTO_GALLERY) {
			if (data != null && data.getData() != null) {
				// 得到图片的全路径
				String url = imageFitfer(data);
				choosePath = url;
				LogUtil.printI(Constant.APP_TAG, choosePath + "gggg");
				SendFile(url);
			}

		} else if (requestCode == FLAG_REQUEST_CAMERA_VIDEO) {
			if (data != null && data.getData() != null) {
				// 得到视频的全路径
				Uri videoUri = data.getData();
				choosePath = getVideoPath(videoUri);
				LogUtil.printI(Constant.APP_TAG, choosePath + "mmm");
				SendFile(choosePath);
			}
		} else if (requestCode == PHOTO_CAMERA) {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
				Bitmap imageBitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
				if (imageBitmap != null) {
					String fromFile = Uri.fromFile(tempFile).toString();
					choosePath = fromFile;
					SendFile(fromFile);
					Log.i(TAG, fromFile.toString());
				}
			} else {
				Toast.makeText(getApplicationContext(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
			}
		}
	}

	//将文件的URL转成文件路径
	@SuppressLint("NewApi")
	private String imageFitfer(Intent data) {
		if (data != null) {
			Uri uri = data.getData();
			if (uri != null) {
				String scheme = uri.getScheme();
				String filePath = "";
				if ("content".equals(scheme)) {// android 4.4以上版本处理方式
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
							&& DocumentsContract.isDocumentUri(this, uri)) {
						String wholeID = DocumentsContract.getDocumentId(uri);
						String id = wholeID.split(":")[1];
						String[] column = {MediaStore.Images.Media.DATA};
						String sel = MediaStore.Images.Media._ID + "=?";
						Cursor cursor = this.getContentResolver().query(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel,
								new String[]{id}, null);
						if (cursor != null && cursor.moveToFirst()) {
							int columnIndex = cursor.getColumnIndex(column[0]);
							filePath = cursor.getString(columnIndex);

							cursor.close();
							return filePath;
						} else {
							return null;
						}
					} else {// android 4.4以下版本处理方式
						String[] filePathColumn = {MediaStore.Images.Media.DATA};
						Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
						if (cursor != null && cursor.moveToFirst()) {
							int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
							filePath = cursor.getString(columnIndex);
							cursor.close();
							return filePath;
						} else {
							return null;
						}
					}
				} else if ("file".equals(scheme)) {// 小米云相册处理方式
					return uri.getPath();
				} else {
					return null;
				}

			} else {
				return null;
			}

		}
		return null;
	}


	// 计算文件大小
	public static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			return size + "Byte(s)";
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "KB";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "MB";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
				+ "TB";
	}


	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Tools.SHOW:
					Toast.makeText(ConnectActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
					break;
				case Tools.FILE_JINDU:
					String[] pi = ((String) msg.obj).split(Tools.sign);
					fileSize = Double.parseDouble(pi[2]);
					proDia.setTitle(pi[0]);// 设置标题
					proDia.setMessage(pi[1] + " 大小：" + getFormatSize(fileSize));// 设置显示信息
					proDia.onStart();
					proDia.show();
					break;
				case Tools.PROGRESS_FLUSH:
					int i0 = (int) ((Tools.sendProgress / (fileSize)) * 100);
					proDia.setProgress(i0);
					break;
				case Tools.PROGRESS_COL:// 关闭进度条
					proDia.dismiss();
					//发动成功 跳转
					Intent intent = new Intent(ConnectActivity.this, ShowImageActivity.class);
					intent.putExtra(Constant.FILENAME, Tools.ConnectB.choosePath);
					startActivity(intent);
					break;
				case Tools.CMD_FILEREQUEST:
					// 、、、、j接受文件请求
					LogUtil.defLog("\t\t\t\t\t// 、、、、j接受文件请求\n");
					receiveFile((Msg) msg.obj);
					break;

				default:
					break;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		Tools.State = Tools.ACTIVITY_CHART;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Tools.State = Tools.MAINACTIVITY;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Tools.State = Tools.MAINACTIVITY;
	}

	// 收到传送文件请求
	private void receiveFile(Msg mes) {
		this.m = mes;
		String str = m.getBody().toString();

		new AlertDialog.Builder(this)
				.setTitle(
						"是否接收文件：" + str.split(Tools.sign)[0] + " 大小："
								+ getFormatSize(Double.parseDouble(str.split(Tools.sign)[1])))
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("接受", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						try {
							// 接收文件 返回提示接受 建立tcp 服务器 接收文件
							FileTcpServer ts = new FileTcpServer(ConnectActivity.this);
							ts.start();
							Tools.sendProgress = 0;
							Message m1 = new Message();
							m1.what = Tools.FILE_JINDU;
							m1.obj = "接收文件" + Tools.sign + "正在接收：" + Tools.newfileName + Tools.sign
									+ Tools.newfileSize;
							handler.sendMessage(m1);

							fileProgress();// 启动进度条线程

							// 发送消息 让对方开始发送文件     ------- 出问题出现一
							Msg msg = new Msg(MainActivity.user.getName(), MainActivity.user.getIp(), m.getSendUserName(), m.getSendUserIp(),
									Tools.CMD_FILEACCEPT, null);
							LogUtil.defLog("发送消息 让对方开始发送文件" + msg);
							tools.sendMsg(msg);


						} catch (Exception e) {
							e.printStackTrace();
						}
						return;
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 不接受 返回提示不接受

						return;
					}
				}
		).show();
	}

	// 文件传送进度条
	public void fileProgress() {
		new Thread() {
			public void run() {
				while (Tools.sendProgress != -1) {
					Message m = new Message();
					m.what = Tools.PROGRESS_FLUSH;
					handler.sendMessage(m);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// 关闭进度条
				Message m1 = new Message();
				m1.what = Tools.PROGRESS_COL;
				handler.sendMessage(m1);
			}
		}.start();
	}
}
