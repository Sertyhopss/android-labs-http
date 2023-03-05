package com.example.myhttp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

public class UrlConnector extends Thread {
    LinkedBlockingQueue<String> container;
    String url;

    public UrlConnector(String url, LinkedBlockingQueue<String> container) {
        this.url = url;
        this.container = container;
    }

    public void run() {
        String html = getHtml(this.url);
        try {
            this.container.put(html);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getHtml(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "SampleHttp");

            return parseInputStream(conn);

        } catch (IOException e) {
            return "nothing";
        }
    }

    private static String parseInputStream(HttpURLConnection connection) throws IOException {
        InputStreamReader isr = new InputStreamReader(connection.getInputStream());
        BufferedReader in = new BufferedReader(isr);
        String line;
        StringBuilder responseBody = new StringBuilder();

        while ((line = in.readLine()) != null) {
            responseBody.append(line);
        }
        return responseBody.toString();
    }

}
