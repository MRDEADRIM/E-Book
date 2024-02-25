package com.mr_deadrim.ebook;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {


    JSONArray jsonArray;


//    public RecyclerAdapter(List<String> moviesList) {
//        this.moviesList = moviesList;
//    }
    public RecyclerAdapter(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            String name = jsonObject.getString("name");
            String storage = jsonObject.getString("storage");
            holder.textView.setText(String.valueOf(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView, rowCountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            rowCountTextView = itemView.findViewById(R.id.rowCountTextView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
//                    jsonArray.remove(getAdapterPosition());
//                    notifyItemRemoved(getAdapterPosition());

                    Toast.makeText(view.getContext(), "long click", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        @Override
        public void onClick(View view) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(getAdapterPosition());
                String storagePath = jsonObject.getString("storage");
                File file = new File(storagePath);
                if (file.exists()) {
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, PdfActivity.class);
                    intent.putExtra("storage", storagePath);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(itemView.getContext(), "File not found", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
