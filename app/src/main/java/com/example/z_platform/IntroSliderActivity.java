package com.example.z_platform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class IntroSliderActivity extends AppCompatActivity {

    int[] sliderLayouts = {R.layout.slider1, R.layout.slider2, R.layout.slider3};
    ViewPager viewPager;
    Button btnNext, btnPrev;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2, radioButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);

        viewPager = findViewById(R.id.ViewPagerIntro);
        btnNext = findViewById(R.id.introNext);
        btnPrev = findViewById(R.id.introPrevious);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radioBtn1);
        radioButton2 = findViewById(R.id.radioBtn2);
        radioButton3 = findViewById(R.id.radioBtn3);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isSeen = sharedPreferences.getBoolean("isSeenSlider", false);

        SliderAdapter sliderAdapter = new SliderAdapter();

        viewPager.setAdapter(sliderAdapter);

        if (isSeen){
            Intent intent = new Intent(IntroSliderActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    radioButton1.setChecked(true);
                    btnNext.setText("Next");
                    btnNext.setOnClickListener(view -> {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    });
                } else if (position == 1) {
                    radioButton2.setChecked(true);
                    btnNext.setText("Next");
                    btnNext.setOnClickListener(view -> {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    });
                } else if (position == sliderLayouts.length - 1) {
                    radioButton3.setChecked(true);
                    btnNext.setText("Let's start!");
                    btnNext.setOnClickListener(view -> {
                        editor.putBoolean("isSeenSlider", true).apply();
                        Intent intent = new Intent(IntroSliderActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioBtn1) {
                    viewPager.setCurrentItem(0);
                } else if (i == R.id.radioBtn2) {
                    viewPager.setCurrentItem(1);
                } else if (i == R.id.radioBtn3){
                    viewPager.setCurrentItem(2);
                }
            }
        });
        changeSystemColor();
    }

    public class SliderAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public SliderAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(sliderLayouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return sliderLayouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public void changeSystemColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.dark_primary));
            window.setNavigationBarColor(getColor(R.color.dark_primary));
        }
    }
}