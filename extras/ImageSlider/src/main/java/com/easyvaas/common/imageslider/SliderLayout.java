package com.easyvaas.common.imageslider;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.imageslider.Animations.BaseAnimationInterface;
import com.easyvaas.common.imageslider.Indicators.PagerIndicator;
import com.easyvaas.common.imageslider.SliderTypes.BaseSliderView;
import com.easyvaas.common.imageslider.Transformers.AccordionTransformer;
import com.easyvaas.common.imageslider.Transformers.BackgroundToForegroundTransformer;
import com.easyvaas.common.imageslider.Transformers.BaseTransformer;
import com.easyvaas.common.imageslider.Transformers.CubeInTransformer;
import com.easyvaas.common.imageslider.Transformers.DefaultTransformer;
import com.easyvaas.common.imageslider.Transformers.DepthPageTransformer;
import com.easyvaas.common.imageslider.Transformers.FadeTransformer;
import com.easyvaas.common.imageslider.Transformers.FlipHorizontalTransformer;
import com.easyvaas.common.imageslider.Transformers.FlipPageViewTransformer;
import com.easyvaas.common.imageslider.Transformers.ForegroundToBackgroundTransformer;
import com.easyvaas.common.imageslider.Transformers.RotateDownTransformer;
import com.easyvaas.common.imageslider.Transformers.RotateUpTransformer;
import com.easyvaas.common.imageslider.Transformers.StackTransformer;
import com.easyvaas.common.imageslider.Transformers.TabletTransformer;
import com.easyvaas.common.imageslider.Transformers.ZoomInTransformer;
import com.easyvaas.common.imageslider.Transformers.ZoomOutSlideTransformer;
import com.easyvaas.common.imageslider.Transformers.ZoomOutTransformer;
import com.easyvaas.common.imageslider.Tricks.FixedSpeedScroller;
import com.easyvaas.common.imageslider.Tricks.InfinitePagerAdapter;
import com.easyvaas.common.imageslider.Tricks.InfiniteViewPager;
import com.easyvaas.common.imageslider.Tricks.ViewPagerEx;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

/**
 * SliderLayout is compound layout. This is combined with {@link com.easyvaas.common.imageslider.Indicators.PagerIndicator}
 * and {@link com.easyvaas.common.imageslider.Tricks.ViewPagerEx} .
 *
 * There is some properties you can set in XML:
 *
 * indicator_visibility
 *      visible
 *      invisible
 *
 * indicator_shape
 *      oval
 *      rect
 *
 * indicator_selected_color
 *
 * indicator_unselected_color
 *
 * indicator_selected_drawable
 *
 * indicator_unselected_drawable
 *
 * pager_animation
 *      Default
 *      Accordion
 *      Background2Foreground
 *      CubeIn
 *      DepthPage
 *      Fade
 *      FlipHorizontal
 *      FlipPage
 *      Foreground2Background
 *      RotateDown
 *      RotateUp
 *      Stack
 *      Tablet
 *      ZoomIn
 *      ZoomOutSlide
 *      ZoomOut
 *
 * pager_animation_span
 *
 *
 */
public class SliderLayout extends RelativeLayout{

    private Context mContext;
    /**
     * InfiniteViewPager is extended from ViewPagerEx. As the name says, it can scroll without bounder.
     */
    private InfiniteViewPager mViewPager;

    /**
     * InfiniteViewPager adapter.
     */
    private SliderAdapter mSliderAdapter;

    /**
     * {@link com.easyvaas.common.imageslider.Tricks.ViewPagerEx} indicator.
     */
    private PagerIndicator mIndicator;


    /**
     * A timer and a TimerTask using to cycle the {@link com.easyvaas.common.imageslider.Tricks.ViewPagerEx}.
     */
    private Timer mCycleTimer;
    private TimerTask mCycleTask;

    /**
     * For resuming the cycle, after user touch or click the {@link com.easyvaas.common.imageslider.Tricks.ViewPagerEx}.
     */
    private Timer mResumingTimer;
    private TimerTask mResumingTask;
    public TextView totalCountsTextView;
    public TextView currentCountTextView;

    /**
     * If {@link com.easyvaas.common.imageslider.Tricks.ViewPagerEx} is Cycling
     */
    private boolean mCycling;

