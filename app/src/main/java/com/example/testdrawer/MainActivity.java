package com.example.testdrawer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

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

    private FloatingActionButton fab;
    private LinearLayout fabLayout1, fabLayout2;
    private View fabBGLayout;
    boolean isFABOpen = false;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SHARED_ID = "id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        timer_button = findViewById(R.id.start_timer_button);
        timer_button.setOnClickListener(this);

        dbHelper = new mDBHelper(this);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fabLayout1 = findViewById(R.id.fabLayout1);
        fabLayout2 = findViewById(R.id.fabLayout2);
        fab = findViewById(R.id.fab);
        FloatingActionButton fab1 = findViewById(R.id.fab1);
        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fabBGLayout = findViewById(R.id.fabBGLayout);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });
    }

    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);
        fab.animate().rotation(45);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotation(0);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout2.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fab:
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
                break;
            case R.id.fab1:
                currentFragment.addLoop();
                closeFABMenu();
                break;
            case R.id.fab2:
                currentFragment.addTimer();
                closeFABMenu();
                break;
            case R.id.start_timer_button:
                currentFragment.startTimer();
                break;
        }
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
