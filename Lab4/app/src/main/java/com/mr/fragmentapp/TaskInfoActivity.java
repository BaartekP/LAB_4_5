package com.mr.fragmentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class TaskInfoActivity extends AppCompatActivity {

    public static String DATA_CHANGED_KEY = "dataSetChanged";
    private boolean imgChanged;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        imgChanged = false;
    }

    public void setImgChanged(boolean val){imgChanged=val;}

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK,new Intent().putExtra(DATA_CHANGED_KEY,imgChanged));
        super.onBackPressed();
    }
}
