package com.srtianxia.seekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.srtianxia.separateseekbar.SeparateSeekBar;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SeparateSeekBar mSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSeekBar = (SeparateSeekBar) findViewById(R.id.seekBar);
        final List<String> tags = new ArrayList<>();
        tags.add("最慢");
        tags.add("稍慢");
        tags.add("中等");
        tags.add("较快");
        tags.add("最快");

        mSeekBar.setTags(tags);
        mSeekBar.setOnItemClickListener(new SeparateSeekBar.OnItemClickListener() {
            @Override public void onClick(int position) {
                ToastUtil.show(MainActivity.this, tags.get(position), true);
            }
        });
    }
}
