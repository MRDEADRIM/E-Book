package com.mr_deadrim.ebook;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;

public class PdfActivity extends AppCompatActivity implements OnPageChangeListener {

    private SharedPreferences pdfReader;
    public String storage;

    public static void startActivity(Context context, String jsonString) {
      Intent intent = new Intent(context, PdfActivity.class);
      intent.putExtra("jsonObject", jsonString);
      context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        Intent intent = getIntent();
        storage = intent.getStringExtra("storage");
        File file = new File(storage);
        pdfReader = this.getSharedPreferences("PDFReader", Context.MODE_PRIVATE);
        PDFView pdfView = (PDFView) findViewById(R.id.pdfbook);
        pdfView.fromFile(file)
                .defaultPage(pdfReader.getInt("pages", 0))
                .onPageChange(this)
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        SharedPreferences.Editor edit = pdfReader.edit();
        edit.putInt("pages",page);
        edit.apply();

        Toast.makeText(this, "page:"+page, Toast.LENGTH_SHORT).show();

    }
}