package com.zskj.helmetclient.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zskj.helmetclient.R;
import com.zskj.helmetclient.bean.User;
import com.zskj.helmetclient.widget.recyclerview.MyItemClickListener;
import com.zskj.helmetclient.widget.recyclerview.RecyclerItemViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * 作者：yangwenquan on 16/7/19
 * 类描述：所有用户适配器
 */
public class AllUserRecAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<User> userList = new ArrayList<>();
    private Context context;
    private MyItemClickListener myItemClickListener;
//    private BottomClickListener mBottomClickListener;

    public AllUserRecAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        return new ItemViewHolder(mInflater.inflate(R.layout.item_all_user, viewGroup, false), myItemClickListener);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (userList != null && userList.size() > 0) {
           User user = userList.get(position);
            if (viewHolder instanceof ItemViewHolder) {
                ((ItemViewHolder) viewHolder).name.setText(user.getName());
                ((ItemViewHolder) viewHolder).ip.setText("IP:"+user.getIp());
            }
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ItemViewHolder extends RecyclerItemViewHolder {
        public TextView name;
        public TextView ip;

        public ItemViewHolder(View view, MyItemClickListener clickListener) {
            super(view, clickListener);
            name = (TextView) view.findViewById(R.id.name);
            ip = (TextView) view.findViewById(R.id.ip);
        }
    }

//    //底部接口
//    public interface BottomClickListener {
//        public void onBottomClick(RecyclerView.ViewHolder holder, int position);
//    }
//
//    //底部的监听
//    public void setOnButtomClickListener(BottomClickListener bottomClickListener) {
//        this.mBottomClickListener = bottomClickListener;
//    }

    //每个item的监听
    public void setOnItemClickListener(MyItemClickListener listener) {
        this.myItemClickListener = listener;
    }
}
