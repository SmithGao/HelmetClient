package com.zskj.helmetclient.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zskj.helmetclient.util.LogUtil;


/**
 * @version V1.0
 * @ClassName: SimpleBaseActivity
 * @Description: 简单的baseActivity
 * @author：LiZhimin
 * @备注：
 */
public abstract class SimpleBaseActivity extends BaseActivity implements OnClickListener {

    private long clickTime = 0;
    protected Toolbar toolbar;
    protected TextView tv_right;
	protected TextView toolbarTitle;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onActivityCreated(savedInstanceState);
//        initToolBar();
        initView();
        initData();
        setListener();
    }

    /**
     * @MethodName:onActivityCreated
     * @Description: 相当于onCreate, 为了不让子类显示super.onCreate()而诞生的方法
     */
    protected abstract void onActivityCreated(Bundle savedInstanceState);

    /**
     * @MethodName:initToolBar
     * @Description: 注册标题Toolbar
     */
//    protected void initToolBar() {
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//		toolbarTitle= (TextView) findViewById(R.id.toolbar_title);
//		if (toolbar != null) {
//            toolbar.setTitle("");
//            setSupportActionBar(toolbar);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            if (!TAG.equals(MainActivity.class.getSimpleName())) {
//                toolbar.setNavigationIcon(R.drawable.new_back);
//                toolbar.setNavigationOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        onLeftClick();
//                    }
//                });
//            } else {
//                toolbar.setNavigationIcon(null);
//            }
//            tv_right = (TextView) findViewById(R.id.tv_right);
//            if (tv_right != null) {
//                tv_right.setOnClickListener(this);
//            }
//        }
//    }

    protected abstract void initView();


    /**
     * @MethodName:initData
     * @Description: 设置页面数据（包括本地数据 或者网络请求数据） 需自己在合适的位置调用
     */
    protected abstract void initData();


    /**
     * @MethodName:setListener
     * @Description: 设置监听 已在oncreate中调用
     */
    protected abstract void setListener();

    /**
     * @param v void
     * @MethodName:onClickEvent
     * @Description: 点击事件包装
     */
    protected abstract void onClickEvent(View v);

    @Override
    public void onClick(View v) {
        if ((System.currentTimeMillis() - clickTime) > 500) {
            clickTime = System.currentTimeMillis();
        } else {
            LogUtil.printD(TAG, "点的太快了吧你");
            return;
        }
        onClickEvent(v);
    }

    protected void onLeftClick() {
        onBackPressed();
    }

}
