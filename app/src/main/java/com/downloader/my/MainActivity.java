package com.downloader.my;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public static FileObserver observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Initialize youtube-dl */
        try {
            YoutubeDL.getInstance().init(getApplication());
        } catch (YoutubeDLException e) {
            Log.e("MainActivityTag", "failed to initialize youtubedl-android", e);
        }

        /* setup ViewPager and TabLayout with Fragments */
        TabLayout tabLayout = findViewById(R.id.tabs);
        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this);
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        tabLayout.getTabAt(MyFragmentAdapter.DOWNLOAD_TAB).setIcon(R.drawable.downloads_icon);
        tabLayout.getTabAt(MyFragmentAdapter.FILES_TAB).setIcon(R.drawable.audio_icon);

        /* Update youtube-dl */
        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().updateYoutubeDL(getApplication()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(status -> {
                    switch (status) {
                        case DONE:
                            Toast.makeText(MainActivity.this, "update successful", Toast.LENGTH_LONG).show();
                            break;
                        case ALREADY_UP_TO_DATE:
                            Log.d("MainActivityTag", "youtube-dl already up to date");
                            break;
                    }

                }, e -> {
                    if (BuildConfig.DEBUG)
                        Toast.makeText(MainActivity.this, "update failed", Toast.LENGTH_LONG).show();


                });
        compositeDisposable.add(disposable);

        /* check permissions */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            }
        }
        
        /* Create directory if it doesn't exist for FileObserver */

        File path = new File(this.getExternalFilesDir(null).getAbsolutePath() + "/MyFiles");
        if (!path.exists()){
            path.mkdirs();
        }

        /* Start FileObserver, update the FilesFragment adapter when file deleted or created */

        /*
         * youtube-dl downloads the files as parts e.g. file.part.mp4 then merges them, so the code below uses FileObserver.MOVED_TO because
         * I want to know when the file is renamed (aka mv file.part.mp4 file.mp4) to file.mp4
         */
        observer = new FileObserver(new File(this.getExternalFilesDir(null).getAbsolutePath() + "/MyFiles"), FileObserver.ALL_EVENTS) { // set up a file observer to watch this directory on sd card
            @Override
            public void onEvent(int event, String file) {
                if (event == FileObserver.MOVED_TO || event == FileObserver.DELETE) {
                    ((FilesFragment) getSupportFragmentManager().getFragments().get(MyFragmentAdapter.FILES_TAB)).updateNames();
                }
            }
        };
        observer.startWatching();
    }


}
