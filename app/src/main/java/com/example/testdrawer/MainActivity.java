package com.example.testdrawer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.testdrawer.DataBase.mDBHelper;
import com.example.testdrawer.Dialogs.LoopEditDialog;
import com.example.testdrawer.Dialogs.NavItemEditDialog;
import com.example.testdrawer.Dialogs.TimerEditDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavItemEditDialog.NavItemEditListener , LoopEditDialog.LoopEditListener, TimerEditDialog.TimerEditListener, View.OnClickListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private mDBHelper dbHelper;
    private Toolbar toolbar;
    private FloatingActionButton timer_button;
    private TimerFragment currentFragment;
    private boolean isFabOpen = false;
    private FloatingActionButton add_toggle;
    private FloatingActionButton add_loop;
    private FloatingActionButton add_timer;

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

        add_toggle = findViewById(R.id.add_button);
        add_loop = findViewById(R.id.add_loop_button);
        add_timer = findViewById(R.id.add_timer_button);

        add_toggle.setOnClickListener(this);
        add_loop.setOnClickListener(this);
        add_timer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.add_button:
                if (isFabOpen){
                    hideFabMenu();
                }else {
                    showFabMenu();
                }
                break;
            case R.id.add_loop_button:
                currentFragment.addLoop();
                hideFabMenu();
                break;
            case R.id.add_timer_button:
                currentFragment.addTimer();
                hideFabMenu();
                break;
        }
    }

    private void showFabMenu(){
        isFabOpen = true;
        add_loop.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        add_timer.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void hideFabMenu(){
        isFabOpen = false;
        add_loop.animate().translationY(0);
        add_timer.animate().translationY(0);
        add_toggle.bringToFront();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){

            case R.id.nav_add:
                int id = dbHelper.addMenuItem(getString(R.string.new_timer_name), R.drawable.ic_timer_black_24dp, navigationView.getMenu());
                toolbar.setTitle(getString(R.string.new_timer_name));

                currentFragment = TimerFragment.newInstance(Integer.toString(id), dbHelper);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
                navigationView.setCheckedItem(id);

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

        MenuItem replacement = navigationView.getMenu().getItem(1);

        int id = replacement.getItemId();
        currentFragment = TimerFragment.newInstance(Integer.toString(id), dbHelper);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

        toolbar.setTitle(replacement.getTitle());

        navigationView.setCheckedItem(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final MenuItem current = navigationView.getCheckedItem();
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

            case R.id.action_delete:
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Delete Timer");
                alertDialog.setMessage("Do you want to delete the timer");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onNavItemDelete(current.getItemId());
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

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

        editor.putInt(SHARED_ID, navigationView.getCheckedItem().getItemId());
        editor.apply();
        super.onStop();
    }
}
