package com.mr_deadrim.ebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    Button Add;
    int fromPosition, toPosition;
    JSONArray jsonArray;
    JSONObject json,temp;
    EditText storageEditText,nameEditText;
    private static final int PICK_FILE_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Add = findViewById(R.id.add);
        recyclerView = findViewById(R.id.recyclerView);
        SharedPreferences prefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        try {
            jsonArray = new JSONArray(prefs.getString("key", "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                json = (JSONObject) jsonArray.get(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                View View = getLayoutInflater().inflate(R.layout.dialog, null);
                nameEditText = (EditText) View.findViewById(R.id.name);
                storageEditText = (EditText) View.findViewById(R.id.storage);
                Button cancel = (Button) View.findViewById(R.id.btn_cancel);
                Button save = (Button) View.findViewById(R.id.btn_okay);
                Button chooseFile =(Button) View.findViewById(R.id.chooseFile);
                
                alert.setView(View);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                chooseFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View view) {
                        openFileChooser();
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject json = new JSONObject();
                        try {
                            json.put("name", nameEditText.getText().toString());
                            json.put("storage", storageEditText.getText().toString());

                            JSONObject jsonPageObject = new JSONObject();
                            jsonPageObject.put("current_page", 0);
                            jsonPageObject.put("total_page",0);

                            json.put("page", jsonPageObject);
                            Toast.makeText(MainActivity.this, storageEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                            jsonArray.put(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        recyclerAdapter.notifyDataSetChanged();
                        Save();

                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        recyclerAdapter = new RecyclerAdapter(jsonArray);
        recyclerView.setAdapter(recyclerAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                try {
                    fromPosition = viewHolder.getAdapterPosition();
                    toPosition = target.getAdapterPosition();
                    temp = (JSONObject) jsonArray.get(fromPosition);
                    jsonArray.put(fromPosition, jsonArray.get(toPosition));
                    jsonArray.put(toPosition, temp);
                    recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                    Save();
                } catch (Exception e) {
                    Log.d("error", "Error spotted on move:" + e);
                    Toast.makeText(MainActivity.this, "Error spotted on move", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String filePath = FileUtils.getPathFromURI(this, uri);
                if (filePath != null) {
                    storageEditText.setText(filePath);
                } else {
                    Toast.makeText(this, "Failed to get file path", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void Save() {
        SharedPreferences prefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("key", jsonArray.toString());
        editor.apply();
        Log.d("array_data", "save array data:" + jsonArray.toString());
    }
}