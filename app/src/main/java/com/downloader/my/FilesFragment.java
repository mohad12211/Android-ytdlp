package com.downloader.my;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class FilesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> names = new ArrayList<>();
    private Context context;
    static  FilesFragment instance  =  null;
    public FilesFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.files_fragment, container, false);
        setNames();
        recyclerView = view.findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyListAdapter(names,context);
        recyclerView.setAdapter(mAdapter);
        Log.d("MyTag", context.getExternalFilesDir(null).getAbsolutePath());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            instance   = this;
    }

    public static FilesFragment getInstance(){
        return instance;
    }


    public void updateList(){
        setNames();
        mAdapter = new MyListAdapter(names,context);
        recyclerView.setAdapter(mAdapter);
    }

    public void setNames() {
        ArrayList<String> names = new ArrayList<>();
        File directory = new File(context.getExternalFilesDir(null).getAbsolutePath() + "/MyFiles");
        File[] files = directory.listFiles();
        if (files == null) {
            this.names = names;
            return;
        }
        for (int i = 0; i < files.length; i++) {
            names.add(files[i].getName());
        }
        this.names = names;
    }


}
