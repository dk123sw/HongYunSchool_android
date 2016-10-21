package cn.xiaocool.hongyunschool.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.xiaocool.hongyunschool.R;

/**
 *  <Pre>
 * 鑷畾涔夊浘鐗囪嚜鍔ㄨ疆鎾帶浠讹紝鑷畾杞挱鎸囩ず鍣ㄦ牱寮忥紝鏀寔鐐瑰嚮锛屾棤闄愯疆鎾紝缃戠粶涓嬭浇鍥剧墖</br>
 * 鍙槸浣跨敤XUtil鐨凚itmapUtils涔熷彲鏄娇鐢╯mart-image-vie敮鎸佽疆鎾枃瀛楀垏鎹?</br>
 * 姝ゆ彃浠舵槸鍩轰簬viewpager瀹炵幇鐨?,闇?瑕佸鍏ndroid-support-v4.jar</br></br>
 *
 * 濡傛灉浣跨敤缃戠粶鍥剧墖璁板緱鍔犳潈闄愩??</br>
 * uses-permission android:name="android.permission.INTERNET"
 * uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
 *
 * 涓昏鍔熻兘:</br></br>
 * 1.鏀寔璁剧疆鏂囧瓧鎻愮ず</br>
 * 2.鏀寔淇敼杞挱鎸囩ず鍣ㄧ殑鏍峰紡鍙婁綅缃紙淇敼view_cycle_image.xml鏍峰紡,涓嶈兘淇敼id锛?</br>
 * 3.鏀寔淇敼鏂囧瓧鎻愭牱寮忓強浣嶇疆锛堜慨鏀箆iew_cycle_image.xml鏍峰紡,涓嶈兘淇敼id锛?</br>
 * 4.鏀寔璁剧疆鏄惁寮?鍚嚜鍔ㄨ疆鎾?</br>
 * 5.鏀寔杩愯涓惎鍔ㄥ拰鍋滄鑷姩杞挱</br>
 * 6.鏀寔缃戠粶鍔犺浇鍥剧墖锛岃祫婧愬浘鐗噄d锛宻d鍗″浘鐗?</br>
 * 7.璁剧疆鏀寔XUtil鐨凚itmapUtils涔熷彲鏄娇鐢╯mart-image-view鍔犺浇鍥剧墖</br>
 * 8.鏀寔鐐瑰嚮浜嬩欢</br>
 * 9.榛樿鏄涓?寮?</br></br>
 *
 * demo瀹炰緥:</br> </br>
 *
 List<ImageCycleView.ImageInfo> list=new ArrayList<ImageCycleView.ImageInfo>();
 mImageCycleView = (ImageCycleView) findViewById(R.id.icv_topView);

 //閬垮厤缃戠粶鍥剧墖鍜屾湰鍦板浘鐗囨贩鍚堜娇鐢紝鐗规畩瑕佹眰鍙湪ImageCycleView.LoadImageCallBack鍥炶皟涓垽鏂鐞?
 //鍒ゆ柇鏄暟瀛楀姞杞芥湰鍦板浘鐗囷紝鏄痟ttp璇锋眰鍔犺浇缃戠粶鍥剧墖
 List<ImageCycleView.ImageInfo> list=new ArrayList<ImageCycleView.ImageInfo>();

 //浣跨敤鏈湴鍥剧墖
 //list.add(new ImageCycleView.ImageInfo(R.drawable.a1,"111111111111",""));
 //list.add(new ImageCycleView.ImageInfo(R.drawable.a2,"222222222222222",""));
 //list.add(new ImageCycleView.ImageInfo(R.drawable.a3,"3333333333333",""));

 //SD鍗″浘鐗囪祫婧?
 list.add(new ImageCycleView.ImageInfo(new File(Environment.getExternalStorageDirectory(),"a1.jpg"),"11111",""));
 list.add(new ImageCycleView.ImageInfo(new File(Environment.getExternalStorageDirectory(),"a2.jpg"),"22222",""));
 list.add(new ImageCycleView.ImageInfo(new File(Environment.getExternalStorageDirectory(),"a3.jpg"),"33333",""));

 //浣跨敤缃戠粶鍔犺浇鍥剧墖
 list.add(new ImageCycleView.ImageInfo("http://img.lakalaec.com/ad/57ab6dc2-43f2-4087-81e2-b5ab5681642d.jpg","3333333333333",""));
 list.add(new ImageCycleView.ImageInfo("http://img.lakalaec.com/ad/cb56a1a6-6c33-41e4-9c3c-363f4ec6b728.jpg","222222222222222",""));
 list.add(new ImageCycleView.ImageInfo("http://img.lakalaec.com/ad/e4229e25-3906-4049-9fe8-e2b52a98f6d1.jpg","3333333333333",""));


 mImageCycleView.loadData(list, new ImageCycleView.LoadImageCallBack() {
@Override
public ImageView loadAndDisplay(ImageCycleView.ImageInfo imageInfo){

//鏈湴鍥剧墖
//ImageView imageView=new ImageView(MainActivity.this);
//imageView.setImageResource(Integer.parseInt(imageInfo.image.toString()));
//return imageView;

//浣跨敤SD鍗″浘鐗?
SmartImageView smartImageView=new SmartImageView(MainActivity.this);
smartImageView.setImageURI(Uri.fromFile((File)imageInfo.image));
return smartImageView;

//浣跨敤SmartImageView
//SmartImageView smartImageView=new SmartImageView(MainActivity.this);
//smartImageView.setImageResource(Integer.parseInt(imageInfo.image.toString()));
//return smartImageView;

//浣跨敤BitmapUtils
BitmapUtils bitmapUtils=new BitmapUtils(MainActivity.this);
ImageView imageView=new ImageView(MainActivity.this);
bitmapUtils.display(imageView,imageInfo.image.toString());
return imageView;
}
});
 *
 *
 * </Pre>
 * @author 浠ｅ嚡鐢?
 *
 */
