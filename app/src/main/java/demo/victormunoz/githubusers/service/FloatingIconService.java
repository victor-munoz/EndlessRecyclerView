package demo.victormunoz.githubusers.service;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import demo.victormunoz.githubusers.R;
import demo.victormunoz.githubusers.ui.App;
import demo.victormunoz.githubusers.ui.users.UsersActivity;

public class FloatingIconService extends Service implements View.OnClickListener, View.OnTouchListener {
    private final int ICON_WIDTH = App.mResources.getDimensionPixelOffset(R.dimen.little_circle_image);
    private final int TRASH_WIDTH = App.mResources.getDimensionPixelOffset(R.dimen.icon_trash);
    private final int TRASH_MARGIN_BOTTOM = ICON_WIDTH * 3/2;
    private final int STATUS_BAR_HEIGHT = getStatusBarHeight();

    private WindowManager windowManager;
    //images
    private ImageView ivIcon;
    private ImageView ivTrash;
    private RelativeLayout rlTrash;
    private WindowManager.LayoutParams iconParams;
    private WindowManager.LayoutParams trashParams;
    //sizes
    private int screenWidth;
    private int screenHeight;
    private int fingerDistanceY;
    private int offSetTrashX;
    private int offSetTrashY;
    private int touchX;
    private int touchY;

