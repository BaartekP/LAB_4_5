package com.mr.fragmentapp;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mr.fragmentapp.tasks.TaskListContent;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaskInfoFragment extends Fragment implements View.OnClickListener{


    public TaskInfoFragment() {
        // Required empty public constructor
    }

    public static final int REQUEST_IMAGE_CAPTURE =1;
    private String mCurrentPhotoPath;
    private TaskListContent.Task mDisplayTask;

    private File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "item_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public void displaytask(TaskListContent.Task task) {
        FragmentActivity activity = getActivity();

        (activity.findViewById(R.id.displayFragment)).setVisibility(View.VISIBLE);

        TextView taskInfoTitle = activity.findViewById(R.id.taskInfoTitle);
        TextView taskInfoDescription = activity.findViewById(R.id.taskInfoDescription);
        final ImageView taskInfoImage = activity.findViewById(R.id.taskInfoImage);

        taskInfoTitle.setText(task.title);
        taskInfoDescription.setText(task.details);

        if (task.picPath != null && !task.picPath.isEmpty()) {
            if (task.picPath.contains("drawable")) {
                Drawable taskDrawable;
                switch (task.picPath) {
                    case "drawable 1":
                        taskDrawable = activity.getResources().getDrawable(R.drawable.circle_drawable_green);
                        break;
                    case "drawable 2":
                        taskDrawable = activity.getResources().getDrawable(R.drawable.circle_drawable_orange);
                        break;
                    case "drawable 3":
                        taskDrawable = activity.getResources().getDrawable(R.drawable.circle_drawable_red);
                        break;
                    default:
                        taskDrawable = activity.getResources().getDrawable(R.drawable.circle_drawable_green);
                }
                taskInfoImage.setImageDrawable(taskDrawable);
            }else {
                Handler handler = new Handler();

                taskInfoImage.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        taskInfoImage.setVisibility(View.VISIBLE);
                        Bitmap cameraImage = PicUtils.decodePic(mDisplayTask.picPath,taskInfoImage.getWidth(),taskInfoImage.getHeight());
                        taskInfoImage.setImageBitmap(cameraImage);
                    }
                },200);

            }

        } else {
            taskInfoImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.circle_drawable_green));
        }

        mDisplayTask = task;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        Intent intent = getActivity().getIntent();
        if (intent != null){
            TaskListContent.Task receivedTask = intent.getParcelableExtra(MainActivity.taskExtra);
            if(receivedTask != null){
                activity.findViewById(R.id.displayFragment).setVisibility(View.INVISIBLE);
                activity.findViewById(R.id.taskInfoImage).setOnClickListener(this);
                displaytask(receivedTask);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            FragmentActivity holdingActivity = getActivity();
            if(holdingActivity != null){
                ImageView taskImage = holdingActivity.findViewById(R.id.taskInfoImage);
                Bitmap cameraImage = PicUtils.decodePic(mCurrentPhotoPath,taskImage.getWidth(),taskImage.getHeight());
                taskImage.setImageBitmap(cameraImage);
                mDisplayTask.setPicPath(mCurrentPhotoPath);

                TaskListContent.Task task= TaskListContent.ITEM_MAP.get(mDisplayTask.id);
                if(task != null){
                    task.setPicPath(mCurrentPhotoPath);
                }

                if(holdingActivity instanceof MainActivity){
                    ((TaskFragment) holdingActivity.getSupportFragmentManager().findFragmentById(R.id.taskFragment)).notifyDataChange();
                }else if(holdingActivity instanceof TaskInfoActivity){
                    ((TaskInfoActivity) holdingActivity).setImgChanged(true);
                }

            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getActivity().getPackageManager())!= null){
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }catch (IOException ex){}

            if(photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(getActivity(),getString(R.string.myFileprovider),photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
            }

        }
    }
}
