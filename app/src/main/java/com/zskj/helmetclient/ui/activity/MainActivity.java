package com.zskj.helmetclient.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zskj.helmetclient.R;
import com.zskj.helmetclient.bean.Msg;
import com.zskj.helmetclient.bean.User;
import com.zskj.helmetclient.ui.adapter.AllUserRecAdapter;
import com.zskj.helmetclient.util.LogUtil;
import com.zskj.helmetclient.util.Tools;
import com.zskj.helmetclient.widget.recyclerview.HorizontalDividerItemDecoration;
import com.zskj.helmetclient.widget.recyclerview.MyItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.zskj.helmetclient.util.Tools.getLocalHostIp;

public class MainActivity extends SimpleBaseActivity {
	private RecyclerView rec_view;
	public List<User> userList = null;
	public List<User> adapterList = new ArrayList<>();
	public static User user;
	public static User user1 = null;
	private AllUserRecAdapter recAdapter;
	private Tools tools = null;
	public boolean isPaused = false;
	private Button button;
	private Msg m = null;
	private ProgressDialog proDia = null;
	private Double fileSize = 0.0;


	@Override
	protected void onActivityCreated(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void initView() {
		button = (Button) findViewById(R.id.btn);
		rec_view = (RecyclerView) findViewById(R.id.rec_view);
		rec_view.setPadding(rec_view.getPaddingLeft(), rec_view.getPaddingTop(), rec_view.getPaddingRight(), rec_view.getHeight());
		rec_view.setLayoutManager(new LinearLayoutManager(this));
		rec_view.addItemDecoration(
				new HorizontalDividerItemDecoration.Builder(this)
						.color(ContextCompat.getColor(this, R.color.black))
						.size(1)
						.build());


		//-----------------测试----------------------/
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			/* 得到SD卡得路径 */ // /storage/emulated/0
			File mRecAudioPath = Environment.getExternalStorageDirectory();
			/* 更新所有录音文件到List中 */
			LogUtil.printI("dddddddd", mRecAudioPath.getAbsolutePath());
		} else {
			Toast.makeText(MainActivity.this, "没有SD卡", Toast.LENGTH_LONG).show();
		}
		String s = Environment.getExternalStorageDirectory().toString();


		String filesDir = getExternalFilesDir(null).toString();

		LogUtil.printI("dddddddd---", s);
		LogUtil.printI("dddddddd---", filesDir);
		File path = Environment.getExternalStorageDirectory();
		if (!path.exists()) {
			path.mkdirs();
		}
		File file = new File(path + Tools.newsavepath + Tools.newfileName);
		LogUtil.printI("dddddddd---", file.getAbsolutePath());

	}

	@Override
	protected void initData() {
		//初始化布局
		Tools.State = Tools.MAINACTIVITY;//状态
		Tools.mainA = this;
		tools = new Tools(this, Tools.ACTIVITY_MAIN);
		initProgressDialog();
		init();
		reBroad();
		// 开启接收端 时时更新在线列表
		tools.receiveMsg();
		// 心跳
		tools.startCheck();
	}

	private void initProgressDialog() {
		proDia = new ProgressDialog(this);
		proDia.setTitle("文件发送");// 设置标题
		proDia.setMessage("文件");// 设置显示信息
		proDia.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 水平进度条
		proDia.setMax(100);// 设置最大进度指
		proDia.setProgress(10);// 开始点
	}

	//广播上线(包括自己)
	private void reBroad() {
		Msg msg = new Msg();
		msg.setSendUserName(user.getName());
		msg.setSendUserIp(user.getIp());
		msg.setReceiveUserIp(Tools.getBroadCastIP());
		LogUtil.printI("YWQ", "广播id?" + Tools.getBroadCastIP());
		msg.setMsgType(Tools.CMD_ONLINE);//通知上线命令
		msg.setDate(Tools.getTimel());
		// 发送广播通知上线
		tools.sendMsg(msg);
	}

	//初始化
	private void init() {
		userList = new ArrayList<>();
		userList.clear();
		user = new User(Build.MODEL, getLocalHostIp(), System.currentTimeMillis());
		userList.add(user);
		User me = new User("自己" + Build.MODEL, getLocalHostIp(), System.currentTimeMillis());
		adapterList.clear();
		adapterList.add(me);
		recAdapter = new AllUserRecAdapter(adapterList, context);
		rec_view.setAdapter(recAdapter);
	}

	@Override
	protected void setListener() {
		button.setOnClickListener(this);
		recAdapter.setOnItemClickListener(new MyItemClickListener() {
			@Override
			public void onitemClick(View view, int position) {
				if (position == 0)
					return;
				user1 = userList.get(position);
				openConnectActivity(user1);
				Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
				intent.putExtra("person", userList.get(position));//需要连接的用户
				intent.putExtra("me", user);//自己
				startActivity(intent);
			}
		});

	}

	//打开所连接客户端的页面
	private void openConnectActivity(User user1) {
		Msg msg = new Msg(user.getName(), user.getIp(), user1.getName(), user1.getIp(),
				Tools.CMD_OPEN_CONNECT_ACTIVITY, user);
		tools.sendMsg(msg);
	}

	@Override
	protected void onClickEvent(View v) {
		switch (v.getId()) {
			case R.id.btn:
//				initData();
				break;

			default:
				break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.defLog("onResume---MAINACTIVITY");
		isPaused = false;
		reBroad();
		Tools.State = Tools.MAINACTIVITY;
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.defLog("onPause");
		isPaused = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.defLog("onDestroy");
		isPaused = true;
	}

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Tools.FLUSH:
					recAdapter.notifyDataSetChanged();
					break;
				case Tools.ADDUSER://添加用户
					adapterList.add((User) msg.obj);
					recAdapter.notifyDataSetChanged();
					LogUtil.printI("YWQ", "更新用户上线");
					break;
				case Tools.DESTROYUSER://删除用户
					int i = (Integer) msg.obj;
					LogUtil.printI("YWQ", "删除用户" + userList.get(i).getIp());
					userList.remove(i);
					adapterList.remove(i);
					recAdapter.notifyDataSetChanged();
					break;
				case Tools.CMD_OPEN_CONNECT_ACTIVITY:
					User user2 = (User) msg.obj;
					String otherIp = Tools.otherIp;
					LogUtil.printI("YWQ", "本机的IP地址:" + getLocalHostIp() + "获得的IP地址:" + otherIp);

					if (getLocalHostIp().equals(otherIp)) {
						LogUtil.printI("YWQ", "跳转条件成立");
						Intent intent1 = new Intent(MainActivity.this, ConnectActivity.class);
						intent1.putExtra("person", user2);//需要连接的用户
						intent1.putExtra("me", user);//自己
						startActivity(intent1);
					}
					break;
				default:
					break;
			}
		}
	};


}
