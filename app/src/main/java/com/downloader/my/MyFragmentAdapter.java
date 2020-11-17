package com.downloader.my;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MyFragmentAdapter extends FragmentStatePagerAdapter {


    Context context;
    DownloadFragment downloadFragment;
    FilesFragment filesFragment;

    public MyFragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                return downloadFragment;
            case 1:
                return filesFragment;


        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void setContext(Context context) {
        this.context = context;
        downloadFragment = new DownloadFragment(context);
        filesFragment = new FilesFragment(context);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Download";
            case 1:
                return "Files";
        }
        return super.getPageTitle(position);
    }

}
