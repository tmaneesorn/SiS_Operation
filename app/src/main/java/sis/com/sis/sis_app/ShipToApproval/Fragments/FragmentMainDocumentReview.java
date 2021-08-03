package sis.com.sis.sis_app.ShipToApproval.Fragments;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import sis.com.sis.sis_app.ShipToApproval.Activities.MainActivity;
import sis.com.sis.sis_app.ShipToApproval.Constants;
import sis.com.sis.sis_app.R;

public class FragmentMainDocumentReview extends Fragment  {
    String  urlGet = "";

    @BindView(R.id.webView) WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.shipto_fragment_main_document_review, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        urlGet = (String) bundle.getSerializable("url");
        String type_doc = (String) bundle.getSerializable("type_doc");

        if (type_doc.equals("po")){
            ((MainActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.main_title_document_detail) + " : Letter / PO");
        }
        else if (type_doc.equals("id_card")){
            ((MainActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.main_title_document_detail) + " : ID Card");
        }
        else if (type_doc.equals("map")){
            ((MainActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.main_title_document_detail) + " : Map");
        }
        else {
            ((MainActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.main_title_document_detail) + " : Other Attach file");
        }

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.invalidate();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);

        if (urlGet.contains(".pdf"))
        {
            Constants.doLog(" LOG URL DOCUMENT REVIEW PDF : " + "https://drive.google.com/viewerng/viewer?embedded=true&url=" + urlGet);
            mWebView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + urlGet);
                }
            }, 500);
            Constants.doLog("OPEN 1ST");
            mWebView.setWebViewClient(new WebViewClient() {
                boolean checkOnPageStartedCalled = false;

                public boolean shouldOverrideUrlLoading(WebView view,String url) {
                    return false;
                }

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    checkOnPageStartedCalled = true;
//                    if (customProgress == null) customProgress = CustomDialogLoading.getInstance();
//                    customProgress.showProgress(getContext(), "Loading",null,getContext().getDrawable(R.drawable.ic_loading),false, false, true);

                }

                @Override
                public void onPageFinished(WebView view, String url) {

                    if (checkOnPageStartedCalled) {
                        Constants.doLog("OPEN 1ST FINISHED");
//                        customProgress.hideProgress();
                    } else {
//                        customProgress.hideProgress();
                        showPdfFile(urlGet);
                    }
                }
            });
        }
        else {
            Constants.doLog(" LOG URL DOCUMENT REVIEW JPG : " + urlGet);
            mWebView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl(urlGet);
                }
            }, 500);
        }

        return view;
    }

    private void showPdfFile(final String imageString) {
        mWebView.invalidate();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + urlGet);
            }
        }, 500);
        Constants.doLog("OPEN 2ND");
        mWebView.setWebViewClient(new WebViewClient() {
            boolean checkOnPageStartedCalled = false;

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                checkOnPageStartedCalled = true;
//                if (customProgress == null) customProgress = CustomDialogLoading.getInstance();
//                customProgress.showProgress(getContext(), "Loading",null,getContext().getDrawable(R.drawable.ic_loading),false, false, true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (checkOnPageStartedCalled) {
//                    customProgress.hideProgress();
                    Constants.doLog("OPEN 2ND FINISHED");
                } else {
//                    customProgress.hideProgress();
                    Constants.doLog("OPEN 3RD");
                    showPdfFile(imageString);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
