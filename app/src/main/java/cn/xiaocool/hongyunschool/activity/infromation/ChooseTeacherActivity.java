package cn.xiaocool.hongyunschool.activity.infromation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.adapter.EListAdapter;
import cn.xiaocool.hongyunschool.bean.Child;
import cn.xiaocool.hongyunschool.bean.Group;
import cn.xiaocool.hongyunschool.bean.TeacherInfo;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.net.VolleyUtil;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.utils.ToastUtil;

public class ChooseTeacherActivity extends BaseActivity {

    @BindView(R.id.listView)
    ExpandableListView listView;
    @BindView(R.id.down_selected_num)
    TextView downSelectedNum;
    @BindView(R.id.quan_check)
    CheckBox quanCheck;
    private List<TeacherInfo> teacherInfos;

    private ArrayList<Group> groups;
    private ArrayList<Child> childs;
    private EListAdapter adapter;
    private int size;
    private ArrayList<String> selectedIds, selectedNames;
    private Context context;
    private String type;
    private String hasData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_reciver);
        ButterKnife.bind(this);
        setTopName("选择接收人");
        getIntentData();
        context = this;
        setRightText("完成").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hasData.equals("error")){
                    ToastUtil.showShort(context, "暂无接收人！");
                    return;
                }
                getAllMenbers();
                if (selectedIds.size() > 0) {

                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("ids", selectedIds);
                    intent.putStringArrayListExtra("names", selectedNames);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {

                    ToastUtil.showShort(ChooseTeacherActivity.this, "请选择接收人！");
                }
            }
        });
        teacherInfos = new ArrayList<>();
        groups = new ArrayList<>();
        childs = new ArrayList<>();
    }

    private void getIntentData() {
        type = getIntent().getStringExtra("type");
    }

    @Override
    public void requsetData() {
        String url = NetConstantUrl.GET_SCHOOL_TEACHER + SPUtils.get(context, LocalConstant.SCHOOL_ID,"1");
        VolleyUtil.VolleyGetRequest(this, url, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(ChooseTeacherActivity.this, result)) {
                    teacherInfos.clear();
                    teacherInfos.addAll(getBeanFromJson(result));
                    groups.clear();
                    childs.clear();
                    setAdapter();
                } else {
                    hasData = "error";
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {

        changeModelForElistmodel();
        if (adapter==null){
            adapter = new EListAdapter(ChooseTeacherActivity.this, groups, quanCheck, downSelectedNum ,"2");
            listView.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }

        listView.setGroupIndicator(null);
    }

    /**
     * 转换模型
     */
    private void changeModelForElistmodel() {
        if (type.equals("message")){
            for (int i = 0; i < teacherInfos.size(); i++) {
                Group group = new Group(teacherInfos.get(i).getId(), teacherInfos.get(i).getName());
                for (int j = 0; j < teacherInfos.get(i).getTeacherinfo().size(); j++) {
                    Child child = new Child(teacherInfos.get(i).getTeacherinfo().get(j).getPhone(), teacherInfos.get(i).getTeacherinfo().get(j).getName(),
                            teacherInfos.get(i).getTeacherinfo().get(j).getName());
                    group.addChildrenItem(child);
                }
                groups.add(group);
            }

        }else {
            for (int i = 0; i < teacherInfos.size(); i++) {
                Group group = new Group(teacherInfos.get(i).getId(), teacherInfos.get(i).getName());
                for (int j = 0; j < teacherInfos.get(i).getTeacherinfo().size(); j++) {
                    Child child = new Child(teacherInfos.get(i).getTeacherinfo().get(j).getId(), teacherInfos.get(i).getTeacherinfo().get(j).getName(),
                            teacherInfos.get(i).getTeacherinfo().get(j).getName());
                    group.addChildrenItem(child);
                }
                groups.add(group);
            }
        }


    }

    /**
     * 字符串转模型
     *
     * @param result
     * @return
     */
    private List<TeacherInfo> getBeanFromJson(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<TeacherInfo>>() {
        }.getType());
    }

    @OnClick(R.id.quan_check)
    public void onClick() {
        if (hasData.equals("error")){
            ToastUtil.showShort(context, "暂无接收人！");
            return;
        }
        if (quanCheck.isChecked()) {
            size = 0;
            for (int i = 0; i < groups.size(); i++) {
                groups.get(i).setChecked(true);
                size += groups.get(i).getChildrenCount();
                for (int j = 0; j < groups.get(i).getChildrenCount(); j++) {
                    groups.get(i).getChildItem(j).setChecked(true);
                }
            }
            adapter.notifyDataSetChanged();
            listView.setOnChildClickListener(adapter);
            for (int i = 0; i < groups.size(); i++) {
                listView.expandGroup(i);
            }
            downSelectedNum.setText("已选择" + size + "人");

        } else {
            for (int i = 0; i < groups.size(); i++) {
                groups.get(i).setChecked(false);
                for (int j = 0; j < groups.get(i).getChildrenCount(); j++) {
                    groups.get(i).getChildItem(j).setChecked(false);
                }
            }
            adapter.notifyDataSetChanged();
            listView.setOnChildClickListener(adapter);
            for (int i = 0; i < groups.size(); i++) {
                listView.expandGroup(i);
            }
            downSelectedNum.setText("已选择0人");
        }
    }


    /**
     * 获取群组初始人员
     */
    private void getAllMenbers() {
        selectedIds = new ArrayList<>();
        selectedNames = new ArrayList<>();
        for (int i = 0; i < adapter.getterGroups().size(); i++) {
            for (int j = 0; j < adapter.getterGroups().get(i).getChildrenCount(); j++) {
                if (adapter.getterGroups().get(i).getChildItem(j).getChecked()) {
                    selectedIds.add(adapter.getterGroups().get(i).getChildItem(j).getUserid());
                    selectedNames.add(adapter.getterGroups().get(i).getChildItem(j).getFullname());
                } else {
                    Log.e("checked", String.valueOf(adapter.getterGroups().get(i).getChildItem(j).getChecked()));

                }

            }
        }

    }
}
