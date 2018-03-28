package demo.victormunoz.githubusers.services;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import demo.victormunoz.githubusers.App;
import demo.victormunoz.githubusers.R;
import demo.victormunoz.githubusers.features.allusers.AllUsersActivity;

public class FloatingIconService extends Service implements View.OnClickListener, View.OnTouchListener {

    private final int LAYOUT_FLAG = getLayoutFlag();
    private final int ICON_WIDTH = App.mResources.getDimensionPixelOffset(R.dimen.image_circle_normal);
    private final int TRASH_WIDTH = App.mResources.getDimensionPixelOffset(R.dimen.icon_remove);
    private final int TRASH_MARGIN_BOTTOM = App.mResources.getDimensionPixelOffset(R.dimen.icon_remove_margin_bottom);
    private final int MAX_FINGER_DISTANCE = ICON_WIDTH;
    private final int STATUS_BAR_HEIGHT = getStatusBarHeight();

    private WindowManager windowManager;
    //icons
    private ImageView ivIcon;
    private ImageView ivTrash;
    private ConstraintLayout rlTrash;
    private WindowManager.LayoutParams iconParams;
    private WindowManager.LayoutParams trashIconParams;
    //motion
    private int lastAction;
    private int touchX;
    private int touchY;
    private boolean isIconOnTrash;
    //animations
    private AnimatorSet iconToTrashAnimation;
    private AnimatorSet iconOutOfTrashAnimation;
    //factor to trash following icon animation
    private float trashEnterAnimPercent;
    //delta to above finger position on touch first move animation
    private int fingerDeltaY;
    private int fingerDeltaX;
    //delta to icon in out of trash animation
    private int iconDeltaX;
    private int iconDeltaY;
    private Rect iconsBound;


    //L I F E C Y C L E

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(){
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        setIconsBound();
        setLauncherIcon();
        setCloseIcon();
    }

    @Override
    public void onDestroy(){
        if (ivIcon != null) {
            windowManager.removeView(ivIcon);
            ivIcon.setOnTouchListener(null);
            ivIcon.setOnClickListener(null);
            ivIcon = null;
        }
        if (rlTrash != null) {
            windowManager.removeView(rlTrash);
            rlTrash = null;
        }
        super.onDestroy();
    }

    //U S E R  A C T I O N S