    private boolean isMoveAction = false;
    private boolean isIconOnTrash;
    //animations
    private AnimatorSet iconToTrashAnimation;
    private AnimatorSet iconOutOfTrashAnimation;


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
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels - STATUS_BAR_HEIGHT;
        screenWidth = displaymetrics.widthPixels;
        setIconImage();
        setTrashImage();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ivIcon != null) {
            windowManager.removeView(ivIcon);
            ivIcon = null;
        }
        if (rlTrash != null) {
            windowManager.removeView(rlTrash);
            rlTrash = null;
        }
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
        touchX = (int) event.getRawX();
        touchY = (int) event.getRawY();
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
                if (isOnTrash()) {
                    if (!isIconOnTrash && (iconToTrashAnimation == null || !iconToTrashAnimation.isRunning()))
                        iconInToTrashAnimation();
                    else {
                        trashParams.x = getTrashX();
                        trashParams.y = getTrashY();
                        windowManager.updateViewLayout(rlTrash, trashParams);
                        iconParams.x = trashParams.x;
                        iconParams.y = trashParams.y;
                        windowManager.updateViewLayout(ivIcon, iconParams);
                    }
                } else {
                    if (isIconOnTrash && (iconOutOfTrashAnimation == null || !iconOutOfTrashAnimation.isRunning())) {
                        iconAboveYourFingerAnimation();
                        iconOutOfTrashAnimation();
                    } else {
                        iconParams.x = getIconX();
                        iconParams.y = getIconY();
                        windowManager.updateViewLayout(ivIcon, iconParams);
                        trashParams.x = getTrashX();
                        trashParams.y = getTrashY();
                        windowManager.updateViewLayout(rlTrash, trashParams);
                    }
                }

                return true;
            case MotionEvent.ACTION_UP:
                //we are performing a moving action
                if (isMoveAction) {
                    if (isOnTrash()) {
                        //we want to close the floating icon
                        trashExitAnimation();
                    } else {//no action was performed
                        trashExitAnimation();
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

    @SuppressLint("InflateParams")
    private void setTrashImage() {
        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        rlTrash = (RelativeLayout) li.inflate(R.layout.service_trash_icon, null);
        rlTrash.setVisibility(View.GONE);
        ivTrash = (ImageView) rlTrash.findViewById(R.id.trash_icon);
        trashParams = new WindowManager.LayoutParams(
                ICON_WIDTH,
                ICON_WIDTH,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        trashParams.gravity = Gravity.TOP | Gravity.START;
        trashParams.x = screenWidth / 2 - ICON_WIDTH / 2;
        trashParams.y = screenHeight - TRASH_MARGIN_BOTTOM;
        windowManager.addView(rlTrash, trashParams);
    }

    private void setIconImage() {
        ivIcon = new ImageView(this);
        ivIcon.setClickable(true);
        ivIcon.setOnClickListener(this);
        ivIcon.setImageResource(R.mipmap.ic_launcher);
        iconParams = new WindowManager.LayoutParams(
                ICON_WIDTH,
                ICON_WIDTH,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        iconParams.gravity = Gravity.TOP | Gravity.START;
        iconParams.x = screenWidth / 2 - ICON_WIDTH / 2;
        iconParams.y = screenHeight / 4;
        ivIcon.setOnTouchListener(this);
        windowManager.addView(ivIcon, iconParams);
        iconToBorderX();
    }

    //A N I M A T I O N S

    private void iconOutOfTrashAnimation() {
        ValueAnimator widthAnim = new ValueAnimator();
        widthAnim.setIntValues(ivTrash.getWidth(), TRASH_WIDTH);
        widthAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                            final int value = (int) valueAnimator.getAnimatedValue();
                                            ivTrash.getLayoutParams().width = value;
                                            ivTrash.getLayoutParams().height = value;
                                            ivTrash.requestLayout();
                                        }
                                    }
        );

        ValueAnimator xAnim = new ValueAnimator();
        xAnim.setIntValues(getIconX() - iconParams.x, 0);
        xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        offSetTrashX = (int) valueAnimator.getAnimatedValue();
                                        iconParams.x = getIconX();
                                        windowManager.updateViewLayout(ivIcon, iconParams);
                                        trashParams.x = getTrashX();
                                        trashParams.y = getTrashY();
                                        windowManager.updateViewLayout(rlTrash, trashParams);
                                    }
                                }

        );
        ValueAnimator yAnim = new ValueAnimator();
        yAnim.setIntValues(getIconY() - iconParams.y, 0);
        yAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        offSetTrashY = (int) valueAnimator.getAnimatedValue();
                                        iconParams.y = getIconY();
                                        windowManager.updateViewLayout(ivIcon, iconParams);
                                        trashParams.x = getTrashX();
                                        trashParams.y = getTrashY();
                                        windowManager.updateViewLayout(rlTrash, trashParams);

                                    }
                                }
        );
        iconOutOfTrashAnimation = new AnimatorSet();
        iconOutOfTrashAnimation.playTogether(xAnim, yAnim, widthAnim);
        iconOutOfTrashAnimation.setDuration(400);
        iconOutOfTrashAnimation.setInterpolator(new DecelerateInterpolator(4f));
        iconOutOfTrashAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isIconOnTrash = false;


            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                iconToTrashAnimation.cancel();
            }
        });
        iconOutOfTrashAnimation.start();

    }

    private void iconInToTrashAnimation() {
        ValueAnimator widthAnim = new ValueAnimator();
        widthAnim.setIntValues(ivTrash.getWidth(), ICON_WIDTH);
        widthAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final int value = (int) valueAnimator.getAnimatedValue();
                ivTrash.getLayoutParams().width = value;
                ivTrash.getLayoutParams().height = value;
                ivTrash.requestLayout();
            }
        });
        ValueAnimator xAnim = new ValueAnimator();
        xAnim.setIntValues(trashParams.x - iconParams.x, 0);
        xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                iconParams.x = trashParams.x - (int) valueAnimator.getAnimatedValue();
                windowManager.updateViewLayout(ivIcon, iconParams);
                trashParams.x = getTrashX();
                trashParams.y = getTrashY();
                windowManager.updateViewLayout(rlTrash, trashParams);
            }
        });
        ValueAnimator yAnim = new ValueAnimator();
        yAnim.setIntValues(trashParams.y - iconParams.y, 0);
        yAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                iconParams.y = trashParams.y - (int) valueAnimator.getAnimatedValue();
                windowManager.updateViewLayout(ivIcon, iconParams);
                trashParams.x = getTrashX();
                trashParams.y = getTrashY();
                windowManager.updateViewLayout(rlTrash, trashParams);
            }
        });
        iconToTrashAnimation = new AnimatorSet();
        iconToTrashAnimation.playTogether(xAnim, yAnim, widthAnim);
        iconToTrashAnimation.setInterpolator(new DecelerateInterpolator(4f));
        iconToTrashAnimation.setDuration(400);
        iconToTrashAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isIconOnTrash = true;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (iconOutOfTrashAnimation != null) iconOutOfTrashAnimation.cancel();
            }
        });
        iconToTrashAnimation.start();
    }


    float trashToIconFactor;
    float trashToPositionFactor;

    private void trashEnterAnimation() {
        ValueAnimator animY = new ValueAnimator();
        animY.setFloatValues(0, 1);
        animY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                trashToPositionFactor = (float) valueAnimator.getAnimatedValue();
                trashToIconFactor = (float) valueAnimator.getAnimatedValue();
                trashParams.x = getTrashX();
                trashParams.y = getTrashY();
                windowManager.updateViewLayout(rlTrash, trashParams);
            }
        });
        animY.setDuration(400);
        animY.setInterpolator(new FastOutSlowInInterpolator());
        animY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                rlTrash.setVisibility(View.VISIBLE);
            }
        });
        animY.start();

    }

    private void trashExitAnimation() {
        ValueAnimator factorAnim = new ValueAnimator();
        factorAnim.setFloatValues(1, 0);
        factorAnim.setDuration(400);
        factorAnim.setInterpolator(new LinearOutSlowInInterpolator());
        factorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                trashToPositionFactor = (float) valueAnimator.getAnimatedValue();
                trashToIconFactor = (float) valueAnimator.getAnimatedValue();
                if (iconParams.x == trashParams.x && iconParams.y == trashParams.y) {
                    iconParams.y = getTrashY();
                    iconParams.x = getTrashX();
                    windowManager.updateViewLayout(ivIcon, iconParams);
                }
                trashParams.y = getTrashY();
                trashParams.x = getTrashX();
                windowManager.updateViewLayout(rlTrash, trashParams);


            }
        });
        factorAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rlTrash.setVisibility(View.GONE);
                if (iconParams.x == trashParams.x && iconParams.y == trashParams.y) {
                    onDestroy();
                }
            }
        });
        factorAnim.start();

    }

    private void iconToBorderX() {

        ValueAnimator anim = new ValueAnimator();
        anim.setInterpolator(new LinearOutSlowInInterpolator());
        if (isOnLeftSideOfScreen()) {
            anim.setIntValues(iconParams.x, -ICON_WIDTH / 3);
        } else {
            anim.setIntValues(iconParams.x, screenWidth - ICON_WIDTH * 2 / 3);
        }
        anim.setDuration(400);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                iconParams.x = (int) valueAnimator.getAnimatedValue();
                windowManager.updateViewLayout(ivIcon, iconParams);
            }
        });

        anim.start();
    }

    private void iconAboveYourFingerAnimation() {
        ValueAnimator xAnim = ValueAnimator.ofInt(ICON_WIDTH / 2, ICON_WIDTH);
        xAnim.setDuration(400);
        xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                fingerDistanceY = (int) valueAnimator.getAnimatedValue();
                iconParams.y = getIconY();
                windowManager.updateViewLayout(ivIcon, iconParams);
            }
        });
        xAnim.start();
    }

    //U T I L S

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = App.mResources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = App.mResources.getDimensionPixelSize(resourceId);
        }
        return result;

    }

    //P O S I T I O N

    /**
     * we want to know if the icon is close to the trash
     *
     * @return true if icon is close to the trash
     */
    private boolean isOnTrash() {
        int distanceToTrash = trashParams.width * 3 / 2;
        //we use the touch coordinates instead of the icon coordinates, otherwise the icon could not
        //scape for the trash
        Point icon = new Point(getIconX(), getIconY());
        Point trash = new Point(trashParams.x, trashParams.y);
        double dist = Math.sqrt(Math.pow(icon.x - trash.x, 2) + Math.pow(icon.y - trash.y, 2));
        return dist <= distanceToTrash;
    }

    private boolean isOnLeftSideOfScreen() {
        return iconParams.x <= (screenWidth / 2) - (ICON_WIDTH / 2);
    }

    private int getIconX() {
        int x = touchX - ICON_WIDTH / 2;
        int limitLeft = 0;
        int limitRight = screenWidth - ICON_WIDTH;
        return Math.max(Math.min(x, limitRight), limitLeft) - offSetTrashX;
    }

    private int getIconY() {
        int y = touchY - STATUS_BAR_HEIGHT - fingerDistanceY -ICON_WIDTH;
        int limitTop = 0;
        int limitBottom = screenHeight - ICON_WIDTH;
        return Math.min(Math.max(y, limitTop), limitBottom) - offSetTrashY;
    }

    private int getTrashX() {
        int centerX = screenWidth / 2 - ICON_WIDTH / 2;
        int trashToIconDelta = (int) ((touchX - offSetTrashX - centerX) * 0.1f);
        return centerX + (int) (trashToIconDelta * trashToIconFactor);
    }

    private int getTrashY() {
        int centerY = screenHeight - (int) (TRASH_MARGIN_BOTTOM * trashToPositionFactor);
        int y=Math.max(touchY, screenHeight / 2);
        int trashToIconDelta = (int) ((y - STATUS_BAR_HEIGHT - fingerDistanceY - offSetTrashY - centerY) * 0.1f);
        return centerY + (int) (trashToIconDelta * trashToIconFactor);
    }


}