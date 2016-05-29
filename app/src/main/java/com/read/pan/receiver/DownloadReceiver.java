package com.read.pan.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.yamin.reader.database.DbDataOperation;
import com.yamin.reader.database.DbTags;
import com.yamin.reader.model.Book;
import com.yamin.reader.utils.ToolUtils;

import java.io.File;

/**
 * Created by pan on 2016/5/29.
 */
public class DownloadReceiver extends BroadcastReceiver {
    private ContentResolver resolver;
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sPreferences = context.getSharedPreferences("downloadcomplete", 0);
        long mDownloadId = sPreferences.getLong("refernece", 0);
        String bookName=sPreferences.getString("bookName",null);
        String bookPath=sPreferences.getString("bookPath",null);
        resolver=context.getContentResolver();
        String bookSize=null;
        if(bookPath!=null){
            File file=new File(bookPath);
            if(file.isFile()){
                bookSize= ToolUtils.FormetFileSize(file.length());
            }
        }
        long lastDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (lastDownloadId == mDownloadId) {
            Book book = new Book();
            book.setBookName(bookName);
            book.setBookPath(bookPath);
            book.setBookSize(bookSize);
            Book b = DbDataOperation.queryBook(resolver, DbTags.FIELD_BOOK_NAME,bookName);
            if (b == null) {
                DbDataOperation.insertToBookInfo(resolver, book);
            }
        }
    }
}
