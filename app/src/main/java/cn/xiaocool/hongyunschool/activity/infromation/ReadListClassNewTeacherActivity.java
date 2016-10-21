package cn.xiaocool.hongyunschool.activity.infromation;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.bean.ClassNewsSend;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.CommonAdapter;
import cn.xiaocool.hongyunschool.utils.ViewHolder;
import cn.xiaocool.hongyunschool.view.NoScrollListView;

public class ReadListClassNewTeacherActivity extends BaseActivity {

    @BindView(R.id.activity_read_list_tv_yidu)
    TextView activityReadListTvYidu;
    @BindView(R.id.activity_read_list_lv_yidu)
    NoScrollListView activityReadListLvYidu;
    @BindView(R.id.activity_read_list_tv_weidu)
    TextView activityReadListTvWeidu;
    @BindView(R.id.activity_read_list_lv_weidu)
    NoScrollListView activityReadListLvWeidu;
    private Context context;
    private ArrayList<ClassNewsSend.ReceiverlistBean> notReads = new ArrayList<>();
    private ArrayList<ClassNewsSend.ReceiverlistBean> alreadyReads = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_list);
        ButterKnife.bind(this);
        setTopName("列表");
        context = this;
        getData();
        setContent();
    }

    private void setContent() {
        activityReadListTvWeidu.setText(notReads.size()+"");
        activityReadListTvYidu.setText(alreadyReads.size()+"");
        activityReadListLvWeidu.setAdapter(new CommonAdapter<ClassNewsSend.ReceiverlistBean>(context, notReads, R.layout.item_read_info) {
            @Override
            public void convert(ViewHolder holder, ClassNewsSend.ReceiverlistBean receiverBean) {
                holder.setText(R.id.item_read_info_tv_name, receiverBean.getReceiver_info().get(0).getName());
                holder.setImageByUrl(R.id.item_read_info_iv_avatar, receiverBean.getReceiver_info().get(0).getPhoto());
            }
        });
        activityReadListLvYidu.setAdapter(new CommonAdapter<ClassNewsSend.ReceiverlistBean>(context,alreadyReads,R.layout.item_read_info) {
            @Override
            public void convert(ViewHolder holder, ClassNewsSend.ReceiverlistBean receiverBean) {
                holder.setText(R.id.item_read_info_tv_name, receiverBean.getReceiver_info().get(0).getName());
                holder.setImageByUrl(R.id.item_read_info_iv_avatar, receiverBean.getReceiver_info().get(0).getPhoto());
            }
        });

    }

    /**
     * 根据intent接收值
     */
    private void getData() {
        alreadyReads = (ArrayList<ClassNewsSend.ReceiverlistBean>) getIntent().getSerializableExtra("yidu");
        notReads = (ArrayList<ClassNewsSend.ReceiverlistBean>) getIntent().getSerializableExtra("weidu");
    }

    @Override
    public void requsetData() {

    }
}
