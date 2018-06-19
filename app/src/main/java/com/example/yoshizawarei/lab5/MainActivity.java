package com.example.yoshizawarei.lab5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity:";
    private SwipeMenuListView mListView;                 //display
    private ArrayAdapter<Todo> mArrayAdapter; //data
    private ArrayList<Todo> mTodos;
    private TodoDBHelper mTodoDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTodoDBHelper = TodoDBHelper.getinstance(this);
        mTodos = (ArrayList<Todo>) mTodoDBHelper.getAllTodos();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            showDialogToAddTodo();

            }
        });

        //create arraylist
        mTodos = new ArrayList<>();

        mListView = findViewById(R.id.todo_list);
        mArrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                mTodos);

        mListView.setAdapter(mArrayAdapter);
        setSwipeMenu();
    }

    private void setSwipeMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // editItem
                SwipeMenuItem editItem = new SwipeMenuItem(MainActivity.this);
                editItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                editItem.setWidth(170);
                editItem.setIcon(R.mipmap.ic_action_edit);
                menu.addMenuItem(editItem);

                // TODO: delete - make (red) delete button
                SwipeMenuItem deleteItem = new SwipeMenuItem(MainActivity.this);
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                deleteItem.setWidth(170);
                deleteItem.setIcon(R.mipmap.ic_action_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // when edit is clicked
                        showDialogToUpdateTodo(mTodos.get(position));
                        break;
                    case 1:
                        // when delete is clicked

                        // 2. delete in the database
                        mTodoDBHelper.deleteTodo(mTodos.get(position));

                        // 1. delete from the mListView
                        //   - get arraylist
                        //   - find the item and remove
                        mTodos.remove(position);
                        //   - notify the listview that you removed an item
                        mArrayAdapter.notifyDataSetChanged();
                        break;

                        // TODO - 1. delete view 2. database
                }
                // false: close the menu, true: not close the menu
                return false;
            }
        });
    }


    private void showDialogToUpdateTodo(final Todo todo){
        //1. create a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.update_menu_title));

        //2. add edittext + add parameter
        final EditText input = new EditText(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(10,0,10,0);

        input.setLayoutParams(layoutParams);
        input.setText(todo.todo);
        //3. Create add buttons
        builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //4. add user input to arraylist
                String todoString = input.getText().toString();
                todo.todo = todoString;
                mArrayAdapter.notifyDataSetChanged();
                //update adapter
                mTodoDBHelper.updateTodo(todo);
            }
        });


        //5. show
        AlertDialog dialog = builder.create();
        dialog.setView(input);
        dialog.show();

    }

    private void showDialogToAddTodo(){
        //1. create a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_menu_title));

        //2. add edittext + add parameter
        final EditText input = new EditText(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(10,0,10,0);

        input.setLayoutParams(layoutParams);

        //3. Create add buttons
        builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //4. add user input to arraylist
                String todoString = input.getText().toString();
                Todo todo = new Todo();
                todo.todo = todoString;
                mTodos.add(todo);
                mArrayAdapter.notifyDataSetChanged();

                //update adapter
                mTodoDBHelper.addTodo(todo);
            }
        });


        //5. show
        AlertDialog dialog = builder.create();
        dialog.setView(input);
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}