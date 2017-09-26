package com.lqm.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lqm.home.R;
import com.lqm.home.adapter.NotesAdapter;
import com.lqm.home.model.Note;
import com.lqm.home.utils.DateUtil;
import com.lqm.home.utils.PrefUtils;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lqm.home.R.id.ib_add_notes;
import static com.lqm.home.R.id.ib_view;

/**
 * 日记列表界面
 */
public class NotesActivity extends BaseActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(ib_view)
    ImageButton ibView;
    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(ib_add_notes)
    ImageButton ibAddNotes;
    @Bind(R.id.btn_delete)
    Button btnDelete;
    @Bind(R.id.btn_select_all)
    Button btnSelectAll;
    @Bind(R.id.btn_select_cancel)
    Button btnSelectCancel;
    @Bind(R.id.ll_function_view)
    LinearLayout llFunctionView;
    private List<Note> notes;
    private NotesAdapter contentAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

        notes = DataSupport.order("time desc").find(Note.class);

        if (PrefUtils.getBoolean(NotesActivity.this, "IsLinearLayoutManager", true) == true) {
            rvContent.setLayoutManager(new LinearLayoutManager(this));
        } else {
            rvContent.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }
        contentAdapter = new NotesAdapter(notes, NotesActivity.this);
        rvContent.setAdapter(contentAdapter);

        contentAdapter.setRecyclerViewOnItemClickListener(new NotesAdapter.RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                if (llFunctionView.getVisibility() == View.GONE) {
                    Intent intent = new Intent(NotesActivity.this, WriteNotesActivity.class);
                    intent.putExtra("content", notes.get(position).getTitle() + notes.get(position).getContent());
                    intent.putExtra("id", notes.get(position).getId());
                    intent.putExtra("time", DateUtil.date2string(notes.get(position).getTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));
                    startActivityForResult(intent, 2);
                } else {
                    //设置选中的项
                    contentAdapter.setSelectItem(position);
                }
            }

            @Override
            public boolean onItemLongClickListener(View view, int position) {
                if (llFunctionView.getVisibility() == View.GONE) {
                    llFunctionView.setVisibility(View.VISIBLE);
                    ibAddNotes.setVisibility(View.GONE);
                } else {
                    llFunctionView.setVisibility(View.GONE);
                    ibAddNotes.setVisibility(View.VISIBLE);
                }
                contentAdapter.setShowBox();
                //设置选中的项
                contentAdapter.setSelectItem(position);
                contentAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @OnClick({R.id.iv_back, ib_view, ib_add_notes, R.id.btn_delete, R.id.btn_select_all, R.id.btn_select_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case ib_view:
                if (PrefUtils.getBoolean(NotesActivity.this, "IsLinearLayoutManager", true) == true) {
                    rvContent.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    ibView.setBackgroundResource(R.mipmap.menu);
                    PrefUtils.setBoolean(NotesActivity.this, "IsLinearLayoutManager", false);
                } else {
                    rvContent.setLayoutManager(new LinearLayoutManager(this));
                    ibView.setBackgroundResource(R.mipmap.notes_layout);
                    PrefUtils.setBoolean(NotesActivity.this, "IsLinearLayoutManager", true);
                }
                break;

            case ib_add_notes:
                Intent intent = new Intent(this, WriteNotesActivity.class);
                startActivityForResult(intent, 1);
                break;

            case R.id.btn_delete:  //删除
                Map<Integer, Boolean> map2 = contentAdapter.getMap();
                for (int i = 0; i < map2.size(); i++) {
                    if (map2.get(i) == true) {
                        DataSupport.delete(Note.class, notes.get(i).getId());
                        notes.remove(i);
                        map2.put(i, false);
                        contentAdapter.isshowBox = false;
                        contentAdapter.notifyDataSetChanged();
                    } else {
                        contentAdapter.isshowBox = false;
                        contentAdapter.notifyDataSetChanged();
                    }

                }
                llFunctionView.setVisibility(View.GONE);
                ibAddNotes.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_select_all: //全选
                Map<Integer, Boolean> map = contentAdapter.getMap();
                for (int i = 0; i < map.size(); i++) {
                    map.put(i, true);
                    contentAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_select_cancel:  //取消
                Map<Integer, Boolean> m = contentAdapter.getMap();
                for (int i = 0; i < m.size(); i++) {
                    m.put(i, false);
                    contentAdapter.isshowBox = false;
                    contentAdapter.notifyDataSetChanged();
                }
                llFunctionView.setVisibility(View.GONE);
                ibAddNotes.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (llFunctionView.getVisibility() == View.VISIBLE) {
                Map<Integer, Boolean> bm = contentAdapter.getMap();
                for (int i = 0; i < bm.size(); i++) {
                    bm.put(i, false);
                    contentAdapter.isshowBox = false;
                    contentAdapter.notifyDataSetChanged();
                }
                llFunctionView.setVisibility(View.GONE);
                ibAddNotes.setVisibility(View.VISIBLE);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case 10:   //添加按钮返回数据
                Note mNote = (Note) data.getSerializableExtra("noteData");
                notes.add(0, mNote);  //添加到第一个位置
                contentAdapter.notifyDataSetChanged();
                break;

            case 20:   //修改Item返回
//                Note EditNote = (Note) data.getSerializableExtra("EditData");
                notes.clear();
                notes.addAll(DataSupport.order("time desc").find(Note.class));
                contentAdapter.notifyDataSetChanged();
                break;
        }
    }

}