    /**
     * Determine if auto recover after user touch the {@link com.easyvaas.common.imageslider.Tricks.ViewPagerEx}
     */
    private boolean mAutoRecover = true;

    private int mTransformerId;

    /**
     * {@link com.easyvaas.common.imageslider.Tricks.ViewPagerEx} transformer time span.
     */
    private int mTransformerSpan = 1100;

    private boolean mAutoCycle;

    /**
     * the duration between animation.
     */
    private long mSliderDuration = 4000;

    private RelativeLayout viewPagerParent;

    /**
     * Visibility of {@link com.easyvaas.common.imageslider.Indicators.PagerIndicator}
     */
    private PagerIndicator.IndicatorVisibility mIndicatorVisibility = PagerIndicator.IndicatorVisibility.Invisible;

    /**
     * {@link com.easyvaas.common.imageslider.Tricks.ViewPagerEx} 's transformer
     */
    private BaseTransformer mViewPagerTransformer;

    /**
     * @see com.easyvaas.common.imageslider.Animations.BaseAnimationInterface
     */
    private BaseAnimationInterface mCustomAnimation;

    /**
     * {@link com.easyvaas.common.imageslider.Indicators.PagerIndicator} shape, rect or oval.
     */

    public SliderLayout(Context context) {
        this(context,null);
    }

    public SliderLayout(Context context, AttributeSet attrs) {
        this(context,attrs, R.attr.SliderStyle);
    }

