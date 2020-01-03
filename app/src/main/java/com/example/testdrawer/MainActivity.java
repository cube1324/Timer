package com.example.testdrawer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.testdrawer.DataBase.NavDrawerItem;
import com.example.testdrawer.DataBase.mDBHelper;
import com.example.testdrawer.Dialogs.LoopEditDialog;
import com.example.testdrawer.Dialogs.NavItemEditDialog;
import com.example.testdrawer.Dialogs.TimerEditDialog;
import com.example.testdrawer.Support.ListElement;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavItemEditDialog.NavItemEditListener , LoopEditDialog.LoopEditListener, TimerEditDialog.TimerEditListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private mDBHelper dbHelper;
    private Toolbar toolbar;
    private FloatingActionButton timer_button;
    TimerFragment currentFragment;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SHARED_ID = "id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        timer_button = findViewById(R.id.start_timer_button);
        timer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFragment.startTimer();
            }
        });

        dbHelper = new mDBHelper(this);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        /*
        if (savedInstanceState == null){
            int id = navigationView.getMenu().getItem(1).getItemId();
            currentFragment = TimerFragment.newInstance(Integer.toString(id), dbHelper);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

            id = 1;
            navigationView.setCheckedItem(id);
        }

         */

    }

    @Override
    protected void onResume() {
        /*
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        int id = sharedPreferences.getInt(SHARED_ID, navigationView.getMenu().getItem(1).getItemId());
        currentFragment = TimerFragment.newInstance(Integer.toString(id), dbHelper);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();


        navigationView.setCheckedItem(id);

         */
        super.onResume();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){

            case R.id.nav_add:
                dbHelper.addMenuItem("New Timer", R.drawable.ic_timer_black_24dp, navigationView.getMenu());
                break;

            default:
                toolbar.setTitle(menuItem.getTitle());
                currentFragment = TimerFragment.newInstance(Integer.toString(menuItem.getItemId()), dbHelper);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        dbHelper.populateMenu(navigationView.getMenu());

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        int id = sharedPreferences.getInt(SHARED_ID, navigationView.getMenu().getItem(1).getItemId());
        currentFragment = TimerFragment.newInstance(Integer.toString(id), dbHelper);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

        navigationView.setCheckedItem(id);

        return true;
    }


    @Override
    public void onNavItemDelete(int pos) {
        dbHelper.delMenuItem(pos, navigationView.getMenu());

        currentFragment.save = false;
        deleteFile("data"+pos);

        int id = navigationView.getMenu().getItem(1).getItemId();
        currentFragment = TimerFragment.newInstance(Integer.toString(id), dbHelper);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

        id = 1;
        navigationView.setCheckedItem(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuItem current = navigationView.getCheckedItem();
        switch (item.getItemId()){
            case R.id.action_settings:

                return true;

            case R.id.action_add_timer:
                currentFragment.addTimer();
                return true;

            case R.id.action_add_loop:
                currentFragment.addLoop();
                return true;

            case R.id.action_edit_timer:
                NavItemEditDialog d = new NavItemEditDialog(current.getItemId(), current.getTitle().toString());
                d.show(getSupportFragmentManager(), "Edit Timer");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onNavItemEdit(int pos, String name) {
        toolbar.setTitle(name);
        dbHelper.editMenuItem(pos,name, navigationView.getMenu());
    }

    @Override
    public void onLoopEdit(int pos, String name, int number) {
        currentFragment.onLoopEdit(pos,name,number);
    }

    @Override
    public void onTimerEdit(int pos, String name, int seconds, int hour) {
        currentFragment.onTimerEdit(pos, name, seconds, hour);
    }

    @Override
    protected void onStop() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(SHARED_ID, navigationView.getMenu().getItem(0).getItemId());
        super.onStop();
    }
}
