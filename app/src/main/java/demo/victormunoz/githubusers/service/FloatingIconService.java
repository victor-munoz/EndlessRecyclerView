package demo.victormunoz.githubusers.service;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import demo.victormunoz.githubusers.R;
import demo.victormunoz.githubusers.ui.users.UsersActivity;

public class FloatingIconService extends Service implements View.OnClickListener, View.OnTouchListener {

    private WindowManager windowManager;
    private ImageView iconImage;
    private ImageView trashImage;
    private boolean isMoveAction = false;
    public static Boolean serviceRunning = false;
    private int iconRadio;
    private int screenWidth;
    private int screenHeight;
    private WindowManager.LayoutParams iconsParams;
    private int offsetY;
    private int statusBarHeight;
    private WindowManager.LayoutParams trashParams;
    private int iconWidth;
    private boolean isIconOnTrash;
    private AnimatorSet iconToTrashAnimation;
    private int offSetTrashX;
    private int offSetTrashY;

    //L I F E C Y C L E

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = new Notification();
        startForeground(42, notification);
        serviceRunning = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        statusBarHeight = getStatusBarHeight();
        screenHeight = displaymetrics.heightPixels - statusBarHeight;
        screenWidth = displaymetrics.widthPixels;
        iconWidth = getResources().getDimensionPixelOffset(R.dimen.little_circle_image);
        iconRadio = iconWidth / 2;

