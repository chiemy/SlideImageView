package com.chiemy.slideimageview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SlideImageView mSlideImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int initialCapacity = 35;
        List<Integer> list = new ArrayList<>(initialCapacity);
        for (int i = 0; i < initialCapacity + 1; i++) {
            int id = getResources().getIdentifier("img_" + i, "mipmap", getPackageName());
            if (id != 0) {
                list.add(id);
            }
        }
        int size = list.size();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = list.get(i);
        }

        mSlideImageView = findViewById(R.id.iv);
        final SeekBar seekBar = findViewById(R.id.seek_bar);
        final int steps = seekBar.getMax() / (size - 1);

        mSlideImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });
        mSlideImageView.setImageChangeListener(new SlideImageView.ImageChangeListener() {
            @Override
            public void onImageChange(int index, int drawable, boolean fromUser) {
                seekBar.setProgress(index * steps);
            }
        });
        mSlideImageView.startAutoChange(100, false);
        mSlideImageView.setImageChangeFactor(2);
        mSlideImageView.setImages(arr);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int index = progress / steps;
                    mSlideImageView.setShowIndex(index);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Switch autoSwitch = findViewById(R.id.switch_auto);
        Switch reverseSwitch = findViewById(R.id.switch_reverse);
        autoSwitch.setChecked(mSlideImageView.isAutoChange());
        reverseSwitch.setChecked(mSlideImageView.isAutoChangeReverse());
        autoSwitch.setOnCheckedChangeListener(mCheckedChangeListener);
        reverseSwitch.setOnCheckedChangeListener(mCheckedChangeListener);
    }

    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.switch_auto:
                    if (isChecked) {
                        mSlideImageView.startAutoChange(100, mSlideImageView.isAutoChangeReverse());
                    } else {
                        mSlideImageView.stopAutoChange();
                    }
                    break;
                case R.id.switch_reverse:
                    mSlideImageView.setAutoChangeReverse(isChecked);
                    break;
            }
        }
    };
}