    public SliderLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.slider_layout, this, true);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs,R.styleable.SliderLayout,
                defStyle,0);

        mTransformerSpan = attributes.getInteger(R.styleable.SliderLayout_pager_animation_span, 1100);
        mTransformerId = attributes.getInt(R.styleable.SliderLayout_pager_animation, Transformer.Default.ordinal());
        mAutoCycle = attributes.getBoolean(R.styleable.SliderLayout_auto_cycle,true);
        int visibility = attributes.getInt(R.styleable.SliderLayout_indicator_visibility,0);
        for(PagerIndicator.IndicatorVisibility v: PagerIndicator.IndicatorVisibility.values()){
            if(v.ordinal() == visibility){
                mIndicatorVisibility = v;
                break;
            }
        }
        mSliderAdapter = new SliderAdapter(mContext);
        PagerAdapter wrappedAdapter = new InfinitePagerAdapter(mSliderAdapter);

        totalCountsTextView = (TextView) findViewById(R.id.page_total_textview);
        currentCountTextView = (TextView) findViewById(R.id.page_count_textview);

        mViewPager = (InfiniteViewPager)findViewById(R.id.daimajia_slider_viewpager);
        mViewPager.setAdapter(wrappedAdapter);
        mViewPager.setPadding(dp2px(25),0,dp2px(25),0);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setClipToPadding(false);
        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                     case MotionEvent.ACTION_UP:
                        recoverCycle();
                        break;
                }
                return false;
            }
        });

        attributes.recycle();
        setPresetIndicator(PresetIndicators.Right_Bottom);
        setPresetTransformer(mTransformerId);
        setSliderTransformDuration(mTransformerSpan,null);
        setIndicatorVisibility(mIndicatorVisibility);
        mViewPager.setPageTransformer(true,new ScaleTransformer());
        if(mAutoCycle){
            startAutoCycle();
        }

    }

    private class ScaleTransformer implements ViewPagerEx.PageTransformer {
        private static final float MIN_SCALE = 0.88f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(View page, float position) {
            if (position < -1 || position > 1) {
//                page.setAlpha(MIN_ALPHA);
                page.setScaleX(MIN_SCALE + 0.05f);
                page.setScaleY(MIN_SCALE);
            } else if (position <= 1) { // [-1,1]
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                if (position < 0) {
                    float scaleX = 1 + 0.12f * position;
                    float roundX = Math.round(scaleX*100)/100.0f;
                    if (roundX >= 0.88 )
                        scaleX = scaleFactor;
                    page.setScaleX(scaleX+0.05f);
                    page.setScaleY(scaleX);
                } else {

                    float scaleX = 1 - 0.12f * position;
                    page.setScaleX(scaleX+0.05f);
                    page.setScaleY(scaleX);
                }
//                page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
        }
        }
    }



    public void addOnPageChangeListener(ViewPagerEx.OnPageChangeListener onPageChangeListener){
        if(onPageChangeListener != null){
            mViewPager.addOnPageChangeListener(onPageChangeListener);
        }
    }

    public void removeOnPageChangeListener(ViewPagerEx.OnPageChangeListener onPageChangeListener) {
        mViewPager.removeOnPageChangeListener(onPageChangeListener);
    }

    public void setCustomIndicator(PagerIndicator indicator){
        if(mIndicator != null){
            mIndicator.destroySelf();
        }
        mIndicator = indicator;
        mIndicator.setIndicatorVisibility(mIndicatorVisibility);
        mIndicator.setViewPager(mViewPager);
        mIndicator.redraw();
    }

    public <T extends BaseSliderView> void addSlider(T imageContent){
        mSliderAdapter.addSlider(imageContent);
    }

    private android.os.Handler mh = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            moveNextPosition(true);
        }
    };

    public void startAutoCycle(){
        startAutoCycle(mSliderDuration, mSliderDuration, mAutoRecover);
    }

    /**
     * start auto cycle.
     * @param delay delay time
     * @param duration animation duration time.
     * @param autoRecover if recover after user touches the slider.
     */
    public void startAutoCycle(long delay,long duration,boolean autoRecover){
        if(mCycleTimer != null) mCycleTimer.cancel();
        if(mCycleTask != null) mCycleTask.cancel();
        if(mResumingTask != null) mResumingTask.cancel();
        if(mResumingTimer != null) mResumingTimer.cancel();
        mSliderDuration = duration;
        mCycleTimer = new Timer();
        mAutoRecover = autoRecover;
        mCycleTask = new TimerTask() {
            @Override
            public void run() {
                mh.sendEmptyMessage(0);
            }
        };
        mCycleTimer.schedule(mCycleTask,delay,mSliderDuration);
        mCycling = true;
        mAutoCycle = true;
    }

    /**
     * pause auto cycle.
     */
    private void pauseAutoCycle(){
        if(mCycling){
            mCycleTimer.cancel();
            mCycleTask.cancel();
            mCycling = false;
        }else{
            if(mResumingTimer != null && mResumingTask != null){
                recoverCycle();
            }
        }
    }

    /**
     * set the duration between two slider changes. the duration value must >= 500
     * @param duration
     */
    public void setDuration(long duration){
        if(duration >= 500){
            mSliderDuration = duration;
            if(mAutoCycle && mCycling){
                startAutoCycle();
            }
        }
    }

    /**
     * stop the auto circle
     */
    public void stopAutoCycle(){
        if(mCycleTask!=null){
            mCycleTask.cancel();
        }
        if(mCycleTimer!= null){
            mCycleTimer.cancel();
        }
        if(mResumingTimer!= null){
            mResumingTimer.cancel();
        }
        if(mResumingTask!=null){
            mResumingTask.cancel();
        }
        mAutoCycle = false;
        mCycling = false;
    }

    /**
     * when paused cycle, this method can weak it up.
     */
    private void recoverCycle(){
        if(!mAutoRecover || !mAutoCycle){
            return;
        }

        if(!mCycling){
            if(mResumingTask != null && mResumingTimer!= null){
                mResumingTimer.cancel();
                mResumingTask.cancel();
            }
            mResumingTimer = new Timer();
            mResumingTask = new TimerTask() {
                @Override
                public void run() {
                    startAutoCycle();
                }
            };
            mResumingTimer.schedule(mResumingTask, 100);
        }
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                pauseAutoCycle();
                break;
            case MotionEvent.ACTION_UP:
                recoverCycle();
                break;
        }
        return false;
    }

    /**
     * set ViewPager transformer.
     * @param reverseDrawingOrder
     * @param transformer
     */
    public void setPagerTransformer(boolean reverseDrawingOrder,BaseTransformer transformer){
        mViewPagerTransformer = transformer;
        mViewPagerTransformer.setCustomAnimationInterface(mCustomAnimation);
        mViewPager.setPageTransformer(reverseDrawingOrder,mViewPagerTransformer);
    }



    /**
     * set the duration between two slider changes.
     * @param period
     * @param interpolator
     */
    public void setSliderTransformDuration(int period,Interpolator interpolator){
        try{
            Field mScroller = ViewPagerEx.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(),interpolator, period);
            mScroller.set(mViewPager,scroller);
        }catch (Exception e){

        }
    }

    /**
     * preset transformers and their names
     */
    public enum Transformer{
        Default("Default"),
        Accordion("Accordion"),
        Background2Foreground("Background2Foreground"),
        CubeIn("CubeIn"),
        DepthPage("DepthPage"),
        Fade("Fade"),
        FlipHorizontal("FlipHorizontal"),
        FlipPage("FlipPage"),
        Foreground2Background("Foreground2Background"),
        RotateDown("RotateDown"),
        RotateUp("RotateUp"),
        Stack("Stack"),
        Tablet("Tablet"),
        ZoomIn("ZoomIn"),
        ZoomOutSlide("ZoomOutSlide"),
        ZoomOut("ZoomOut");

        private final String name;

        private Transformer(String s){
            name = s;
        }
        public String toString(){
            return name;
        }

        public boolean equals(String other){
            return (other == null)? false:name.equals(other);
        }
    };

    /**
     * set a preset viewpager transformer by id.
     * @param transformerId
     */
    public void setPresetTransformer(int transformerId){
        for(Transformer t : Transformer.values()){
            if(t.ordinal() == transformerId){
                setPresetTransformer(t);
                break;
            }
        }
    }


    /**
     * set preset PagerTransformer via the name of transforemer.
     * @param transformerName
     */
    public void setPresetTransformer(String transformerName){
        for(Transformer t : Transformer.values()){
            if(t.equals(transformerName)){
                setPresetTransformer(t);
                return;
            }
        }
    }

    /**
     * Inject your custom animation into PageTransformer, you can know more details in
     * {@link com.easyvaas.common.imageslider.Animations.BaseAnimationInterface},
     * and you can see a example in {@link com.easyvaas.common.imageslider.Animations.DescriptionAnimation}
     * @param animation
     */
    public void setCustomAnimation(BaseAnimationInterface animation){
        mCustomAnimation = animation;
        if(mViewPagerTransformer != null){
            mViewPagerTransformer.setCustomAnimationInterface(mCustomAnimation);
        }
    }

    /**
     * pretty much right? enjoy it. :-D
     *
     * @param ts
     */
    public void setPresetTransformer(Transformer ts){
        //
        // special thanks to https://github.com/ToxicBakery/ViewPagerTransforms
        //
        BaseTransformer t = null;
        switch (ts){
            case Default:
                t = new DefaultTransformer();
                break;
            case Accordion:
                t = new AccordionTransformer();
                break;
            case Background2Foreground:
                t = new BackgroundToForegroundTransformer();
                break;
            case CubeIn:
                t = new CubeInTransformer();
                break;
            case DepthPage:
                t = new DepthPageTransformer();
                break;
            case Fade:
                t = new FadeTransformer();
                break;
            case FlipHorizontal:
                t = new FlipHorizontalTransformer();
                break;
            case FlipPage:
                t = new FlipPageViewTransformer();
                break;
            case Foreground2Background:
                t = new ForegroundToBackgroundTransformer();
                break;
            case RotateDown:
                t = new RotateDownTransformer();
                break;
            case RotateUp:
                t = new RotateUpTransformer();
                break;
            case Stack:
                t = new StackTransformer();
                break;
            case Tablet:
                t = new TabletTransformer();
                break;
            case ZoomIn:
                t = new ZoomInTransformer();
                break;
            case ZoomOutSlide:
                t = new ZoomOutSlideTransformer();
                break;
            case ZoomOut:
                t = new ZoomOutTransformer();
                break;
        }
        setPagerTransformer(true,t);
    }



    /**
     * Set the visibility of the indicators.
     * @param visibility
     */
    public void setIndicatorVisibility(PagerIndicator.IndicatorVisibility visibility){
        if(mIndicator == null){
            return;
        }

        mIndicator.setIndicatorVisibility(visibility);
    }

    public PagerIndicator.IndicatorVisibility getIndicatorVisibility(){
        if(mIndicator == null){
            return mIndicator.getIndicatorVisibility();
        }
        return PagerIndicator.IndicatorVisibility.Invisible;

    }

    /**
     * get the {@link com.easyvaas.common.imageslider.Indicators.PagerIndicator} instance.
     * You can manipulate the properties of the indicator.
     * @return
     */
    public PagerIndicator getPagerIndicator(){
        return mIndicator;
    }

    public enum PresetIndicators{
        Center_Bottom("Center_Bottom",R.id.default_center_bottom_indicator),
        Right_Bottom("Right_Bottom",R.id.default_bottom_right_indicator),
        Left_Bottom("Left_Bottom",R.id.default_bottom_left_indicator),
        Center_Top("Center_Top",R.id.default_center_top_indicator),
        Right_Top("Right_Top",R.id.default_center_top_right_indicator),
        Left_Top("Left_Top",R.id.default_center_top_left_indicator);

        private final String name;
        private final int id;
        private PresetIndicators(String name,int id){
            this.name = name;
            this.id = id;
        }

        public String toString(){
            return name;
        }

        public int getResourceId(){
            return id;
        }
    }
    public void setPresetIndicator(PresetIndicators presetIndicator){
        PagerIndicator pagerIndicator = (PagerIndicator)findViewById(presetIndicator.getResourceId());
        setCustomIndicator(pagerIndicator);
    }

    private InfinitePagerAdapter getWrapperAdapter(){
        PagerAdapter adapter = mViewPager.getAdapter();
        if(adapter!=null){
            return (InfinitePagerAdapter)adapter;
        }else{
            return null;
        }
    }

    private SliderAdapter getRealAdapter(){
        PagerAdapter adapter = mViewPager.getAdapter();
        if(adapter!=null){
            return ((InfinitePagerAdapter)adapter).getRealAdapter();
        }
        return null;
    }

    /**
     * get the current item position
     * @return
     */
    public int getCurrentPosition(){

        if(getRealAdapter() == null)
            throw new IllegalStateException("You did not set a slider adapter");

        return mViewPager.getCurrentItem() % getRealAdapter().getCount();

    }

    /**
     * get current slider.
     * @return
     */
    public BaseSliderView getCurrentSlider(){

        if(getRealAdapter() == null)
            throw new IllegalStateException("You did not set a slider adapter");

        int count = getRealAdapter().getCount();
        int realCount = mViewPager.getCurrentItem() % count;
        return  getRealAdapter().getSliderView(realCount);
    }

    /**
     * remove  the slider at the position. Notice: It's a not perfect method, a very small bug still exists.
     */
    public void removeSliderAt(int position){
        if(getRealAdapter()!=null){
            getRealAdapter().removeSliderAt(position);
            mViewPager.setCurrentItem(mViewPager.getCurrentItem(),false);
        }
    }

    /**
     * remove all the sliders. Notice: It's a not perfect method, a very small bug still exists.
     */
    public void removeAllSliders(){
        if(getRealAdapter()!=null){
            int count = getRealAdapter().getCount();
            getRealAdapter().removeAllSliders();
            //a small bug, but fixed by this trick.
            //bug: when remove adapter's all the sliders.some caching slider still alive.
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() +  count,false);
        }
    }

    /**
     *set current slider
     * @param position
     */
    public void setCurrentPosition(int position, boolean smooth) {
        if (getRealAdapter() == null)
            throw new IllegalStateException("You did not set a slider adapter");
        if(position >= getRealAdapter().getCount()){
            throw new IllegalStateException("Item position is not exist");
        }
        int p = mViewPager.getCurrentItem() % getRealAdapter().getCount();
        int n = (position - p) + mViewPager.getCurrentItem();
        mViewPager.setCurrentItem(n, smooth);
    }

    public void setCurrentPosition(int position) {
        setCurrentPosition(position, true);
    }

    /**
     * move to prev slide.
     */
    public void movePrevPosition(boolean smooth) {

        if (getRealAdapter() == null)
            throw new IllegalStateException("You did not set a slider adapter");

        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, smooth);
    }

    public void movePrevPosition(){
        movePrevPosition(true);
    }

    /**
     * move to next slide.
     */
    public void moveNextPosition(boolean smooth) {

        if (getRealAdapter() == null)
            throw new IllegalStateException("You did not set a slider adapter");

        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, smooth);
    }

    public void moveNextPosition() {
        moveNextPosition(true);
    }

    /**
     * dp 2 px
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }
}
