package sis.com.sis.sis_app.Views;


import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import sis.com.sis.sis_app.Constants;
import sis.com.sis.sis_app.R;


public class PopupBrowser extends PopupWindow
{
    Context mContext;

    private WebView mWebView;
    private TextView tvLabel;
    private ImageButton btnClose, btnBack, btnNext, btnMore;

    public PopupBrowser(Context context)
    {
        super(context);

        mContext = context;

        View view = LayoutInflater.from(context).inflate(R.layout.popup_browser, null);
        setContentView(view);

        setBackgroundDrawable(null);
        tvLabel = (TextView)view.findViewById(R.id.tvLabel);
        mWebView = (WebView)view.findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);

//        mWebView.loadUrl("www.sisthai.com");
        mWebView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                Constants.doLog("Loading  fail" + failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("R.string.sslExpired");
                builder.setPositiveButton("R.string.btnContinue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("R.string.cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();


            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr)
            {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString)
            {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon)
            {
                tvLabel.setText("Loading...");
                Constants.doLog("Loading STATE : " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                if (mWebView.canGoBack())
                {
                    btnBack.setEnabled(true);
                    btnBack.setClickable(true);
                } else
                {
                    btnBack.setEnabled(false);
                    btnBack.setClickable(false);
                }

                if (mWebView.canGoForward())
                {
                    btnNext.setEnabled(true);
                    btnNext.setClickable(true);
                } else
                {
                    btnNext.setEnabled(false);
                    btnNext.setClickable(false);
                }

                tvLabel.setText(view.getTitle());

            }


        });

        btnClose = (ImageButton)view.findViewById(R.id.btnClose);
        btnBack = (ImageButton)view.findViewById(R.id.btnBack);
        btnNext = (ImageButton)view.findViewById(R.id.btnNext);
        btnMore = (ImageButton)view.findViewById(R.id.btnMore);


        btnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {

                PopupBrowser.this.dismiss();

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {
                mWebView.goBack();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {
                mWebView.goForward();
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {

                String url = mWebView.getUrl();
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(url, url);
                clipboard.setPrimaryClip(clip);
//                Toast.makeText(mContext, R.string.copyURL, Toast.LENGTH_LONG).show();
            }
        });

        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);


    }

    public void show(View anchor)
    {
        showAtLocation(anchor, Gravity.CENTER, 0, 0);
    }

    public void showWithUrl(View anchor, String url) {
        Constants.doLog("Loading " + url);
        showAtLocation(anchor, Gravity.CENTER, 0, 0);

        String lastThreeChars = url.substring(url.length() - 3);

        if (lastThreeChars.equals("pdf")) {
            url = "https://docs.google.com/viewer?embedded=true&url=" + url;
        }
        mWebView.loadUrl(url);
    }

    public void showWithUrlAndPostData(View anchor, String url, String postData) // eq: String postData = "username=" + URLEncoder.encode(my_username, "UTF-8") + "&password=" + URLEncoder.encode(my_password, "UTF-8");
    {
        showAtLocation(anchor, Gravity.CENTER, 0, 0);
        mWebView.postUrl(url, postData.getBytes());
    }


}