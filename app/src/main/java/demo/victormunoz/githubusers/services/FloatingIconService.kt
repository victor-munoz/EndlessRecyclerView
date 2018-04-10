package demo.victormunoz.githubusers.services

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView

import demo.victormunoz.githubusers.App
import demo.victormunoz.githubusers.R
import demo.victormunoz.githubusers.features.allusers.AllUsersActivity

class FloatingIconService : Service(), View.OnClickListener, View.OnTouchListener {

    private val LAYOUT_FLAG = layoutFlag
    private val ICON_WIDTH = App.mResources.getDimensionPixelOffset(R.dimen.image_circle_normal)
    private val TRASH_WIDTH = App.mResources.getDimensionPixelOffset(R.dimen.icon_remove)
    private val TRASH_MARGIN_BOTTOM = App.mResources.getDimensionPixelOffset(R.dimen.icon_remove_margin_bottom)
    private val MAX_FINGER_DISTANCE = ICON_WIDTH
    private val STATUS_BAR_HEIGHT = statusBarHeight

    private var windowManager: WindowManager? = null
    //icons
    private var ivIcon: ImageView? = null
    private var ivTrash: ImageView? = null
    private var rlTrash: ConstraintLayout? = null
    private var iconParams: WindowManager.LayoutParams? = null
    private var trashIconParams: WindowManager.LayoutParams? = null
    //motion
    private var lastAction: Int = 0
    private var touchX: Int = 0
    private var touchY: Int = 0
    private var isIconOnTrash: Boolean = false
    //animations
    private var iconToTrashAnimation: AnimatorSet? = null
    private var iconOutOfTrashAnimation: AnimatorSet? = null
    //factor to trash following icon animation
    private var trashEnterAnimPercent: Float = 0.toFloat()
    //delta to above finger position on touch first move animation
    private var fingerDeltaY: Int = 0
    private var fingerDeltaX: Int = 0
    //delta to icon in out of trash animation
    private var iconDeltaX: Int = 0
    private var iconDeltaY: Int = 0
    private var iconsBound: Rect? = null

    //U T I L S

    private val statusBarHeight: Int
        get() {
            var result = 0
            val resourceId = App.mResources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = App.mResources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    private val layoutFlag: Int
        get() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_PHONE
        } else {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }

    //P O S I T I O N

    /**
     * we want to know if the icon is close to the trash
     *
     * @return true if icon is close to the trash
     */
    private val isIconInsideTrashArea: Boolean
        get() {
            val iconX = touchX - ICON_WIDTH / 2 - fingerDeltaX
            val iconY = touchY - ICON_WIDTH / 2 - fingerDeltaY
            val dist = Math.sqrt(Math.pow((iconX - trashIconParams!!.x).toDouble(), 2.0) + Math.pow((iconY - trashIconParams!!.y).toDouble(), 2.0))
            val distanceToTrash = trashIconParams!!.width * 3 / 2
            return dist <= distanceToTrash
        }

    private val iconX: Int
        get() {
            val iconX = touchX - ICON_WIDTH / 2
            val iconFixedX = Math.max(Math.min(iconX, iconsBound!!.right), iconsBound!!.left)
            return iconFixedX - iconDeltaX - fingerDeltaX
        }

    private val iconY: Int
        get() {
            val iconY = touchY - ICON_WIDTH / 2 - fingerDeltaY
            val iconFixedY = Math.min(Math.max(iconY, iconsBound!!.top), iconsBound!!.bottom)
            return iconFixedY - iconDeltaY
        }

    private val trashX: Int
        get() {
            val trashX = iconsBound!!.centerX()
            val trashToIconDelta = ((iconX - trashX) * 0.1f).toInt()
            return trashX + (trashToIconDelta * trashEnterAnimPercent).toInt()
        }