    @Override
    public void onClick(View view){
        Intent intent = new Intent(getApplicationContext(), AllUsersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stopSelf();
    }

    @Override
    public boolean onTouch(@NonNull View view, @NonNull MotionEvent event){
        touchX = (int) event.getRawX();
        touchY = (int) event.getRawY() - STATUS_BAR_HEIGHT;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                lastAction = MotionEvent.ACTION_DOWN;
                return false;

            case MotionEvent.ACTION_MOVE:

                if (lastAction == MotionEvent.ACTION_DOWN) {
                    trashEnterAnimation();
                    iconAboveYourFingerAnimation();
                }
                lastAction = MotionEvent.ACTION_MOVE;

                if (isIconInsideTrashArea()) {

                    if (iconOutOfTrashAnimation != null && iconOutOfTrashAnimation.isRunning()) {
                        iconOutOfTrashAnimation.cancel();
                    }
                    if (isIconOnTrash) {
                        updateIcon(trashIconParams.x, trashIconParams.y);
                        updateTrashIcon(getTrashX(), getTrashY());
                    } else if (iconToTrashAnimation == null || !iconToTrashAnimation.isRunning()) {
                        iconInToTrashAnimation();
                    }

                } else {

                    if (iconToTrashAnimation != null && iconToTrashAnimation.isRunning()) {
                        iconToTrashAnimation.cancel();
                    }
                    if (!isIconOnTrash) {
                        updateIcon(getIconX(), getIconY());
                        updateTrashIcon(getTrashX(), getTrashY());
                    } else if (iconOutOfTrashAnimation == null || !iconOutOfTrashAnimation.isRunning()) {
                        iconOutOfTrashAnimation();
                    }

                }
                return true;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_MOVE) {
                    if (!isIconInsideTrashArea()) {
                        iconToBorderX();
                    }
                    trashExitAnimation();
                    return true;
                }
                return false;

        }
        view.performClick();
        return true;
    }

    //S E T

    private void setIconsBound(){
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int screenHeight = displaymetrics.heightPixels - ICON_WIDTH - getStatusBarHeight();
        int screenWidth = displaymetrics.widthPixels - ICON_WIDTH;
        iconsBound = new Rect(0, 0, screenWidth, screenHeight);
    }

    @SuppressLint("InflateParams")
    private void setCloseIcon(){
        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        if (li != null) {
            rlTrash = (ConstraintLayout) li.inflate(R.layout.service_icon_remove, null, false);
            ivTrash = rlTrash.findViewById(R.id.trash_icon);
            trashIconParams = new WindowManager.LayoutParams(ICON_WIDTH, ICON_WIDTH, LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT);
            trashIconParams.gravity = Gravity.TOP | Gravity.START;
            trashIconParams.x = iconsBound.centerX();
            trashIconParams.y = iconsBound.bottom - TRASH_MARGIN_BOTTOM;
            windowManager.addView(rlTrash, trashIconParams);
        } else {
            stopSelf();
//            onDestroy();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setLauncherIcon(){
        ivIcon = new ImageView(getApplicationContext());
        ivIcon.setImageResource(R.mipmap.ic_launcher);
        ivIcon.setClickable(true);
        ivIcon.setOnClickListener(this);
        ivIcon.setOnTouchListener(this);

        iconParams = new WindowManager.LayoutParams(ICON_WIDTH, ICON_WIDTH, LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT);
        iconParams.gravity = Gravity.TOP | Gravity.START;
        iconParams.x = iconsBound.centerX();
        iconParams.y = iconsBound.bottom / 4;
        ivIcon.setLayoutParams(iconParams);
        windowManager.addView(ivIcon, ivIcon.getLayoutParams());
        iconToBorderX();
    }

    //A N I M A T I O N S

    private void iconOutOfTrashAnimation(){
        ValueAnimator widthAnim = new ValueAnimator();
        widthAnim.setIntValues(ivTrash.getWidth(), TRASH_WIDTH);
        widthAnim.addUpdateListener(valueAnimator -> {
            final int value = (int) valueAnimator.getAnimatedValue();
            ivTrash.getLayoutParams().width = value;
            ivTrash.getLayoutParams().height = value;
            ivTrash.requestLayout();
        });

        ValueAnimator xAnim = new ValueAnimator();
        xAnim.setIntValues(getIconX() - iconParams.x, 0);
        xAnim.addUpdateListener(valueAnimator -> {
            iconDeltaX = (int) valueAnimator.getAnimatedValue();
            iconParams.x = getIconX();
            trashIconParams.x = getTrashX();
            windowManager.updateViewLayout(ivIcon, iconParams);
            windowManager.updateViewLayout(rlTrash, trashIconParams);
        }

        );
        ValueAnimator yAnim = new ValueAnimator();
        yAnim.setIntValues(getIconY() - iconParams.y, 0);
        yAnim.addUpdateListener(valueAnimator -> {
            iconDeltaY = (int) valueAnimator.getAnimatedValue();
            iconParams.y = getIconY();
            trashIconParams.y = getTrashY();
            windowManager.updateViewLayout(ivIcon, iconParams);
            windowManager.updateViewLayout(rlTrash, trashIconParams);

        });
        iconOutOfTrashAnimation = new AnimatorSet();
        iconOutOfTrashAnimation.playTogether(xAnim, yAnim, widthAnim);
        iconOutOfTrashAnimation.setDuration(400);
        iconOutOfTrashAnimation.setInterpolator(new DecelerateInterpolator(4f));
        iconOutOfTrashAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation){
                super.onAnimationCancel(animation);
                isIconOnTrash = false;
            }

            @Override
            public void onAnimationEnd(Animator animation){
                super.onAnimationEnd(animation);
                isIconOnTrash = false;


            }

            @SuppressWarnings("EmptyMethod")
            @Override
            public void onAnimationStart(Animator animation){
                super.onAnimationStart(animation);
            }
        });
        iconOutOfTrashAnimation.start();

    }

    private void iconInToTrashAnimation(){
        ValueAnimator widthAnim = new ValueAnimator();
        widthAnim.setIntValues(ivTrash.getWidth(), ICON_WIDTH);
        widthAnim.addUpdateListener(valueAnimator -> {
            final int value = (int) valueAnimator.getAnimatedValue();
            ivTrash.getLayoutParams().width = value;
            ivTrash.getLayoutParams().height = value;
            ivTrash.requestLayout();
        });
        ValueAnimator xAnim = new ValueAnimator();
        xAnim.setIntValues(trashIconParams.x - iconParams.x, 0);
        xAnim.addUpdateListener(valueAnimator -> {
            iconParams.x = trashIconParams.x - (int) valueAnimator.getAnimatedValue();
            windowManager.updateViewLayout(ivIcon, iconParams);
            trashIconParams.x = getTrashX();
            //trashIconParams.y = getTrashY();
            windowManager.updateViewLayout(rlTrash, trashIconParams);
        });
        ValueAnimator yAnim = new ValueAnimator();
        yAnim.setIntValues(trashIconParams.y - iconParams.y, 0);
        yAnim.addUpdateListener(valueAnimator -> {
            iconParams.y = trashIconParams.y - (int) valueAnimator.getAnimatedValue();
            windowManager.updateViewLayout(ivIcon, iconParams);
            //  trashIconParams.x = getTrashX();
            trashIconParams.y = getTrashY();
            windowManager.updateViewLayout(rlTrash, trashIconParams);
        });
        iconToTrashAnimation = new AnimatorSet();
        iconToTrashAnimation.playTogether(xAnim, yAnim, widthAnim);
        iconToTrashAnimation.setInterpolator(new DecelerateInterpolator(4f));
        iconToTrashAnimation.setDuration(400);
        iconToTrashAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation){
                super.onAnimationCancel(animation);
                isIconOnTrash = true;
            }

            @Override
            public void onAnimationEnd(Animator animation){
                super.onAnimationEnd(animation);
                isIconOnTrash = true;
            }
            @SuppressWarnings("EmptyMethod")
            @Override
            public void onAnimationStart(Animator animation){
                super.onAnimationStart(animation);
            }
        });
        iconToTrashAnimation.start();
    }

    private void trashEnterAnimation(){
        ValueAnimator animY = new ValueAnimator();
        animY.setFloatValues(0, 1);
        animY.addUpdateListener(valueAnimator -> {
            trashEnterAnimPercent = (float) valueAnimator.getAnimatedValue();
            trashIconParams.x = getTrashX();
            trashIconParams.y = getTrashY();
            windowManager.updateViewLayout(rlTrash, trashIconParams);
        });
        animY.setDuration(400);
        animY.setInterpolator(new FastOutSlowInInterpolator());
        animY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation){
                super.onAnimationStart(animation);
                rlTrash.setVisibility(View.VISIBLE);
            }
        });
        animY.start();

    }

    private void trashExitAnimation(){
        ValueAnimator factorAnim = new ValueAnimator();
        factorAnim.setFloatValues(1, 0);
        factorAnim.setDuration(400);
        factorAnim.setInterpolator(new LinearOutSlowInInterpolator());
        factorAnim.addUpdateListener(valueAnimator -> {
            trashEnterAnimPercent = (float) valueAnimator.getAnimatedValue();
            if (iconParams.x == trashIconParams.x && iconParams.y == trashIconParams.y) {
                iconParams.y = getTrashY();
                iconParams.x = getTrashX();
                windowManager.updateViewLayout(ivIcon, iconParams);
            }
            trashIconParams.y = getTrashY();
            trashIconParams.x = getTrashX();
            windowManager.updateViewLayout(rlTrash, trashIconParams);


        });
        factorAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation){
                super.onAnimationEnd(animation);
                rlTrash.setVisibility(View.GONE);
                if (iconParams.x == trashIconParams.x && iconParams.y == trashIconParams.y) {
                    //onDestroy();
                    stopSelf();
                }
            }
        });
        factorAnim.start();

    }

    private void iconToBorderX(){
        ValueAnimator anim = new ValueAnimator();
        anim.setInterpolator(new LinearOutSlowInInterpolator());
        if (iconParams.x < iconsBound.centerX()) {
            anim.setIntValues(iconParams.x, iconsBound.left - ICON_WIDTH / 3);
        } else {
            anim.setIntValues(iconParams.x, iconsBound.right + ICON_WIDTH / 3);
        }
        anim.setDuration(400);
        anim.addUpdateListener(valueAnimator -> {
            iconParams.x = (int) valueAnimator.getAnimatedValue();
            windowManager.updateViewLayout(ivIcon, iconParams);
        });
        anim.start();
    }

    private void iconAboveYourFingerAnimation(){
        ValueAnimator yAnim = ValueAnimator.ofInt(touchY - iconParams.y - ICON_WIDTH / 2, MAX_FINGER_DISTANCE);
        yAnim.addUpdateListener(valueAnimator -> {
            fingerDeltaY = (int) valueAnimator.getAnimatedValue();
            iconParams.y = getIconY();
            windowManager.updateViewLayout(ivIcon, iconParams);
        });
        ValueAnimator xAnim = ValueAnimator.ofInt(ICON_WIDTH / 3, 0);
        xAnim.addUpdateListener(valueAnimator -> {
            if (iconParams.x < iconsBound.centerX()) {
                fingerDeltaX = (int) valueAnimator.getAnimatedValue();
            } else {
                fingerDeltaX = -(int) valueAnimator.getAnimatedValue();
            }
            iconParams.x = getIconX();
            windowManager.updateViewLayout(ivIcon, iconParams);
        });
        AnimatorSet set = new AnimatorSet();
        set.playTogether(xAnim, yAnim);
        set.setDuration(400);
        set.start();
    }

    //U T I L S

    private int getStatusBarHeight(){
        int result = 0;
        int resourceId = App.mResources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = App.mResources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    private int getLayoutFlag(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
    }

    //P O S I T I O N

    /**
     * we want to know if the icon is close to the trash
     *
     * @return true if icon is close to the trash
     */
    private boolean isIconInsideTrashArea(){
        int iconX = touchX - ICON_WIDTH / 2 - fingerDeltaX;
        int iconY = touchY - ICON_WIDTH / 2 - fingerDeltaY;
        double dist = Math.sqrt(Math.pow(iconX - trashIconParams.x, 2) + Math.pow(iconY - trashIconParams.y, 2));
        int distanceToTrash = trashIconParams.width * 3 / 2;
        return dist <= distanceToTrash;
    }

    private int getIconX(){
        int iconX = touchX - ICON_WIDTH / 2;
        int iconFixedX = Math.max(Math.min(iconX, iconsBound.right), iconsBound.left);
        return iconFixedX - iconDeltaX - fingerDeltaX;
    }

    private int getIconY(){
        int iconY = touchY - ICON_WIDTH / 2 - fingerDeltaY;
        int iconFixedY = Math.min(Math.max(iconY, iconsBound.top), iconsBound.bottom);
        return iconFixedY - iconDeltaY;
    }

    private int getTrashX(){
        int trashX = iconsBound.centerX();
        int trashToIconDelta = (int) ((getIconX() - trashX) * 0.1f);
        return trashX + (int) (trashToIconDelta * trashEnterAnimPercent);
    }

    private int getTrashY(){
        int trashY = iconsBound.bottom + ICON_WIDTH - (int) (TRASH_MARGIN_BOTTOM * trashEnterAnimPercent);
        int fixedTouchY = Math.max(touchY, iconsBound.centerY());
        int iconY = fixedTouchY - STATUS_BAR_HEIGHT - fingerDeltaY - ICON_WIDTH / 2 - iconDeltaY;
        int trashToIconDelta = (int) ((iconY - trashY) * 0.1f);
        return trashY + (int) (trashToIconDelta * trashEnterAnimPercent);
    }

    private void updateIcon(int x, int y){
        iconParams.x = x;
        iconParams.y = y;
        windowManager.updateViewLayout(ivIcon, iconParams);
    }

    private void updateTrashIcon(int x, int y){
        trashIconParams.x = x;
        trashIconParams.y = y;
        windowManager.updateViewLayout(rlTrash, trashIconParams);
    }


}