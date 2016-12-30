package com.zskj.helmetclient.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.zskj.helmetclient.R;
import com.zskj.helmetclient.app.Constant;
import com.zskj.helmetclient.util.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @Description: 展示文件类
 * @author：GaomingShuo
 * @date：${DATA} 10:18
 */
public class ShowImageActivity extends SimpleBaseActivity {
	private ImageView iamgeview;
	private VideoView videoView;

	@Override
	protected void onActivityCreated(Bundle savedInstanceState) {
		setContentView(R.layout.activity_show);
	}

	@Override
	protected void initView() {
		iamgeview = (ImageView) findViewById(R.id.iamgeview);
		videoView = (VideoView) findViewById(R.id.video_View);
		Intent intent = getIntent();
		///data/data/com.zskj.helmetclient/filesPicture_11_Taste.jpg:
		String path = intent.getStringExtra(Constant.FILENAME);
		if (path.contains(".mp4")) {
			///storage/emulated/0/Android/data/com.zskj.helmetclient/filesV61205-132712.mp4
			LogUtil.printI("pathpathpathpath", path);
			Uri uri = Uri.parse(path);
			videoView.setVisibility(View.VISIBLE);
			iamgeview.setVisibility(View.GONE);
			//播放完成回调
			videoView.setOnCompletionListener(new MyPlayerOnCompletionListener());
			videoView.setVideoURI(uri);
			videoView.start();
		} else {
			videoView.setVisibility(View.GONE);
			iamgeview.setVisibility(View.VISIBLE);
			Bitmap bitmap = getLoacalBitmap(path);
			LogUtil.printI("xsxsxsxsxsxs", "取数据---" + bitmap);
			iamgeview.setImageBitmap(bitmap);
		}
	}

	class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer mp) {
			Toast.makeText(ShowImageActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
		}
	}


	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	//暂时保留打开视频文件
	public static Intent getVideoFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void setListener() {

	}

	@Override
	protected void onClickEvent(View v) {

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.defLog("onDestroy");
		finish();
	}
}
