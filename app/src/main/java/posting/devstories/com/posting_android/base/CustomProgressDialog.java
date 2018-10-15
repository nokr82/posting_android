package posting.devstories.com.posting_android.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import kr.co.threemeals.android.R;

/**
 * Created by dev1 on 2017-05-08.
 */

public class CustomProgressDialog extends Dialog {


    private ImageView imageView;
    private Context mContext;

    public CustomProgressDialog(Context context) {
        super(context, R.style.CustomProgressBar);
        mContext = context;

        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(true);
        //setOnCancelListener(null);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 400);
        imageView = new ImageView(context);
        imageView.setBackgroundResource(R.drawable.loading_animation);
        layout.addView(imageView, params);
        addContentView(layout, params);
    }

    @Override
    public void show() {
        super.show();
        AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getBackground();
        frameAnimation.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
