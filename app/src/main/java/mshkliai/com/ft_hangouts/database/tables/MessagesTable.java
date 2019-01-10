package mshkliai.com.ft_hangouts.database.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import mshkliai.com.ft_hangouts.database.Database;
import mshkliai.com.ft_hangouts.database.DatabasePhotoConverter;
import mshkliai.com.ft_hangouts.listView.Model;

public class MessagesTable {
    private Database    database;

    private static final String[]   messagesFields
            = new String[] {"name", "body", "photo"};

    public MessagesTable(Context context) {
        database = new Database(context);
    }

    public void close() {
        database.close();
    }

    /********** Fields managers ****************/

    public void addMessage(String name, String body, Bitmap photo) {
        SQLiteDatabase  db = database.getWritableDatabase();
        ContentValues   content = new ContentValues();

        content.put("name", name);
        content.put("body", body);
        if (photo != null) {
            content.put("photo", DatabasePhotoConverter.bitmapToBytes(photo));
        }

        db.insert("messages", null, content);
        db.close();
    }

    /*********** Getters ************/

    public List<Model> getMessagesForList() {
        Cursor cursor = database.getReadableDatabase().query("messages", messagesFields,
                null, null, null, null, null);
        List<Model>   messages = new ArrayList<>();

        if (cursor != null) {
            String  name, body;
            Bitmap  photo;

            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex("name"));
                body = cursor.getString(cursor.getColumnIndex("body"));
                photo = DatabasePhotoConverter.bytesToBitmap(
                        cursor.getBlob(cursor.getColumnIndex("photo")) );

                messages.add( new Model(name, body, null, photo) );
            }

            cursor.close();
        }
        return messages;
    }

}
