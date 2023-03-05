package com.example.myhttp;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SearchView searchView = findViewById(R.id.searchView);
        WebView webView = findViewById(R.id.webView);
        LinkedBlockingQueue<String> toShow = new LinkedBlockingQueue<String>();

        searchView.setOnQueryTextListener(new Listener(toShow, webView));
    }

    private class Listener implements SearchView.OnQueryTextListener {

        private final LinkedBlockingQueue<String> toShow;
        private final WebView webView;

        public Listener(LinkedBlockingQueue<String> queue, WebView webView) {
            this.toShow = queue;
            this.webView = webView;
        }

        @Override
        public boolean onQueryTextSubmit(String s) {
            if (!s.endsWith(".mp3")) {
                UrlConnector urlConnector = new UrlConnector(s, toShow);
                urlConnector.start();

                runOnUiThread(() -> {
                    while (true) {
                        try {
                            String s1 = toShow.poll();
                            if (s1 != null) {
                                webView.loadData(s1, "text/plain", "utf-8");
                                break;
                            }
                        } catch (Exception ignored) {}
                    }
                });
            } else {
                downloadAudio(s);
            }

            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }

        private void downloadAudio(String url) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            String title = URLUtil.guessFileName(url, null, "audio/mpeg");
            request.setTitle(title);
            request.setDescription("Downloading...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);

            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);

            Toast.makeText(MainActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();
        }
    }

}