public class ImageCycleView extends FrameLayout {

	/**
	 * 涓婁笅鏂?
	 */
	private Context mContext;
	/**
	 * 鍥剧墖杞挱瑙嗗浘
	 */
	private ImageCycleViewPager mViewPager;
	/**
	 * 鏁版嵁闆嗗悎
	 * Map<String,String> map=new HashMap<String, String>();
	 * map.put("","");
	 *
	 */
	private List<ImageInfo> data=new ArrayList<ImageInfo>();
	/**
	 * 鍔犺浇鍥剧墖鍥炶皟鍑芥暟
	 */
	private LoadImageCallBack mLoadImageCallBack;

	/**
	 * 鍥剧墖杞挱鎸囩ず鍣ㄥ鍣?
	 */
	private LinearLayout mIndicationGroup;
	/**
	 * 杞挱鐨勬?绘暟
	 */
	private int mCount=0;
	/**
	 * 鏈幏寰楃劍鐐规寚绀哄櫒璧勬簮
	 */
	private Bitmap unFocusIndicationStyle;
	/**
	 * 鑾峰緱鐒︾偣鎸囩ず鍣ㄨ祫婧?
	 */
	private Bitmap focusIndicationStyle;
	/**
	 * 鎸囩ず鍣ㄩ棿璺濈浉瀵逛簬鑷韩鐨勭櫨鍒嗘瘮,榛樿闂磋窛涓烘寚绀哄櫒楂樺害鐨?1/2
	 */
	private float indication_self_margin_percent=0.5f;
	/**
	 * 鍗曞嚮浜嬩欢鐩戝惉鍣?
	 */
	private OnPageClickListener mOnPageClickListener;
	/**
	 * 鍥剧墖鏂囨湰鎻愮ず
	 */
	private TextView mText;


	public ImageCycleView(Context context) {
		super(context);
		init(context);
	}

	public ImageCycleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 鍒濆鍖栧熀纭?淇℃伅
	 * @param context
	 */
	private void init(Context context){
		mContext=context;
		unFocusIndicationStyle=drawCircle(50,Color.GRAY);
		focusIndicationStyle=drawCircle(50,Color.WHITE);
		initView();
	}

