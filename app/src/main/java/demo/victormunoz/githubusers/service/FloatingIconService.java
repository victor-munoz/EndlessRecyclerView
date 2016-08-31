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
    private final int CIRCLE_DIAMETER = App.mResources.getDimensionPixelOffset(R.dimen.little_circle_image);
    private final int ICON_TRASH = App.mResources.getDimensionPixelOffset(R.dimen.icon_trash);
    private final int TRASH_BOTTOM_MARGIN = CIRCLE_DIAMETER * 2;
    private final int STATUS_BAR_HEIGHT = getStatusBarHeight();

    private WindowManager windowManager;
    //images
    private ImageView iconImage;
    private WindowManager.LayoutParams iconsParams;
    private RelativeLayout rlTrash;
    private WindowManager.LayoutParams trashParams;
    //sizes
    private int screenWidth;
    private int screenHeight;
    private int offsetY;
    private int offSetTrashX;
    private int offSetTrashY;
    private int rawX;
    private int rawY;

    private boolean isMoveAction = false;
    private boolean isIconOnTrash;
    //animations
    private AnimatorSet iconToTrashAnimation;
    private AnimatorSet iconOutOfTrashAnimation;
    private ImageView ivTrash;


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
        if (iconImage != null) {
            windowManager.removeView(iconImage);
            iconImage = null;
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
        rawX= (int) event.getRawX();
        rawY= (int) event.getRawY();
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
                } else {
                    if (isIconOnTrash && (iconOutOfTrashAnimation == null || !iconOutOfTrashAnimation.isRunning())) {
                        iconAboveYourFingerAnimation();
                        iconOutOfTrashAnimation();
                    }
                    iconsParams.x = fixPositionX();
                    iconsParams.y = fixPositionY();
                }
                windowManager.updateViewLayout(iconImage, iconsParams);
                return true;
            case MotionEvent.ACTION_UP:
                //we are performing a moving action
                if (isMoveAction) {
                    if (isOnTrash()) {
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

    @SuppressLint("InflateParams")
    private void setTrashImage() {
        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        rlTrash = (RelativeLayout) li.inflate(R.layout.trash_icon, null);
        rlTrash.setVisibility(View.GONE);
        ivTrash = (ImageView) rlTrash.findViewById(R.id.trash_icon);
        trashParams = new WindowManager.LayoutParams(
                CIRCLE_DIAMETER,
                CIRCLE_DIAMETER,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        trashParams.gravity = Gravity.TOP | Gravity.START;
        trashParams.x = screenWidth / 2 - CIRCLE_DIAMETER / 2;
        trashParams.y = screenHeight - TRASH_BOTTOM_MARGIN - CIRCLE_DIAMETER / 2;
        windowManager.addView(rlTrash, trashParams);
    }

    private void setIconImage() {
        iconImage = new ImageView(this);
        iconImage.setClickable(true);
        iconImage.setOnClickListener(this);
        iconImage.setImageResource(R.mipmap.ic_launcher);
        iconsParams = new WindowManager.LayoutParams(
                CIRCLE_DIAMETER,
                CIRCLE_DIAMETER,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        iconsParams.gravity = Gravity.TOP | Gravity.START;
        iconsParams.x = screenWidth / 2 - CIRCLE_DIAMETER / 2;
        iconsParams.y = screenHeight / 4;
        iconImage.setOnTouchListener(this);
        windowManager.addView(iconImage, iconsParams);
        iconToBorderX();
    }

    //A N I M A T I O N S

    private void iconOutOfTrashAnimation() {
        ValueAnimator widthAnim = new ValueAnimator();
        widthAnim.setIntValues(ivTrash.getWidth(), ICON_TRASH);
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
        xAnim.setIntValues(fixPositionX() - iconsParams.x, 0);
        xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                                {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        offSetTrashX=(int) valueAnimator.getAnimatedValue();
                                        iconsParams.x = fixPositionX();
                                        windowManager.updateViewLayout(iconImage, iconsParams);
                                    }
                                }

        );
        ValueAnimator yAnim = new ValueAnimator();
        yAnim.setIntValues(fixPositionY() - iconsParams.y, 0);
        yAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                                {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        offSetTrashY=(int) valueAnimator.getAnimatedValue();
                                        iconsParams.y = fixPositionY();
                                        windowManager.updateViewLayout(iconImage, iconsParams);

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
        widthAnim.setIntValues(ivTrash.getWidth(), CIRCLE_DIAMETER);
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
        xAnim.setIntValues(iconsParams.x, screenWidth / 2 - CIRCLE_DIAMETER / 2);
        xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                iconsParams.x = (int) valueAnimator.getAnimatedValue();
                windowManager.updateViewLayout(iconImage, iconsParams);
            }
        });
        ValueAnimator yAnim = new ValueAnimator();
        yAnim.setIntValues(iconsParams.y, screenHeight - TRASH_BOTTOM_MARGIN - CIRCLE_DIAMETER / 2);
        yAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                iconsParams.y = (int) valueAnimator.getAnimatedValue();
                windowManager.updateViewLayout(iconImage, iconsParams);
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

    private void trashEnterAnimation() {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(screenHeight, screenHeight - TRASH_BOTTOM_MARGIN - trashParams.width / 2);
        anim.setDuration(400);
        anim.setInterpolator(new FastOutSlowInInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                trashParams.y = (int) valueAnimator.getAnimatedValue();
                windowManager.updateViewLayout(rlTrash, trashParams);
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                rlTrash.setVisibility(View.VISIBLE);
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
                windowManager.updateViewLayout(rlTrash, trashParams);
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
                rlTrash.setVisibility(View.GONE);
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

    private void iconToBorderX() {

        ValueAnimator anim = new ValueAnimator();
        anim.setInterpolator(new LinearOutSlowInInterpolator());
        if (isOnLeftSideOfScreen()) {
            anim.setIntValues(iconsParams.x, -CIRCLE_DIAMETER / 3);
        } else {
            anim.setIntValues(iconsParams.x, screenWidth - CIRCLE_DIAMETER * 2 / 3);
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
        ValueAnimator xAnim = ValueAnimator.ofInt(CIRCLE_DIAMETER / 2, CIRCLE_DIAMETER);
        xAnim.setDuration(400);
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
        int resourceId = App.mResources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = App.mResources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //P O S I T I O N

    private boolean isOnTrash() {
        int distanceToTrash = trashParams.width * 3 / 2;
        Point icon = new Point(
                rawX - iconsParams.width / 2 - offSetTrashX
                ,rawY- STATUS_BAR_HEIGHT - iconsParams.width / 2 - offsetY - offSetTrashY);
        Point trash = new Point(trashParams.x, trashParams.y);
        double dist = Math.sqrt(Math.pow(icon.x - trash.x, 2) + Math.pow(icon.y - trash.y, 2));
        return dist <= distanceToTrash;
    }

    private boolean isOnLeftSideOfScreen() {
        return iconsParams.x <= (screenWidth / 2) - (iconsParams.width / 2);
    }

    private int fixPositionX() {
        if (rawX > screenWidth - iconsParams.width / 2) {
            return screenWidth - iconsParams.width - offSetTrashX;
        } else if (rawX < iconsParams.width / 2) {
            return 0 - offSetTrashX;
        } else {
            return  rawX - iconsParams.width / 2 - offSetTrashX;
        }
    }

    private int fixPositionY() {
        if (rawY - STATUS_BAR_HEIGHT - offsetY < iconsParams.width / 2) {
            return 0;
        } else {
            return  rawY - STATUS_BAR_HEIGHT - iconsParams.width / 2 - offsetY - offSetTrashY;
        }

    }


}