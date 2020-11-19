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

    public static final int DOWNLOAD_TAB = 0;
    public static final int FILES_TAB = 1;
    public static final int TAB_COUNT = 2;

    public MyFragmentAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
        super(fm, behavior);
        this.context = context;
        downloadFragment = new DownloadFragment(context);
        filesFragment = new FilesFragment(context);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case DOWNLOAD_TAB:
                return downloadFragment;
            case FILES_TAB:
                return filesFragment;
        }
        return null;

    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case DOWNLOAD_TAB:
                return "Download";
            case FILES_TAB:
                return "Files";
        }
        return super.getPageTitle(position);
    }

}
