package mshkliai.com.ft_hangouts.database.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mshkliai.com.ft_hangouts.listView.Model;
import mshkliai.com.ft_hangouts.R;
import mshkliai.com.ft_hangouts.database.Database;
import mshkliai.com.ft_hangouts.database.DatabasePhotoConverter;

public class ContactsTable {
    private Database                database;
    private Context                 context;

    private static final String[]   usersFields
            = new String[] {"id", "name", "mobile_num", "mail", "photo"};

    public ContactsTable(Context context) {
        database = new Database(context);
        this.context = context;
    }

    public void close() {
        database.close();
    }

    /********* Table Managers *************/

    public boolean addContact(String name, String number, String mail, Bitmap bitmap) {
        if ( checkUniquenessNumber(number) ) {
            SQLiteDatabase db = database.getWritableDatabase();
            ContentValues content = new ContentValues();

            content.put("id", sizeDatabase());
            content.put("name", name);
            content.put("mobile_num", number);
            content.put("mail", mail);

            if (bitmap != null) {
                content.put( "photo", DatabasePhotoConverter.bitmapToBytes(bitmap) );
            }

            db.insert("users", null, content);
            db.close();

            return true;
        }
        return false;
    }

    public void updateFields(int id, String name, String mobNum, String mail) {
        SQLiteDatabase db = database.getReadableDatabase();
        ContentValues   content = new ContentValues();

        content.put("name", name);
        content.put("mobile_num", mobNum);
        content.put("mail", mail);

        db.update("users", content, "id = "  + id, null);
        db.close();
    }

    public void updateImage(int id, Bitmap bitmap) {
        SQLiteDatabase  db = database.getWritableDatabase();

        byte[] photo = DatabasePhotoConverter.bitmapToBytes(bitmap);

        ContentValues   content = new ContentValues();
        content.put("photo", photo);

        db.update("users", content, "id = "  + id, null);
        db.close();
    }

    public void removeContact(int id) {
        SQLiteDatabase  db = database.getWritableDatabase();

        db.delete("users", "id=?", new String[] {Integer.toString(id)} );
        Toast.makeText(context, R.string.delete_success, Toast.LENGTH_SHORT).show();

        Cursor cursor = db.query("users", usersFields, null, null,
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            ContentValues   content;
            int             newId = 1;

            while (cursor.moveToNext()) {
                content = new ContentValues();
                content.put("id", newId++);

                db.update("users", content, "id = "
                        + cursor.getInt(cursor.getColumnIndex("id")), null);
            }
            cursor.close();
        }

        db.close();
    }

    /************ Getters *************/

    public Model getContactById(int id) {
        Model contact = null;
        Cursor  cursor = database.getReadableDatabase().query("users", usersFields,
                "id = " + id, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Bitmap  photo = DatabasePhotoConverter.bytesToBitmap(
                    cursor.getBlob(cursor.getColumnIndex("photo")) );

            contact = new Model(
                    cursor.getString( cursor.getColumnIndex("name") ),
                    cursor.getString( cursor.getColumnIndex("mobile_num") ),
                    cursor.getString( cursor.getColumnIndex("mail") ),
                    photo
            );

            cursor.close();
        }

        return contact;
    }

    public Model    getContactByNumber(String mobileNumber) {
        Cursor  cursor = database.getReadableDatabase().query("users", usersFields,
                "mobile_num like '" + mobileNumber + "'", null, null, null, null);
        Model   contact = new Model("", "", "", null);

        if (cursor != null && cursor.moveToFirst()) {
            contact.setName( cursor.getString(cursor.getColumnIndex("name")) );
            contact.setPhoto( DatabasePhotoConverter
                    .bytesToBitmap(cursor.getBlob(cursor.getColumnIndex("photo"))) );

            cursor.close();
        }

        return contact;
    }

    public List<Model> getContactsForList() {
        Cursor          cursor = database.getReadableDatabase().query("users", usersFields,
                null, null, null, null, null);
        List<Model>   contacts = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            String  name, number;
            Bitmap  photo;

            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex("name"));
                number = cursor.getString(cursor.getColumnIndex("mobile_num"));
                photo = DatabasePhotoConverter.bytesToBitmap(
                        cursor.getBlob(cursor.getColumnIndex("photo")) );

                contacts.add( new Model(name, number, null, photo) );
            }

            cursor.close();
        }
        return contacts;
    }

    public int  sizeDatabase() {
        Cursor cursor = database.getReadableDatabase().query("users", usersFields,
                null, null, null, null, null);
        int size = cursor.getCount();

        cursor.close();
        return size;
    }

    /*********** Checker *****************/

    private boolean checkUniquenessNumber(String num) {
        Cursor cursor = database.getReadableDatabase().query("users", usersFields,
                null, null, null, null, null);

        if (cursor != null) {
            while ( cursor.moveToNext() ) {
                if ( cursor.getString(cursor.getColumnIndex("mobile_num")).equals(num) ) {
                    Toast.makeText(context, R.string.not_unique, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            cursor.close();
        }
        return true;
    }
}
