package com.zskj.helmetclient.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zskj.helmetclient.util.LogUtil;


public class BaseActivity extends AppCompatActivity {
	protected String TAG = getClass().getSimpleName();
	protected Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;
		super.onCreate(savedInstanceState);
		LogUtil.printI("BaseActivity", TAG + "->onCreate");
	}

	@Override
	protected void onStart() {
		super.onStart();
		LogUtil.printI("BaseActivity", TAG + "->onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.printI("BaseActivity", TAG + "->onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.printI("BaseActivity", TAG + "->onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtil.printI("BaseActivity", TAG + "->onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.printI("BaseActivity", TAG + "->onDestroy");
	}

	/**
	 * Activity跳转
	 */
	protected void goToActivity(Class<?> clazz) {
		Intent intent = new Intent(context, clazz);
		startActivity(intent);
	}

//	/**
//	 * @author mingshuo
//	 * created at 16/1/21 11:32
//	 * TODO:fragment 跳转
//	 */
//	protected void goToFragment(Class<?> clazz, int gotoType, int gotonum) {
//		Intent intent = new Intent(context, clazz);
//		intent.putExtra(KeyUtil.GotoType, gotoType);
//		intent.putExtra(KeyUtil.FragmentKey, gotonum);
//		startActivity(intent);
//	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == Constant.FINISH_THIS_ACTIVITY && resultCode == RESULT_OK) {
//			finishActivity();
//		}
//	}
//
//	protected void finishActivity() {
//		((Activity) context).setResult(RESULT_OK);
//		finish();
//	}

}
