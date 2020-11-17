package com.downloader.my;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.CLIPBOARD_SERVICE;

public class DownloadFragment extends Fragment implements View.OnClickListener {

    Context context;
    ProgressBar pb;
    TextView textView;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public DownloadFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.download_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button download = view.findViewById(R.id.download_button);
        download.setOnClickListener(this);
        pb = view.findViewById(R.id.progressBar);
        textView = view.findViewById(R.id.status);
    }


    @Override
    public void onClick(View view) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        String link = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
        String command = "-o " + context.getExternalFilesDir(null).getAbsolutePath() + "/MyFiles/%(title)s.%(ext)s " + link;
        //String command = "-f bestaudio -o " + context.getExternalFilesDir(null).getAbsolutePath() + "/MyFiles/%(title)s.%(ext)s " + link;
        YoutubeDLRequest request = new YoutubeDLRequest(Collections.emptyList());
        String commandRegex = "\"([^\"]*)\"|(\\S+)";
        Matcher m = Pattern.compile(commandRegex).matcher(command);
        while (m.find()) {
            if (m.group(1) != null) {
                request.addOption(m.group(1));
            } else {
                request.addOption(m.group(2));
            }
        }
        pb.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        textView.setText("Downloading...");
        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().execute(request))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(youtubeDLResponse -> {
                    pb.setVisibility(View.INVISIBLE);
                    textView.setText("Done!");
                    FilesFragment.getInstance().updateList();
                }, e -> {
                    pb.setVisibility(View.INVISIBLE);
                    textView.setText("Failed");
                    if (BuildConfig.DEBUG) Log.e("TAG", "command failed", e);
                });
        compositeDisposable.add(disposable);

    }


}
