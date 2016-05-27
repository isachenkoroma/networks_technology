package ru.zib.project;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class StartActivity extends AppCompatActivity {

    private StartActivity startActivity = this;
    private AsyncTask mT;
    private long startTime;
    private String LIVING_TIME = "living_time";
    private String START_TIME = "start_time";
    private String YES_CONNECTION = "yesConnection";
    private final int SLEEP_TIME = 2000;
    private HttpRequest download;
    private boolean yesConnection = true;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        textView = (TextView)startActivity.findViewById(R.id.helloTV);
        String json_url = getIntent().getStringExtra("json_url");

        if (getActionBar() != null) {
            getActionBar().hide();
        }

        if (savedInstanceState == null) {
            startTime = System.currentTimeMillis();
            download = new HttpRequest(this);
            download.execute(json_url);
            mT = new StartMain();
            mT.execute();
        } else {
            startTime = savedInstanceState.getLong(START_TIME);
            download = (HttpRequest) getLastCustomNonConfigurationInstance();
            yesConnection = savedInstanceState.getBoolean(YES_CONNECTION);
            if (!yesConnection) {
                textView.setText("Проверьте подключение к интернету.");
            } else if (savedInstanceState.getInt(LIVING_TIME) > SLEEP_TIME && download.getStatus().toString().equals("FINISHED")) {
                Log.d("connection", "intent from onCreate");
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
            } else {
                mT = new StartMain();
                mT.execute();
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LIVING_TIME, (int) (System.currentTimeMillis() - startTime));
        outState.putLong(START_TIME, startTime);
        outState.putBoolean(YES_CONNECTION, yesConnection);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return download;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mT != null) {
            mT.cancel(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    class StartMain extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            try {
                int sec = 0;
                while (sec < 5 || (!download.getStatus().toString().equals("FINISHED") && yesConnection)) {
                    sec += 1;
                    TimeUnit.SECONDS.sleep(1);
                    if (isCancelled()) {
                        return null;
                    }
                }
                if (yesConnection) {
                    Log.d("connection", "intent from StartMain");
                    Intent intent = new Intent(startActivity, MainActivity.class);
                    startActivity(intent);
                    startActivity.finish();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!yesConnection) {
                textView.setText("Проверьте подключение к интернету.");
            }
        }
    }


    private class HttpRequest extends AsyncTask<String, Integer, String> {
        private WeakReference<StartActivity> mListener;

        public HttpRequest(StartActivity listener) {
            mListener = new WeakReference<StartActivity>(listener);
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                InputStream inputStream = openFileInput("technology");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (params != null && params.length > 0) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = null;
                    try {
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(5000);
                        connection.setRequestMethod("GET");
                        InputStream is = new BufferedInputStream(connection.getInputStream());
                        String html = Utils.readInputStream(is);
                        StringBuilder builder = new StringBuilder();
                        builder.append(html);
                        is.close();

                        try {
                            StartActivity stA = mListener.get();
                            // отрываем поток для записи
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stA.openFileOutput("technology", stA.MODE_PRIVATE)));
                            // пишем данные
                            bw.write(builder.toString());
                            // закрываем поток
                            bw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return builder.toString();
                    } catch (UnknownHostException hostEx) {
                        Log.d("connection", "hostEx");
                        yesConnection = false;
                        hostEx.printStackTrace();
                    } catch (SocketTimeoutException socketEx) {
                        Log.d("connection", "time out");
                        yesConnection = false;
                        socketEx.printStackTrace();
                    } catch (IOException ex) {
                        Log.d("connection", "IOException");
                        ex.printStackTrace();
                    }
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
        }

    }
}
