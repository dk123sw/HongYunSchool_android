package cn.xiaocool.hongyunschool.utils;

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.xiaocool.hongyunschool.activity.ImageDetailActivity;
import cn.xiaocool.hongyunschool.adapter.ImgGridAdapter;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.view.NoScrollGridView;

/**
 * Created by Administrator on 2016/8/12 0012.
 * 自定义的设置图片、文字的类
 */
public class ViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    public SparseArray<View> getmViews() {
        return mViews;
    }

    public void setmViews(SparseArray<View> mViews) {
        this.mViews = mViews;
    }

    public View getmConvertView() {
        return mConvertView;
    }

    public void setmConvertView(View mConvertView) {
        this.mConvertView = mConvertView;
    }

    public ViewHolder(Context context, ViewGroup parent, int layoutId, int position){
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId,parent,false);
        mConvertView.setTag(this);
    }
    public static ViewHolder get(Context context,View convertView,ViewGroup parent,int layoutId,int posiion){
        if(convertView==null){
            return new ViewHolder(context,parent,layoutId,posiion);
        }else{
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mPosition = posiion;
            return holder;
        }
    }

    /**
     * 通过viewId获取控件
     * @param viewId
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);
        if(view==null){
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T) view;
    }

    /**
     * 设置TextView的值
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId,String text){
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置时间的text
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setTimeText(int viewId,String text){
        Date date = new Date();
        date.setTime(Long.parseLong(text)*1000);
        TextView tv = getView(viewId);
        tv.setText(new SimpleDateFormat("yyyy-MM-dd  HH:mm").format(date));
        return this;
    }

    /**
     * 设置图片Resource
     * @param viewId
     * @param resId
     * @return
     */
    public ViewHolder setImageResource(int viewId,int resId){
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public ViewHolder setImageByUrl(int viewId,String url){
        ImageView view = getView(viewId);
        ImgLoadUtil.display(NetConstantUrl.IMAGE_URL+url,view);
        return this;
    }


    public ViewHolder setItemImages(final Context mContext,int oneview,int gridview, final ArrayList<String> images){
        ImageView view = getView(oneview);
        NoScrollGridView gv = getView(gridview);

        if (images.size()>0){
            if (images.size()>1){
                view.setVisibility(View.GONE);
                gv.setVisibility(View.VISIBLE);
                gv.setAdapter(new ImgGridAdapter(images, mContext));

            }else if (images.size()==1&&!images.get(0).equals("null")&&!images.get(0).equals("")){
                view.setVisibility(View.VISIBLE);
                gv.setVisibility(View.GONE);
                ImgLoadUtil.display(NetConstantUrl.IMAGE_URL + images.get(0), view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtils.e(images.toString());
                        Intent intent = new Intent();
                        intent.setClass(mContext, ImageDetailActivity.class);
                        intent.putStringArrayListExtra("Imgs", images);
                        intent.putExtra("position",0);
                        intent.putExtra("type", "4");
                        mContext.startActivity(intent);
                    }
                });
            }else {
                    images.clear();
                    view.setVisibility(View.GONE);
                    gv.setVisibility(View.VISIBLE);
                    gv.setAdapter(new ImgGridAdapter(images, mContext));
            }



        }else {
            images.clear();
            view.setVisibility(View.GONE);
            gv.setVisibility(View.VISIBLE);
            gv.setAdapter(new ImgGridAdapter(images, mContext));
        }

        return this;
    }
}
