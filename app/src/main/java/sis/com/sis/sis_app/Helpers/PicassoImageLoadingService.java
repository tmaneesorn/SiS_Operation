package sis.com.sis.sis_app.Helpers;

import android.content.Context;
import android.widget.ImageView;

import ss.com.bannerslider.ImageLoadingService;


public class PicassoImageLoadingService implements ImageLoadingService {
    public Context context;

    public PicassoImageLoadingService(Context context) {
        this.context = context;
    }

    @Override
    public void loadImage(String url, ImageView imageView) {
        PicassoTrustAll.getInstance(context).load(url).into(imageView);
    }

    @Override
    public void loadImage(int resource, ImageView imageView) {
        PicassoTrustAll.getInstance(context).load(resource).into(imageView);
    }

    @Override
    public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView)
    {
        PicassoTrustAll.getInstance(context).load(url).fit().error(errorDrawable).placeholder(placeHolder).into(imageView);
    }
}