	/**
	 * 鍒濆鍖杤iew鎺т欢
	 * @author 浠ｅ嚡鐢?
	 */
	private void initView() {
		View.inflate(mContext, R.layout.view_image_cycle, this);
		FrameLayout fl_image_cycle = (FrameLayout) findViewById(R.id.fl_image_cycle);
		mViewPager=new ImageCycleViewPager(mContext);
		mViewPager.setLayoutParams(new  ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
		fl_image_cycle.addView(mViewPager);
		mViewPager.setOnPageChangeListener(new ImageCyclePageChangeListener());
		mIndicationGroup = (LinearLayout) findViewById(R.id.ll_indication_group);
		mText=(TextView)findViewById(R.id.tv_text);
	}

	public enum IndicationStyle{
		COLOR,IMAGE
	}

	/**
	 * 璁剧疆杞挱鎸囩ず鍣ㄦ牱寮忥紝濡傛灉浣犲榛樿鐨勬牱寮忎笉婊℃剰鍙互鑷繁璁剧疆
	 * @param indicationStyle 璧勬簮绫诲瀷,color,image,shape
	 * @param unFocus 鏈幏寰楃劍鐐规寚绀哄櫒璧勬簮id  鍥剧墖鎴杝hape鎴朿olor鍊?
	 * @param focus 鑾峰緱鐒︾偣鎸囩ず鍣ㄨ祫婧恑d 鍥剧墖鎴杝hape鎴朿olor鍊?
	 * @param indication_self_percent 鑷韩楂樺害鐨勭櫨鍒嗘瘮 >=0f
	 */
	public void setIndicationStyle(IndicationStyle indicationStyle,int unFocus,int focus,float indication_self_percent){
		if(indicationStyle== IndicationStyle.COLOR){
			unFocusIndicationStyle=drawCircle(50,unFocus);
			focusIndicationStyle=drawCircle(50,focus);
		}else if(indicationStyle== IndicationStyle.IMAGE){
			unFocusIndicationStyle= BitmapFactory.decodeResource(mContext.getResources(),unFocus);
			focusIndicationStyle=BitmapFactory.decodeResource(mContext.getResources(), focus);
		}
		indication_self_margin_percent=indication_self_percent;
		initIndication();
	}

	/**
	 * 鍥剧墖杞挱鏄嚜鍔ㄦ粴鍔ㄧ姸鎬?  true 鑷姩婊氬姩锛宖alse 鍥剧墖涓嶈兘鑷姩婊氬姩鍙兘鎵嬪姩宸﹀彸婊戝姩
	 */
	private boolean isAutoCycle=true;
	/**
	 *鑷姩杞挱鏃堕棿闂撮殧榛樿5绉?
	 */
	private long mCycleDelayed=5000;

	/**
	 * 璁剧疆鏄惁鑷姩鏃犻檺杞挱
	 * @param delayed  鑷姩杞挱鏃堕棿闂撮殧
	 */
	public void setCycleDelayed(long delayed){
		mCycleDelayed=delayed;
	}

	/**
	 * 璁剧疆鏄惁鑷姩鏃犻檺杞挱
	 * @param state
	 */
	public void setAutoCycle(Boolean state){
		isAutoCycle=state;
	}

	/**
	 * 鍔犺浇鏄剧ず鐨勬暟鎹?  缃戠粶鍥剧墖璧勬簮鍙婃爣棰?
	 * @param list       鏁版嵁
	 * @param callBack   濡備綍鍔犺浇鍥剧墖鍙婃樉绀虹殑鍥炶皟鏂规硶 not null
	 */
	public void loadData(List<ImageInfo> list,LoadImageCallBack callBack){
		data=list;
		mCount=list.size();
		initIndication();
		if(callBack==null){
			new IllegalArgumentException("LoadImageCallBack 鍥炶皟鍑芥暟涓嶈兘涓虹┖锛?");
		}
		mLoadImageCallBack=callBack;
		mViewPager.setAdapter(new ImageCycleAdapter());
		//鏈?澶у?间腑闂? 鐨勭涓?涓?
		mViewPager.setCurrentItem(Integer.MAX_VALUE/2-((Integer.MAX_VALUE/2)%mCount));
	}

	/**
	 * 璁剧疆鐐瑰嚮浜嬩欢鐩戝惉鍥炶皟鍑芥暟
	 * @param listener
	 */
	public void setOnPageClickListener(OnPageClickListener listener){
		mOnPageClickListener=listener;
	}

	/**
	 * 杞挱鎺т欢鐨勭洃鍚簨浠?
	 */
	public interface OnPageClickListener {
		/**
		 * 鍗曞嚮鍥剧墖浜嬩欢
		 * @param imageView 琚偣鍑荤殑View瀵硅薄
		 * @param imageInfo 鏁版嵁淇℃伅
		 */
		void onClick(View imageView, ImageInfo imageInfo);
	}


	/**
	 * 鍒濆鍖栨寚鏍囧櫒
	 */
	private void initIndication(){
		mIndicationGroup.removeAllViews();
		for(int i=0;i<mCount;i++){
			ImageView imageView = new ImageView(mContext);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mIndicationGroup.getLayoutParams().height, LinearLayout.LayoutParams.MATCH_PARENT);
			params.leftMargin = (int)(mIndicationGroup.getLayoutParams().height*indication_self_margin_percent);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setLayoutParams(params);
			if(i==0) {
				imageView.setImageBitmap(focusIndicationStyle);
			}else{
				imageView.setImageBitmap(unFocusIndicationStyle);
			}
			mIndicationGroup.addView(imageView);
		}
	}

