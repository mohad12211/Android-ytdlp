package com.downloader.my;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyViewHolder> {
    private ArrayList<String> names;
    Context context;
    public MyListAdapter(ArrayList<String> names, Context context) {
        this.names = names;
        this.context=context;
    }


    RecyclerView mRecyclerView;


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public MyListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = mRecyclerView.getChildLayoutPosition(view);
                String item = names.get(itemPosition);
                Uri uri  = Uri.parse(context.getExternalFilesDir(null).getAbsolutePath() + "/MyFiles/"+item);

                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                intent.setDataAndType(uri, "video/*");
                context.startActivity(intent);
                /*FragmentTransaction fragmentTransaction =  ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                AudioPlayer player = new AudioPlayer();
                player.setName(item);
                player.show(fragmentTransaction,null);*/

            }
        });
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(names.get(position).substring(0, names.get(position).lastIndexOf('.')));
        Uri uri = Uri.parse(context.getExternalFilesDir(null).getAbsolutePath() + "/MyFiles/"+names.get(position));
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        //mmr.setDataSource(context.getApplicationContext(),uri);
        mmr.setDataSource(String.valueOf(uri));
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int millSecond = Integer.parseInt(durationStr);
        String duration = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millSecond),
                TimeUnit.MILLISECONDS.toSeconds(millSecond) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millSecond))
        );
        holder.duration.setText("Duration : "+duration);
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Are you sure you want to delete the file?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                File file = new File(context.getExternalFilesDir(null).getAbsolutePath() + "/MyFiles/"+names.get(position));
                                file.delete();
                                dialog.cancel();
                                names.remove(position);
                                notifyItemRemoved(position);
                            }
                        });

                builder1.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return names.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageButton imageButton;
        public TextView duration;
        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.name);
            imageButton = v.findViewById(R.id.imageButton);
            duration = v.findViewById(R.id.duration);
        }
    }
}
