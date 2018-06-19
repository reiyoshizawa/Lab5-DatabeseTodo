package com.example.yoshizawarei.lab5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class TodoDBHelper extends SQLiteOpenHelper {

    public static class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todos";
        public static final String COLUMN_NAME_TODO = "todo";
    }
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "todo.db";

    private static TodoDBHelper mTodoDBHelper;

    public static synchronized TodoDBHelper getinstance(Context context) {
        // if no instance
        if (mTodoDBHelper == null) {
            mTodoDBHelper = new TodoDBHelper(context.getApplicationContext());
        }
        // use the one that's already created
        return mTodoDBHelper;

    }

    public TodoDBHelper(Context context) {
        // we only need one instance at TodoDBHelper
        // Singleton pattern : class that only has one instance
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create a table
        db.execSQL("CREATE TABLE " + TodoEntry.TABLE_NAME + "(" + TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TodoEntry.COLUMN_NAME_TODO + " TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop the table
        db.execSQL("DROP TABLE IF EXISTS " + TodoEntry.TABLE_NAME);
        onCreate(db);
    }
    // add
    public void addTodo(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        // it helps with performance and ensures consistency of db
        db.beginTransaction();
        try {
            // HashMap
            ContentValues values = new ContentValues();
            values.put(TodoEntry.COLUMN_NAME_TODO, todo.todo);
            long id = db.insertOrThrow(TodoEntry.TABLE_NAME, null, values);
            // INSERT INTO todos VALUES (todo = 'Do something')"
            todo._id = id; // java
            db.setTransactionSuccessful();
        } catch (Exception e) {
            // error!
        } finally {
            db.endTransaction();
        }
    }

    // read - read all todos from the table
    public List<Todo> getAllTodos() {
        // create an arrayList
        List<Todo> todos = new ArrayList<>();

        // get all data from the database
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TodoEntry.TABLE_NAME, null);

        // add each row(data) to the arrayList add return.
        try {
            if (cursor.moveToFirst()) {
                do {
                    Todo todo = new Todo();
                    todo.todo = cursor.getString(cursor.getColumnIndex(TodoEntry.COLUMN_NAME_TODO));
                    todo._id = cursor.getLong(0);
                    todos.add(todo);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            // error

        } finally {
            if (cursor != null && cursor.isClosed()) {
                cursor.close();
            }
        }
        return todos;
    }

    // update
    public void updateTodo(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(TodoEntry.COLUMN_NAME_TODO, todo.todo);

            // UPDATE todos SET todos = new_todo WHEre condition
            String[] args = {String.valueOf(todo._id)};
            db.update(TodoEntry.TABLE_NAME, values, "_id=?", args);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            // error!
        } finally {
            db.endTransaction();
        }
    }

    // delete
    public void deleteTodo(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            String[] args = { String.valueOf(todo._id) };
            db.delete(TodoEntry.TABLE_NAME, "_id=?", args);
            db.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }
    }

    // create
}
