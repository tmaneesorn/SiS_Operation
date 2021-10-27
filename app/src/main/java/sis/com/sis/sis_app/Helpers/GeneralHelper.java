package sis.com.sis.sis_app.Helpers;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Environment;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

import sis.com.sis.sis_app.Constants;
import sis.com.sis.sis_app.R;


public class GeneralHelper
{
    private static GeneralHelper uniqInstance;

    private AsyncHttpClient client;
    private boolean isSyncingAccount;
    private Fetch fetch;
    private Context mContext;

    public interface GeneralHelperListener
    {
//        public void syncAccountCallback(boolean success, User user);
    }


    private GeneralHelper() {
    }

    public static GeneralHelper getInstance()
    {
        if (uniqInstance == null)
        {
            synchronized(GeneralHelper.class)
            {
                // check again to avoid multi-thread access
                if (uniqInstance == null)
                    uniqInstance = new GeneralHelper();
            }
        }
        return uniqInstance;
    }


    public void showBasicAlert(Context mContext, String message)
    {
        TextView textView = new TextView(mContext);
        textView.setText(mContext.getResources().getString(R.string.main_title_information));
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(20F);
        textView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        textView.setTextColor(Color.WHITE);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCustomTitle(textView);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(
                mContext.getResources().getString(R.string.button_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showUpdateAlert(Context mContext, String message)
    {
        TextView textView = new TextView(mContext);
        textView.setText(mContext.getResources().getString(R.string.main_title_information));
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(20F);
        textView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        textView.setTextColor(Color.WHITE);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCustomTitle(textView);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(
                mContext.getResources().getString(R.string.button_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        System.exit(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void sendPushTokenToServer(Context mContext)
    {
        int user_id = SharedPreferenceHelper.getSharedPreferenceInt(mContext,"user_id", 0);
        String token = SharedPreferenceHelper.getSharedPreferenceString(mContext,"token", "");
        if (client == null) client = new AsyncHttpClient(true, 80, 443);
        RequestParams rParams = new RequestParams();
        rParams.put("token", token);
        rParams.put("device", "ANDROID");
        rParams.put("user_id", Integer.toString(user_id));

        Constants.doLog("Sending to server " + token);
//        client.post(Constants.API_HOST + "push_token", rParams, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onStart()
//            {
//
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] response)
//            {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    Constants.doLog(new String(response, StandardCharsets.UTF_8));
//                }
//            }
//
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
//            {
//
//            }
//
//            @Override
//            public boolean getUseSynchronousMode() {
//                return false;
//            }
//
//            @Override
//            public void onRetry(int retryNo)
//            {
//
//            }
//        });
    }






    // GET USER OBJECT
//    public void syncAccount(Context mContext, final GeneralHelperListener sharedHelperListener)
//    {
//        if (isSyncingAccount) return;
//
//
//        RequestParams rParams = new RequestParams();
//        int company_id = SharedPreferenceHelper.getSharedPreferenceInt(mContext, "company_id", 0);
//        String user_email = SharedPreferenceHelper.getSharedPreferenceString(mContext, "user_email", "");
//        String user_password = SharedPreferenceHelper.getSharedPreferenceString(mContext, "user_password", "");
//
//        rParams.put("company", Integer.toString(company_id));
//        rParams.put("email", user_email);
//        rParams.put("password", user_password);
//
//        if (client == null) client = new AsyncHttpClient(true, 80, 443);
//        client.post(Constants.API_HOST + "syncUserAccount",rParams, new AsyncHttpResponseHandler()
//        {
//
//            @Override
//            public void onStart()
//            {
//                Constants.doLog("Syncing account");
//                isSyncingAccount = true;
//            }
//
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] response)
//            {
//                isSyncingAccount = false;
//                Gson gson = new Gson();
//                ResponseResult responseResult = gson.fromJson(new String(response, StandardCharsets.UTF_8), ResponseResult.class);
//
//                Constants.doLog(new String(response, StandardCharsets.UTF_8));
//
//                if (responseResult.responseCode.errorCode == 0)
//                {
//                    if (sharedHelperListener != null) sharedHelperListener.syncAccountCallback(true, responseResult.responseData.user);
//                } else
//                {
//                    if (sharedHelperListener != null) sharedHelperListener.syncAccountCallback(false, null);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
//            {
//                isSyncingAccount = false;
//                if (sharedHelperListener != null) sharedHelperListener.syncAccountCallback(false, null);
//            }
//
//            @Override
//            public void onRetry(int retryNo)
//            {
//                // called when request is retried
//            }
//        });
//
//
//    }



    public void downloadUrl(Context mContext, String name, String type, String url)
    {
        this.mContext = mContext;
        if (fetch == null)
        {
            Constants.doLog("Fetch and setting listener");
            FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(mContext)
                    .setDownloadConcurrentLimit(3)
                    .build();
            fetch = Fetch.Impl.getInstance(fetchConfiguration);
            fetch.addListener(fetchListener);
        }



        //@todo: Change back to https when online. Certificate is personal so error causes failed download.
        url = url.replace("https://","http://");

        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String file = f.getAbsolutePath() + "/" + name + "." + type;

        final Request request = new Request(url, file);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);
        fetch.enqueue(request, updatedRequest -> {
            Constants.doLog("Started downloading");
        }, error -> {
            if (mContext != null) showBasicAlert(mContext,"Error downloading: " + error.getThrowable().getLocalizedMessage());
        });
    }


//    public void downloadFile(Context mContext, Document document, boolean original)
//    {
//        this.mContext = mContext;
//
//        if (fetch == null)
//        {
//            Constants.doLog("Fetch and setting listener");
//            FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(mContext)
//                    .setDownloadConcurrentLimit(3)
//                    .build();
//            fetch = Fetch.Impl.getInstance(fetchConfiguration);
//            fetch.addListener(fetchListener);
//        }
//
//
//
//
//        //@todo: Change back to https when online. Certificate is personal so error causes failed download.
//        String url = document.url.replace("https://","http://");
//        if (!original)
//        {
//            url = document.processurl.replace("https://","http://");
//        }
//        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        String file = f.getAbsolutePath() + "/" + document.filename_original;
//        if (!original)
//        {
//            file = f.getAbsolutePath() + "/" + document.filename_process;
//        }
//
//        final Request request = new Request(url, file);
//        request.setPriority(Priority.HIGH);
//        request.setNetworkType(NetworkType.ALL);
//
//        fetch.enqueue(request, updatedRequest -> {
//            Constants.doLog("Started downloading");
//        }, error -> {
//            if (mContext != null) showBasicAlert(mContext,"Error downloading: " + error.getThrowable().getLocalizedMessage());
//        });
//    }



    FetchListener fetchListener = new FetchListener() {

        @Override
        public void onWaitingNetwork(@NotNull Download download) {
            Constants.doLog("onWaitingNetwork");
        }

        @Override
        public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {
            Constants.doLog("onStarted");
        }

        @Override
        public void onResumed(@NotNull Download download) {

        }

        @Override
        public void onRemoved(@NotNull Download download) {

        }

        @Override
        public void onQueued(@NotNull Download download, boolean b) {

        }

        @Override
        public void onProgress(@NotNull Download download, long l, long l1) {
            Constants.doLog("onProgress");
        }

        @Override
        public void onPaused(@NotNull Download download) {

        }

        @Override
        public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {
            Constants.doLog("onError " + throwable.getLocalizedMessage());

            if (mContext != null) showBasicAlert(mContext,"Error downloading: " + throwable.getLocalizedMessage());
        }

        @Override
        public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

        }

        @Override
        public void onDeleted(@NotNull Download download) {

        }

        @Override
        public void onCompleted(@NotNull Download download) {
            if (mContext != null) showBasicAlert(mContext,"Download completed.\n\nIt is available in your downloads folder.");
        }

        @Override
        public void onCancelled(@NotNull Download download) {
            Constants.doLog("onCancelled");
        }

        @Override
        public void onAdded(@NotNull Download download) {

        }
    };
}
