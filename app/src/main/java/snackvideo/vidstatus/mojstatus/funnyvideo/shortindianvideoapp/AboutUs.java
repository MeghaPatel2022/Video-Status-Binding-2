package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.ActivityAboutUsBinding;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class AboutUs extends AppCompatActivity {

    ActivityAboutUsBinding aboutUsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aboutUsBinding = DataBindingUtil.setContentView(AboutUs.this, R.layout.activity_about_us);

        Glide
                .with(AboutUs.this)
                .load(R.drawable.logo)
                .transition(withCrossFade())
                .transition(new DrawableTransitionOptions().crossFade(700))
                .into(aboutUsBinding.imgLogo);

        aboutUsBinding.setVersionCode("Version: " + BuildConfig.VERSION_NAME);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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