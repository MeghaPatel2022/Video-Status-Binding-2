package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.ActivityPrivacyPolicyBinding;

public class PrivacyPolicy extends AppCompatActivity {

    ActivityPrivacyPolicyBinding policyBinding;
    MyClickHandlers myClickHandlers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        policyBinding = DataBindingUtil.setContentView(PrivacyPolicy.this, R.layout.activity_privacy_policy);
        myClickHandlers = new MyClickHandlers(PrivacyPolicy.this);
        policyBinding.setOnClick(myClickHandlers);

        policyBinding.ivWebView.setWebViewClient(new MyWebViewClient());
        openURL();

    }

    private void openURL() {
        policyBinding.ivWebView.loadUrl(getResources().getString(R.string.privacy_policy));
        policyBinding.ivWebView.requestFocus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public class MyClickHandlers {
        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onBackBtnClicked(View view) {
            onBackPressed();
        }
    }
}