        setIconImage();
        setTrashImage();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iconImage != null) {
            windowManager.removeView(iconImage);
            iconImage = null;
        }
        if (trashImage != null) {
            windowManager.removeView(trashImage);
            trashImage = null;
        }
        serviceRunning = false;


    }

    //U S E R  A C T I O N S

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //by default the action will be a click
                isMoveAction = false;
                return false;
            case MotionEvent.ACTION_MOVE:
                if (!isMoveAction) {
                    trashEnterAnimation();
                    iconAboveYourFingerAnimation();
                }
                isMoveAction = true;
                if (isOnTrash(event.getRawX(), event.getRawY())) {
                    if (!isIconOnTrash) iconToTrashAnimation();
                } else {
                    if (isIconOnTrash) iconOutOfTrashAnimation(event.getRawX(),event.getRawY());
                    iconsParams.x = fixPositionX(event.getRawX());
                    iconsParams.y = fixPositionY(event.getRawY());
                }
                windowManager.updateViewLayout(iconImage, iconsParams);
                return true;
            case MotionEvent.ACTION_UP:
                //we are performing a moving action
                if (isMoveAction) {
                    if (isOnTrash(event.getRawX(), event.getRawY())) {
                        //we want to close the floating icon
                        trashExitAnimation(true);
                    } else {//no action was performed
                        trashExitAnimation(false);
                        iconToBorderX();
                    }
                    return true;
                }
                //we are performed a Click action
                return false;
        }

        return false;
    }

    //S E T

    private void setTrashImage() {
        trashImage = new ImageView(this);
        trashImage.setVisibility(View.GONE);
        trashImage.setImageResource(R.drawable.icon_close);
        trashImage.setBackground(getResources().getDrawable(R.drawable.close_background, null));
        trashImage.setScaleType(ImageView.ScaleType.CENTER);
        trashParams = new WindowManager.LayoutParams(
                getResources().getDimensionPixelOffset(R.dimen.little_circle_image),
                getResources().getDimensionPixelOffset(R.dimen.little_circle_image),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        trashParams.gravity = Gravity.TOP | Gravity.START;
        trashParams.x = screenWidth / 2 - iconRadio;
        windowManager.addView(trashImage, trashParams);
    }

    private void setIconImage() {
        iconImage = new ImageView(this);
        iconImage.setClickable(true);
        iconImage.setOnClickListener(this);
        iconImage.setImageResource(R.mipmap.ic_launcher);
        iconsParams = new WindowManager.LayoutParams(
                getResources().getDimensionPixelOffset(R.dimen.little_circle_image),
                getResources().getDimensionPixelOffset(R.dimen.little_circle_image),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        iconsParams.gravity = Gravity.TOP | Gravity.START;
        iconsParams.x = screenWidth / 2 - iconRadio;
        iconsParams.y = screenHeight / 4;
        iconImage.setOnTouchListener(this);
        windowManager.addView(iconImage, iconsParams);
        iconToBorderX();
    }

    //A N I M A T I O N S
    private void iconOutOfTrashAnimation(float x, float y){
        ValueAnimator xAnim = new ValueAnimator();
        xAnim.setIntValues(fixPositionX(x)-iconsParams.x,0);
        xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                offSetTrashX = (int) valueAnimator.getAnimatedValue();
            }
        });
        ValueAnimator yAnim = new ValueAnimator();
        yAnim.setIntValues(fixPositionY(y)-iconsParams.y,0);
        yAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                offSetTrashY = (int) valueAnimator.getAnimatedValue();
            }
        });
        AnimatorSet iconOutOfTrashAnimation = new AnimatorSet();
        iconOutOfTrashAnimation.playTogether(xAnim,yAnim);
        iconOutOfTrashAnimation.setDuration(100);
        iconOutOfTrashAnimation.setInterpolator(new FastOutSlowInInterpolator());
        iconOutOfTrashAnimation.start();

        iconToTrashAnimation.cancel();
        isIconOnTrash =false;
    }

    private void iconToTrashAnimation(){
        ValueAnimator xAnim = new ValueAnimator();
        xAnim.setIntValues(iconsParams.x,trashParams.x);
        xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                iconsParams.x = (int) valueAnimator.getAnimatedValue();
                windowManager.updateViewLayout(iconImage, iconsParams);
            }
        });

        ValueAnimator yAnim = new ValueAnimator();
        yAnim.setIntValues(iconsParams.y,trashParams.y);
        yAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                iconsParams.y = (int) valueAnimator.getAnimatedValue();
                windowManager.updateViewLayout(iconImage, iconsParams);
            }
        });

        iconToTrashAnimation =new AnimatorSet();
        iconToTrashAnimation.playTogether(xAnim,yAnim);
        iconToTrashAnimation.setInterpolator(new FastOutSlowInInterpolator());
        iconToTrashAnimation.setDuration(100);
        iconToTrashAnimation.start();
        isIconOnTrash =true;
    }

    private void trashEnterAnimation() {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(screenHeight, screenHeight - iconRadio * 4);
        anim.setDuration(400);
        anim.setInterpolator(new FastOutSlowInInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                trashParams.y = (int) valueAnimator.getAnimatedValue();
                windowManager.updateViewLayout(trashImage, trashParams);
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                trashImage.setVisibility(View.VISIBLE);
            }
        });
        anim.start();
    }

    private void trashExitAnimation(final boolean withIcon) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(trashParams.y, screenHeight);
        anim.setDuration(400);
        anim.setInterpolator(new LinearOutSlowInInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                trashParams.y = (int) valueAnimator.getAnimatedValue();
                windowManager.updateViewLayout(trashImage, trashParams);
                if (withIcon) {
                    iconsParams.y = (int) valueAnimator.getAnimatedValue();
                    windowManager.updateViewLayout(iconImage, iconsParams);
                }
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationStart(animation);
                trashImage.setVisibility(View.GONE);
            }
        });
        if (withIcon) {
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationStart(animation);
                    onDestroy();
                }
            });
        }
        anim.start();

    }

    private void iconToBorderX(){

        ValueAnimator anim = new ValueAnimator();
        anim.setInterpolator(new LinearOutSlowInInterpolator());
        if(isOnLeftSideOfScreen()) {
            anim.setIntValues(iconsParams.x, -iconWidth / 3);
        }
        else{
            anim.setIntValues(iconsParams.x, screenWidth - iconWidth * 2 / 3);
        }
        anim.setDuration(400);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                iconsParams.x = (int) valueAnimator.getAnimatedValue();
                windowManager.updateViewLayout(iconImage, iconsParams);
            }
        });

        anim.start();
    }

    private void iconAboveYourFingerAnimation() {
        ValueAnimator xAnim = ValueAnimator.ofInt(iconRadio, iconWidth + iconRadio);
        xAnim.setDuration(600);
        xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                offsetY = (int) valueAnimator.getAnimatedValue();
            }
        });
        xAnim.start();
    }

    //U T I L S

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //P O S I T I O N

    private boolean isOnTrash(float x, float y) {
        int distanceToTrash = iconRadio * 3;
        Point icon = new Point((int) (x - iconRadio), (int) (y-statusBarHeight-offsetY-iconRadio));
        Point trash = new Point(trashParams.x, trashParams.y);
        double dist = Math.sqrt(Math.pow(icon.x - trash.x, 2) + Math.pow(icon.y - trash.y, 2));
        return dist <= distanceToTrash;
    }

    private boolean isOnLeftSideOfScreen() {
        return iconsParams.x <= (screenWidth / 2) - (iconRadio / 2);
    }

    private int fixPositionX(float x) {
        if (x > screenWidth - iconRadio) {
            return screenWidth - iconWidth-offSetTrashX;
        } else if (x < iconRadio) {
            return 0-offSetTrashX;
        } else {
            return (int) x-iconRadio-offSetTrashX;
        }
    }

    private int fixPositionY(float y) {
        if(y-statusBarHeight-offsetY<iconRadio){
            return 0;
        }
        else{
            return (int) (y-statusBarHeight-iconRadio-offsetY-offSetTrashY);
        }

    }


}