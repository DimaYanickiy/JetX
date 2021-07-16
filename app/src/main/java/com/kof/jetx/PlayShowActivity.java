package com.kof.jetx;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;

public class PlayShowActivity extends AppCompatActivity implements SaveSettingsInterface{

    ValueCallback<Uri[]> callback;
    String photoPath;
    WebView play;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        play = (WebView)findViewById(R.id.play);
        setSettings1();
        setSettings2();
        setSettings3();
        setSettings4();
        setCoocies();

        setClient();
        setChrome();

        play.loadUrl(getDot());
    }

    public void setClient(){
        play.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mailto:")) {
                    Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                    startActivity(i);
                    return true;
                } else if (url.startsWith("tg:") || url.startsWith("https://t.me") || url.startsWith("https://telegram.me")) {
                    try {
                        WebView.HitTestResult result = view.getHitTestResult();
                        String data = result.getExtra();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                        view.getContext().startActivity(intent);
                    } catch (Exception ex) {
                    }
                    return true;
                } else {
                    return false;
                }
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return overrideUrl(view, request.getUrl().toString());
            }

            private boolean overrideUrl(WebView view, String toString) {
                return overrideUrl(view, toString);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (getWhatFirst() == "game") {
                    setDot(url);
                    setWhatFirst("site");
                    CookieManager.getInstance().flush();
                }
                CookieManager.getInstance().flush();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });
    }

    public void setChrome(){
        play.setWebChromeClient(new WebChromeClient() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void checkPermission() {
                ActivityCompat.requestPermissions(
                        PlayShowActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                        1);
            }

            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                int permissionStatus = ContextCompat.checkSelfPermission(PlayShowActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    if (callback != null) {
                        callback.onReceiveValue(null);
                    }
                    callback = filePathCallback;
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", photoPath);
                        } catch (IOException ex) {
                        }
                        if (photoFile != null) {
                            photoPath = "file:" + photoFile.getAbsolutePath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile));
                        } else {
                            takePictureIntent = null;
                        }
                    }
                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType("image/*");
                    Intent[] intentArray;
                    if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else {
                        intentArray = new Intent[0];
                    }
                    Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                    chooser.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooser.putExtra(Intent.EXTRA_TITLE, "Photo");
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooser, 1);
                    return true;
                } else
                    checkPermission();
                return false;
            }

            private File createImageFile() throws IOException {
                File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DirectoryNameHere");
                if (!imageStorageDir.exists())
                    imageStorageDir.mkdirs();
                imageStorageDir = new File(imageStorageDir + File.separator + "Photo_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                return imageStorageDir;
            }
        });
    }

    public void setSettings1(){
        play.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        play.requestFocus(View.FOCUS_DOWN);
        play.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        play.getSettings().setUserAgentString(play.getSettings().getUserAgentString());
        play.getSettings().setAllowFileAccess(true);
    }
    public void setSettings2(){
        play.getSettings().setJavaScriptEnabled(true);
        play.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        play.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        play.getSettings().setAppCacheEnabled(true);
    }
    public void setSettings3(){
        play.getSettings().setDomStorageEnabled(true);
        play.getSettings().setDatabaseEnabled(true);
        play.getSettings().setSupportZoom(false);
        play.getSettings().setAllowFileAccess(true);
    }
    public void setSettings4(){
        play.getSettings().setAllowContentAccess(true);
        play.getSettings().setLoadWithOverviewMode(true);
        play.getSettings().setUseWideViewPort(true);
        play.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        play.getSettings().setPluginState(WebSettings.PluginState.ON);
        play.getSettings().setSavePassword(true);
    }
    public void setCoocies(){
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.acceptCookie();
        cookieManager.setAcceptThirdPartyCookies(play, true);
        cookieManager.flush();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieManager.getInstance().flush();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieManager.getInstance().flush();
    }

    @Override
    public void onBackPressed() {
        if(play.canGoBack()){
            play.goBack();
        } else {
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1 || callback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null || data.getData() == null) {
                if (photoPath != null) {
                    results = new Uri[]{Uri.parse(photoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }
        callback.onReceiveValue(results);
        callback = null;
    }


    @Override
    public void initSp() {
        settings = getSharedPreferences("Play Settings", MODE_PRIVATE);
    }

    @Override
    public String getDot() {
        return settings.getString("pref1", "");
    }

    @Override
    public void setDot(String dot) {
        settings.edit().putString("pref1", dot).apply();
    }

    @Override
    public String getWhatFirst() {
        return settings.getString("pref2", "game");
    }

    @Override
    public void setWhatFirst(String whatFirst) {
        settings.edit().putString("pref2", whatFirst).apply();
    }

    @Override
    public boolean isFirstRun() {
        return settings.getBoolean("pref3", true);
    }

    @Override
    public void setFirstRun(boolean firstRun) {
        settings.edit().putBoolean("pref3", firstRun).apply();
    }

    @Override
    public String getFirstFlyer() {
        return settings.getString("pref4", "first");
    }

    @Override
    public void setFirstFlyer(String sfflyer) {
        settings.edit().putString("pref4", sfflyer).apply();
    }
}