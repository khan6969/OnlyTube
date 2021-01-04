package com.example.onlytube;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class FacebookActivity extends AppCompatActivity {

    private WebView webo;
    private static String URL = "https://www.facebook.com/login/";

    public static final String FOLDER_NAME = "/FBDownloader/";
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button streamButton, downloadButton, cancelButton;
    private ProgressBar progress;
    String fileN = null;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    boolean result;
    String urlString;
    Dialog main_dialog, downloadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_facebook);


        progress = findViewById(R.id.progressBar);

        progress.setVisibility(View.INVISIBLE);
        result = checkPermission();
        if (result) {
            checkFolder();
        }
        webo = findViewById(R.id.webViewFb);
        webo.getSettings().setJavaScriptEnabled(true);
        webo.addJavascriptInterface(this, "FBDownloader");
        webo.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress.setVisibility(View.INVISIBLE);
                FacebookActivity.this.setValue(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });
        webo.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                FacebookActivity.this.webo.loadUrl("javascript:(function() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');}}})()");
                Log.e("WEBVIEWFIN", url);
                progress.setVisibility(View.GONE);
//                MainActivity.this.progress.setProgress(100);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                FacebookActivity.this.webo.loadUrl("javascript:(function prepareVideo() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;console.log(i);var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');}}})()");
                FacebookActivity.this.webo.loadUrl("javascript:( window.onload=prepareVideo;)()");
            }
        });
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this);
        }
        cookieManager.setAcceptCookie(true);
        webo.loadUrl(URL);
    }

    private void setValue(int newProgress) {

        this.progress.setProgress(newProgress);
        if (newProgress >= 100) // code to be added
            this.progress.setVisibility(View.GONE);
    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {

        String fileN = null;
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        private NumberProgressBar bnp;

        @Override
        protected String doInBackground(String... strings) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                java.net.URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();
                fileN = "FbDownloader_" + UUID.randomUUID().toString().substring(0, 10) + ".mp4";
                File filename = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + FOLDER_NAME, fileN);
                output = new FileOutputStream(filename);

                byte[] data = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            LayoutInflater dialogLayout = LayoutInflater.from(FacebookActivity.this);
            View DialogView = dialogLayout.inflate(R.layout.progress_dialog, null);
            downloadDialog = new Dialog(FacebookActivity.this, R.style.CustomAlertDialog);
            downloadDialog.setContentView(DialogView);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(downloadDialog.getWindow().getAttributes());
            lp.width = (getResources().getDisplayMetrics().widthPixels);
            lp.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);
            downloadDialog.getWindow().setAttributes(lp);

            final Button cancel = DialogView.findViewById(R.id.cancel_btn);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //stopping the Asynctask
                    cancel(true);
                    downloadDialog.dismiss();

                }
            });


            downloadDialog.setCancelable(false);
            downloadDialog.setCanceledOnTouchOutside(false);
            bnp = DialogView.findViewById(R.id.number_progress_bar);
            bnp.setProgress(0);
            bnp.setMax(100);
            downloadDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            bnp.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mWakeLock.release();
            downloadDialog.dismiss();
            if (s != null)
                Toast.makeText(context, "Download error: " + s, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            MediaScannerConnection.scanFile(FacebookActivity.this,
                    new String[]{Environment.getExternalStorageDirectory().getAbsolutePath() +
                            FOLDER_NAME + fileN}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String newpath, Uri newuri) {
                            Log.i("ExternalStorage", "Scanned " + newpath + ":");
                            Log.i("ExternalStorage", "-> uri=" + newuri);
                        }
                    });

        }
    }

    @JavascriptInterface
    public void processVideo(String str, String str2) {
        Log.e("WEBVIEWJS", "RUN");
        Log.e("WEBVIEWJS", str);
        Bundle args = new Bundle();
        args.putString("vid_data", str);
        urlString = str;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // put your code to show dialog here.
                LayoutInflater dialogLayout = LayoutInflater.from(FacebookActivity.this);
                View DialogView = dialogLayout.inflate(R.layout.dialog_download, null);
                main_dialog = new Dialog(FacebookActivity.this, R.style.MyDialogTheme);
                main_dialog.setContentView(DialogView);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(main_dialog.getWindow().getAttributes());
                lp.width = (getResources().getDisplayMetrics().widthPixels);
                lp.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);
                main_dialog.getWindow().setAttributes(lp);
                streamButton = DialogView.findViewById(R.id.streamButton);
                downloadButton = DialogView.findViewById(R.id.downloadButton);
                cancelButton = DialogView.findViewById(R.id.cancelButton);
                main_dialog.setCancelable(false);
                main_dialog.setCanceledOnTouchOutside(false);
                main_dialog.show();
                streamButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(FacebookActivity.this, "Streaming", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(FacebookActivity.this, VideoPlayer.class);
                        intent.putExtra("video_url", urlString);
                        startActivity(intent);
                        main_dialog.dismiss();
                    }
                });
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(FacebookActivity.this, "Downloading", Toast.LENGTH_SHORT).show();
                        //download Method

                        newDownload(urlString);
                        main_dialog.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        main_dialog.dismiss();
                    }
                });
            }
        });
    }

    private void newDownload(String urlString) {
        final DownloadTask downloadTask = new DownloadTask(FacebookActivity.this);
        downloadTask.execute(urlString);

    }

    private void checkFolder() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FBDownloader/";
        File dir = new File(path);
        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = dir.mkdir();
        }
        if (isDirectoryCreated) {
            // do something\
            Log.d("Folder", "Already Created");
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(FacebookActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(FacebookActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(FacebookActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(FacebookActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(FacebookActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }

    }


   }