    private val trashY: Int
        get() {
            val trashY = iconsBound!!.bottom + ICON_WIDTH - (TRASH_MARGIN_BOTTOM * trashEnterAnimPercent).toInt()
            val fixedTouchY = Math.max(touchY, iconsBound!!.centerY())
            val iconY = fixedTouchY - STATUS_BAR_HEIGHT - fingerDeltaY - ICON_WIDTH / 2 - iconDeltaY
            val trashToIconDelta = ((iconY - trashY) * 0.1f).toInt()
            return trashY + (trashToIconDelta * trashEnterAnimPercent).toInt()
        }


    //L I F E C Y C L E

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        setIconsBound()
        setLauncherIcon()
        setCloseIcon()
    }

    override fun onDestroy() {
        if (ivIcon != null) {
            windowManager!!.removeView(ivIcon)
            ivIcon!!.setOnTouchListener(null)
            ivIcon!!.setOnClickListener(null)
            ivIcon = null
        }
        if (rlTrash != null) {
            windowManager!!.removeView(rlTrash)
            rlTrash = null
        }
        super.onDestroy()
    }

    //U S E R  A C T I O N S

    override fun onClick(view: View) {
        val intent = Intent(applicationContext, AllUsersActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        stopSelf()
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        touchX = event.rawX.toInt()
        touchY = event.rawY.toInt() - STATUS_BAR_HEIGHT

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                lastAction = MotionEvent.ACTION_DOWN
                return false
            }

            MotionEvent.ACTION_MOVE -> {

                if (lastAction == MotionEvent.ACTION_DOWN) {
                    trashEnterAnimation()
                    iconAboveYourFingerAnimation()
                }
                lastAction = MotionEvent.ACTION_MOVE

                if (isIconInsideTrashArea) {

                    if (iconOutOfTrashAnimation != null && iconOutOfTrashAnimation!!.isRunning) {
                        iconOutOfTrashAnimation!!.cancel()
                    }
                    if (isIconOnTrash) {
                        updateIcon(trashIconParams!!.x, trashIconParams!!.y)
                        updateTrashIcon(trashX, trashY)
                    } else if (iconToTrashAnimation == null || !iconToTrashAnimation!!.isRunning) {
                        iconInToTrashAnimation()
                    }

                } else {

                    if (iconToTrashAnimation != null && iconToTrashAnimation!!.isRunning) {
                        iconToTrashAnimation!!.cancel()
                    }
                    if (!isIconOnTrash) {
                        updateIcon(iconX, iconY)
                        updateTrashIcon(trashX, trashY)
                    } else if (iconOutOfTrashAnimation == null || !iconOutOfTrashAnimation!!.isRunning) {
                        iconOutOfTrashAnimation()
                    }

                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (lastAction == MotionEvent.ACTION_MOVE) {
                    if (!isIconInsideTrashArea) {
                        iconToBorderX()
                    }
                    trashExitAnimation()
                    return true
                }
                return false
            }
        }
        view.performClick()
        return true
    }

    //S E T

    private fun setIconsBound() {
        val displaymetrics = resources.displayMetrics
        val screenHeight = displaymetrics.heightPixels - ICON_WIDTH - statusBarHeight
        val screenWidth = displaymetrics.widthPixels - ICON_WIDTH
        iconsBound = Rect(0, 0, screenWidth, screenHeight)
    }

    @SuppressLint("InflateParams")
    private fun setCloseIcon() {
        val li = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (li != null) {
            rlTrash = li.inflate(R.layout.service_icon_remove, null, false) as ConstraintLayout
            ivTrash = rlTrash!!.findViewById(R.id.trash_icon)
            trashIconParams = WindowManager.LayoutParams(ICON_WIDTH, ICON_WIDTH, LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT)
            trashIconParams!!.gravity = Gravity.TOP or Gravity.START
            trashIconParams!!.x = iconsBound!!.centerX()
            trashIconParams!!.y = iconsBound!!.bottom - TRASH_MARGIN_BOTTOM
            windowManager!!.addView(rlTrash, trashIconParams)
        } else {
            stopSelf()
            //            onDestroy();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setLauncherIcon() {
        ivIcon = ImageView(applicationContext)
        ivIcon!!.setImageResource(R.mipmap.ic_launcher)
        ivIcon!!.isClickable = true
        ivIcon!!.setOnClickListener(this)
        ivIcon!!.setOnTouchListener(this)

        iconParams = WindowManager.LayoutParams(ICON_WIDTH, ICON_WIDTH, LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT)
        iconParams!!.gravity = Gravity.TOP or Gravity.START
        iconParams!!.x = iconsBound!!.centerX()
        iconParams!!.y = iconsBound!!.bottom / 4
        ivIcon!!.layoutParams = iconParams
        windowManager!!.addView(ivIcon, ivIcon!!.layoutParams)
        iconToBorderX()
    }

    //A N I M A T I O N S

    private fun iconOutOfTrashAnimation() {
        val widthAnim = ValueAnimator()
        widthAnim.setIntValues(ivTrash!!.width, TRASH_WIDTH)
        widthAnim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            ivTrash!!.layoutParams.width = value
            ivTrash!!.layoutParams.height = value
            ivTrash!!.requestLayout()
        }

        val xAnim = ValueAnimator()
        xAnim.setIntValues(iconX - iconParams!!.x, 0)
        xAnim.addUpdateListener { valueAnimator ->
            iconDeltaX = valueAnimator.animatedValue as Int
            iconParams!!.x = iconX
            trashIconParams!!.x = trashX
            windowManager!!.updateViewLayout(ivIcon, iconParams)
            windowManager!!.updateViewLayout(rlTrash, trashIconParams)
        }
        val yAnim = ValueAnimator()
        yAnim.setIntValues(iconY - iconParams!!.y, 0)
        yAnim.addUpdateListener { valueAnimator ->
            iconDeltaY = valueAnimator.animatedValue as Int
            iconParams!!.y = iconY
            trashIconParams!!.y = trashY
            windowManager!!.updateViewLayout(ivIcon, iconParams)
            windowManager!!.updateViewLayout(rlTrash, trashIconParams)

        }
        iconOutOfTrashAnimation = AnimatorSet()
        iconOutOfTrashAnimation!!.playTogether(xAnim, yAnim, widthAnim)
        iconOutOfTrashAnimation!!.duration = 400
        iconOutOfTrashAnimation!!.interpolator = DecelerateInterpolator(4f)
        iconOutOfTrashAnimation!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
                isIconOnTrash = false
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                isIconOnTrash = false


            }

        })
        iconOutOfTrashAnimation!!.start()

    }

    private fun iconInToTrashAnimation() {
        val widthAnim = ValueAnimator()
        widthAnim.setIntValues(ivTrash!!.width, ICON_WIDTH)
        widthAnim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            ivTrash!!.layoutParams.width = value
            ivTrash!!.layoutParams.height = value
            ivTrash!!.requestLayout()
        }
        val xAnim = ValueAnimator()
        xAnim.setIntValues(trashIconParams!!.x - iconParams!!.x, 0)
        xAnim.addUpdateListener { valueAnimator ->
            iconParams!!.x = trashIconParams!!.x - valueAnimator.animatedValue as Int
            windowManager!!.updateViewLayout(ivIcon, iconParams)
            trashIconParams!!.x = trashX
            //trashIconParams.y = getTrashY();
            windowManager!!.updateViewLayout(rlTrash, trashIconParams)
        }
        val yAnim = ValueAnimator()
        yAnim.setIntValues(trashIconParams!!.y - iconParams!!.y, 0)
        yAnim.addUpdateListener { valueAnimator ->
            iconParams!!.y = trashIconParams!!.y - valueAnimator.animatedValue as Int
            windowManager!!.updateViewLayout(ivIcon, iconParams)
            //  trashIconParams.x = getTrashX();
            trashIconParams!!.y = trashY
            windowManager!!.updateViewLayout(rlTrash, trashIconParams)
        }
        iconToTrashAnimation = AnimatorSet()
        iconToTrashAnimation!!.playTogether(xAnim, yAnim, widthAnim)
        iconToTrashAnimation!!.interpolator = DecelerateInterpolator(4f)
        iconToTrashAnimation!!.duration = 400
        iconToTrashAnimation!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
                isIconOnTrash = true
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                isIconOnTrash = true
            }

        })
        iconToTrashAnimation!!.start()
    }

    private fun trashEnterAnimation() {
        val animY = ValueAnimator()
        animY.setFloatValues(0f, 1f)
        animY.addUpdateListener { valueAnimator ->
            trashEnterAnimPercent = valueAnimator.animatedValue as Float
            trashIconParams!!.x = trashX
            trashIconParams!!.y = trashY
            windowManager!!.updateViewLayout(rlTrash, trashIconParams)
        }
        animY.duration = 400
        animY.interpolator = FastOutSlowInInterpolator()
        animY.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                rlTrash!!.visibility = View.VISIBLE
            }
        })
        animY.start()

    }

    private fun trashExitAnimation() {
        val factorAnim = ValueAnimator()
        factorAnim.setFloatValues(1f, 0f)
        factorAnim.duration = 400
        factorAnim.interpolator = LinearOutSlowInInterpolator()
        factorAnim.addUpdateListener { valueAnimator ->
            trashEnterAnimPercent = valueAnimator.animatedValue as Float
            if (iconParams!!.x == trashIconParams!!.x && iconParams!!.y == trashIconParams!!.y) {
                iconParams!!.y = trashY
                iconParams!!.x = trashX
                windowManager!!.updateViewLayout(ivIcon, iconParams)
            }
            trashIconParams!!.y = trashY
            trashIconParams!!.x = trashX
            windowManager!!.updateViewLayout(rlTrash, trashIconParams)


        }
        factorAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                rlTrash!!.visibility = View.GONE
                if (iconParams!!.x == trashIconParams!!.x && iconParams!!.y == trashIconParams!!.y) {
                    //onDestroy();
                    stopSelf()
                }
            }
        })
        factorAnim.start()

    }

    private fun iconToBorderX() {
        val anim = ValueAnimator()
        anim.interpolator = LinearOutSlowInInterpolator()
        if (iconParams!!.x < iconsBound!!.centerX()) {
            anim.setIntValues(iconParams!!.x, iconsBound!!.left - ICON_WIDTH / 3)
        } else {
            anim.setIntValues(iconParams!!.x, iconsBound!!.right + ICON_WIDTH / 3)
        }
        anim.duration = 400
        anim.addUpdateListener { valueAnimator ->
            iconParams!!.x = valueAnimator.animatedValue as Int
            windowManager!!.updateViewLayout(ivIcon, iconParams)
        }
        anim.start()
    }

    private fun iconAboveYourFingerAnimation() {
        val yAnim = ValueAnimator.ofInt(touchY - iconParams!!.y - ICON_WIDTH / 2, MAX_FINGER_DISTANCE)
        yAnim.addUpdateListener { valueAnimator ->
            fingerDeltaY = valueAnimator.animatedValue as Int
            iconParams!!.y = iconY
            windowManager!!.updateViewLayout(ivIcon, iconParams)
        }
        val xAnim = ValueAnimator.ofInt(ICON_WIDTH / 3, 0)
        xAnim.addUpdateListener { valueAnimator ->
            if (iconParams!!.x < iconsBound!!.centerX()) {
                fingerDeltaX = valueAnimator.animatedValue as Int
            } else {
                fingerDeltaX = -(valueAnimator.animatedValue as Int)
            }
            iconParams!!.x = iconX
            windowManager!!.updateViewLayout(ivIcon, iconParams)
        }
        val set = AnimatorSet()
        set.playTogether(xAnim, yAnim)
        set.duration = 400
        set.start()
    }

    private fun updateIcon(x: Int, y: Int) {
        iconParams!!.x = x
        iconParams!!.y = y
        windowManager!!.updateViewLayout(ivIcon, iconParams)
    }

    private fun updateTrashIcon(x: Int, y: Int) {
        trashIconParams!!.x = x
        trashIconParams!!.y = y
        windowManager!!.updateViewLayout(rlTrash, trashIconParams)
    }


}