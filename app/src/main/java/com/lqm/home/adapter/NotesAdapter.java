package com.lqm.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lqm.home.R;
import com.lqm.home.model.Note;
import com.lqm.home.utils.DateUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日记适配器
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {
    //数据源
    private List<Note> list;
    private Context context;
    //是否显示单选框,默认false
    public boolean isshowBox = false;
    // 存储勾选框状态的map集合
    private Map<Integer, Boolean> map = new HashMap<>();
    //接口实例
    private RecyclerViewOnItemClickListener onItemClickListener;

    public NotesAdapter(List<Note> list, Context context) {
        this.list = list;
        this.context = context;
        initMap();
    }

    //初始化map集合,默认为不选中
    private void initMap() {
        for (int i = 0; i < list.size(); i++) {
            map.put(i, false);
        }
    }

    //视图管理
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_date;
        private CheckBox checkBox;
        private View root;

        public ViewHolder(View root) {
            super(root);
            this.root = root;

            tv_title = (TextView) root.findViewById(R.id.tv_title);
            tv_content = (TextView) root.findViewById(R.id.tv_content);
            tv_date = (TextView) root.findViewById(R.id.tv_date);
            checkBox = (CheckBox) root.findViewById(R.id.cb_item_select);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //绑定视图管理者
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_title.setText(list.get(position).getTitle());
        holder.tv_content.setText(list.get(position).getContent());
        holder.tv_date.setText(DateUtil.date2string(list.get(position).getTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));

        //长按显示/隐藏
        if (isshowBox) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.INVISIBLE);
        }
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.list_anim);
        //设置checkBox显示的动画
        if (isshowBox)
            holder.checkBox.startAnimation(animation);
        //设置Tag
        holder.root.setTag(position);
        //设置checkBox改变监听
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //用map集合保存
                map.put(position, isChecked);
            }
        });
        // 设置CheckBox的状态
        if (map.get(position) == null) {
            map.put(position, false);
        }
        holder.checkBox.setChecked(map.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notes, parent, false);
        ViewHolder vh = new ViewHolder(root);
        //为Item设置点击事件
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);
        return vh;
    }

    //点击事件
    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            onItemClickListener.onItemClickListener(v, (Integer) v.getTag());
        }
    }

    //长按事件
    @Override
    public boolean onLongClick(View v) {
        //不管显示隐藏，清空状态
        initMap();
        return onItemClickListener != null && onItemClickListener.onItemLongClickListener(v, (Integer) v.getTag());
    }

    //设置点击事件
    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //设置是否显示CheckBox
    public void setShowBox() {
        //取反
        isshowBox = !isshowBox;
    }

    //点击item选中CheckBox
    public void setSelectItem(int position) {
        //对当前状态取反
        if (map.get(position)) {
            map.put(position, false);
        } else {
            map.put(position, true);
        }
        notifyItemChanged(position);
    }

    //返回集合给MainActivity
    public Map<Integer, Boolean> getMap() {
        return map;
    }

    //接口回调设置点击事件
    public interface RecyclerViewOnItemClickListener {
        //点击事件
        void onItemClickListener(View view, int position);

        //长按事件
        boolean onItemLongClickListener(View view, int position);
    }
}