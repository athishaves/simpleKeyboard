package com.athishworks.keyboard;

import androidx.appcompat.app.AppCompatActivity;

        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.text.method.LinkMovementMethod;
        import android.util.Log;
        import android.view.View;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.Button;
        import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView) findViewById(R.id.github)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onClick(View v) {
        Log.i("Main Keyboard", "Clicked " + v.getId());
        switch (v.getId()) {
            case R.id.enable:
                this.startActivityForResult(new Intent("android.settings.INPUT_METHOD_SETTINGS"),1111);
                break;
            case R.id.input:
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
                break;
        }
    }
}