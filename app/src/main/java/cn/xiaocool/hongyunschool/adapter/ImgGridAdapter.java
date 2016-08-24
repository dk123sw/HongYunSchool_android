package cn.xiaocool.hongyunschool.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.activity.ImageDetailActivity;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.utils.ImgLoadUtil;


/**
 * Created by Administrator on 2016/5/27.
 */
public class ImgGridAdapter extends BaseAdapter {

    private int i;
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> mWorkImgs;
    private Context mContext;


    public ImgGridAdapter(ArrayList<String> workImgs, Context context) {
        this.mContext = context;
        this.mWorkImgs = workImgs;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.i = mWorkImgs.size();
    }

    @Override
    public int getCount() {

        return i > 9 ? 9 : i;
    }

    @Override
    public Object getItem(int position) {
        return i;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyGridViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new MyGridViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.user_img_item, parent, false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_user_img);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyGridViewHolder) convertView.getTag();
        }
        ImgLoadUtil.display(NetConstantUrl.IMAGE_URL+mWorkImgs.get(position), viewHolder.imageView);
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, ImageDetailActivity.class);
                intent.putStringArrayListExtra("Imgs", mWorkImgs);
                intent.putExtra("position",position);
                intent.putExtra("type", "4");
                mContext.startActivity(intent);
            }
        });


        return convertView;
    }

    private static class MyGridViewHolder {
        ImageView imageView;
    }
}
