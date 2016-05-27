package ru.zib.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by artembochkarev on 23/05/16.
 */
public class PreStartActivity extends AppCompatActivity {

    private PreStartActivity prestartActivity = this;
    private EditText json_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestart);

        json_url = (EditText) prestartActivity.findViewById(R.id.json_url);

        if (getActionBar() != null) {
            getActionBar().hide();
        }
    }

    public void SendReference(View view) {
        Intent intent = new Intent(this, StartActivity.class);
        intent.putExtra("json_url", json_url.getText().toString());
        startActivity(intent);
    }

    public void PreView(View view) {
        Intent intent = new Intent(this, StartActivity.class);
        intent.putExtra("json_url", "https://raw.githubusercontent.com/isachenkoroma/networks_technology/master/technology.js");
        startActivity(intent);
    }

}
