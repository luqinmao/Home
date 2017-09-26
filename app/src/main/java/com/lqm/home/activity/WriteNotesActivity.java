package com.lqm.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqm.home.R;
import com.lqm.home.model.Note;
import com.lqm.home.utils.DateUtil;
import com.lqm.home.utils.T;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lqm.home.R.id.tv_day;

/**
 * 写日记
 */
public class WriteNotesActivity extends BaseActivity {


    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(tv_day)
    TextView tvDay;
    @Bind(R.id.tv_minute)
    TextView tvMinute;
    @Bind(R.id.tv_submit)
    TextView tvSubmit;
    @Bind(R.id.et_content)
    EditText etContent;
    private String lastContent;
    private long mId;
    private String mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_notes);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        lastContent = intent.getStringExtra("content");
        mId = intent.getLongExtra("id",0);
        mTime = intent.getStringExtra("time");

        if(lastContent != null){
            etContent.setText(lastContent);
            etContent.setSelection(lastContent.length());
        }
        tvDay.setText(mTime);

    }

    @OnClick({R.id.iv_back, R.id.tv_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_submit:
                if (etContent.getText().toString().length() == 0){
                    T.showShort(this,"请输入内容");
                }else{
                    Note note =new Note();
                    note.setTitle(getTitleText());
                    note.setContent(getContentText());
                    note.setTime(DateUtil.getNowDate());
                    if (lastContent == null){  //添加日志
                        note.save();
                        Intent mIntent = new Intent();
                        mIntent.putExtra("noteData",note);
                        setResult(10, mIntent);
                        T.showShort(this,"已完成");
                        finish();
                    }else{  //修改日志
                        note.update(mId);
                        Intent mIntent = new Intent();
                        mIntent.putExtra("EditData",note);
                        setResult(20, mIntent);
                        T.showShort(this,"已完成");
                        finish();
                    }
                }

                break;
        }
    }


    //获取EditText第一行文本
    private String getTitleText(){
        Layout layout=etContent.getLayout();
        String result="";
        String text=layout.getText().toString();
        int start=layout.getLineStart(0);
        int end=layout.getLineEnd(0);
        result = text.substring(start, end);
        return result;
    }

    //获取EditText除标题外的文本
    private String getContentText(){
        int line=etContent.getLayout().getLineCount();
        Layout layout=etContent.getLayout();
        String result="";
        String text=layout.getText().toString();

        int start=layout.getLineStart(1);
        int end=layout.getLineEnd(line-1);
        result=text.substring(start, end);
        return result;
    }
}