	private Bitmap drawCircle(int radius,int color){
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);// 璁剧疆棰滆壊
		Bitmap bitmap=Bitmap.createBitmap(radius,radius, Bitmap.Config.ARGB_8888);
		Canvas canvas=new Canvas(bitmap);
		canvas.drawCircle(radius / 2, radius / 2, radius / 2, paint);
		return bitmap;
	}


	public static class ImageInfo{
		public ImageInfo(Object image, String text, Object value) {
			this.image = image;
			this.text = text;
			this.value = value;
		}
		public Object image;
		public String text="";
		public Object value;
	}


	/**
	 * 鍔犺浇鍥剧墖骞舵樉绀哄洖璋冩帴鍙?
	 */
	public interface LoadImageCallBack{
		/**
		 * 鑷繁濡備綍璁剧疆鍔犺浇鍥剧墖
		 * @param imageInfo  鏁版嵁淇℃伅
		 */
		ImageView loadAndDisplay(ImageInfo imageInfo);
	}

	/**
	 * 杞挱鍥剧墖鐩戝惉
	 * @author 浠ｅ嚡鐢?
	 */
	private final class ImageCyclePageChangeListener implements OnPageChangeListener {

		//涓婃鎸囩ず鍣ㄦ寚绀虹殑浣嶇疆,寮?濮嬩负榛樿浣嶇疆0
		private int preIndex=0;

		@Override
		public void onPageSelected(int index) {
			index=index%mCount;
			//鏇存柊鏂囨湰淇℃伅
			String text=data.get(index).text;
			mText.setText(TextUtils.isEmpty(text)?"":text);
			//鎭㈠榛樿娌℃湁鑾峰緱鐒︾偣鎸囩ず鍣ㄦ牱寮?
			((ImageView)(mIndicationGroup.getChildAt(preIndex))).setImageBitmap(unFocusIndicationStyle);
			// 璁剧疆褰撳墠鏄剧ず鍥剧墖鐨勬寚绀哄櫒鏍峰紡
			((ImageView)(mIndicationGroup.getChildAt(index))).setImageBitmap(focusIndicationStyle);
			preIndex=index;
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onPageScrolled(int i, float v, int i1) {

		}
	}

	/**
	 * 鍥剧墖杞挱閫傞厤鍣?
	 */
	private class ImageCycleAdapter extends PagerAdapter {

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			final ImageInfo imageInfo = data.get(position % mCount);

			ImageView imageView=mLoadImageCallBack.loadAndDisplay(imageInfo);
			imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			// 璁剧疆鍥剧墖鐐瑰嚮鐩戝惉
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mOnPageClickListener!=null) {
						mOnPageClickListener.onClick(v,imageInfo);
					}
				}
			});

			container.addView(imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}
		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}
	}


	/**
	 * 寮?濮嬪浘鐗囪疆鎾?
	 */
	private void startImageCycle() {
		handler.sendEmptyMessageDelayed(0, mCycleDelayed);
	}

	/**
	 * 鏆傚仠鍥剧墖杞挱
	 */
	private void stopImageCycle() {
		handler.removeCallbacksAndMessages(null);
	}

	/**
	 * 瀹炵幇鑷姩杞挱
	 */
	private Handler handler=new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if (mViewPager != null) {
				mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
				handler.sendEmptyMessageDelayed(0,mCycleDelayed);
			}
			return false;
		}
	});

	/**
	 * 瑙︽懜鍋滄璁℃椂鍣紝鎶捣鍚姩璁℃椂鍣?
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_UP){
			if(isAutoCycle) {
				// 寮?濮嬪浘鐗囨粴鍔?
				startImageCycle();
			}
		}else{
			if(isAutoCycle) {
				// 鍋滄鍥剧墖婊氬姩
				stopImageCycle();
			}
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		// 鍋滄鍥剧墖婊氬姩
		stopImageCycle();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if(isAutoCycle) {
			startImageCycle();
		}
	}


	/**
	 * 鑷畾涔塚iewPager涓昏鐢ㄤ簬浜嬩欢澶勭悊
	 */
	public class ImageCycleViewPager  extends ViewPager {

		public ImageCycleViewPager(Context context) {
			super(context);
		}

		public ImageCycleViewPager(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		/**
		 * 浜嬩欢鎷︽埅
		 */
		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			return super.onInterceptTouchEvent(ev);
		}

		/**
		 * 浜嬩欢鍒嗗彂
		 */
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			getParent().requestDisallowInterceptTouchEvent(true);
			return super.dispatchTouchEvent(ev);
		}
		/**
		 * 浜嬩欢澶勭悊
		 */
		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			return super.onTouchEvent(ev);
		}


	}

}
