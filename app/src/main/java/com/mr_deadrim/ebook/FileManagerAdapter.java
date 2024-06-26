package com.mr_deadrim.ebook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlinx.coroutines.CoroutineScope;

public class FileManagerAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final ArrayList<String> mFileList;
    private final Map<String, Bitmap> mBitmapCache;
    private final ExecutorService mExecutor;

    public FileManagerAdapter(Context context, ArrayList<String> fileList) {
        super(context, R.layout.file_list_layout, fileList);
        this.mContext = context;
        this.mFileList = fileList;
        this.mBitmapCache = new HashMap<>();
        this.mExecutor = Executors.newFixedThreadPool(5);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.file_list_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.item_image);
            viewHolder.textView = convertView.findViewById(R.id.item_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String fileName = mFileList.get(position);
        File file = new File(fileName);

        viewHolder.imageView.setImageResource(android.R.drawable.stat_notify_sync);

        if (fileName.toLowerCase().endsWith(".pdf")) {
            viewHolder.imageView.setImageResource(R.drawable.pdf);
        }
        if (file.isDirectory()) {
            viewHolder.imageView.setImageResource(R.drawable.folder);
        }
        if (fileName.toLowerCase().endsWith(".png") || fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
            if (mBitmapCache.containsKey(fileName)) {
                viewHolder.imageView.setImageBitmap(mBitmapCache.get(fileName));
            } else {
                BitmapWorkerTask task = new BitmapWorkerTask(viewHolder.imageView, fileName);
                mExecutor.execute(task);
            }
        }

        viewHolder.textView.setText(file.getName());

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    private class BitmapWorkerTask implements Runnable {
        private final WeakReference<ImageView> imageViewReference;
        private final String filePath;

        public BitmapWorkerTask(ImageView imageView, String filePath) {
            this.imageViewReference = new WeakReference<>(imageView);
            this.filePath = filePath;
        }

        @Override
        public void run() {
            final Bitmap bitmap = decodeSampledBitmapFromFile(filePath, 100, 100); // Adjust dimensions as needed
            if (bitmap != null && imageViewReference.get() != null) {
                mBitmapCache.put(filePath, bitmap);
                ((AppCompatActivity) mContext).runOnUiThread(() -> {
                    ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        }
    }

    private Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}