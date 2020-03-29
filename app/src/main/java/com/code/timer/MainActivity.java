package com.code.timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.code.timer.DataBase.mDBHelper;
import com.code.timer.Dialogs.LoopEditDialog;
import com.code.timer.Dialogs.NavItemEditDialog;
import com.code.timer.Dialogs.TimerEditDialog;
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
    private TextView fabText1, fabText2;
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

        fabText1 = findViewById(R.id.fabText1);
        fabText2 = findViewById(R.id.fabText2);

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
        fabBGLayout.animate().alpha(0.7f).setDuration(400).setListener(null);
        fabText1.animate().alpha(1f).setDuration(400).setListener(null);
        fabText2.animate().alpha(1f).setDuration(400).setListener(null);

        fab.animate().rotationBy(135).setDuration(500);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.animate().alpha(0.0f).setDuration(400).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fabBGLayout.setVisibility(View.GONE);
            }
        });
        fab.animate().rotationBy(135).setDuration(500);

        fabText1.animate().alpha(0f).setListener(null);
        fabText2.animate().alpha(0f).setListener(null);

        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isFABOpen) {
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout1.setVisibility(View.GONE);
                }
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
                if (navigationView.getMenu().size() > 1) {
                    currentFragment.addLoop();
                    closeFABMenu();
                }
                break;
            case R.id.fab2:
                if (navigationView.getMenu().size() > 1) {
                    currentFragment.addTimer();
                    closeFABMenu();
                }
                break;
            case R.id.start_timer_button:
                if (navigationView.getMenu().size() > 1) {
                    currentFragment.startTimer();
                }
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
        if (navigationView.getMenu().size() > 1) {

            int id = sharedPreferences.getInt(SHARED_ID, navigationView.getMenu().getItem(1).getItemId());
            currentFragment = TimerFragment.newInstance(Integer.toString(id), dbHelper);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

            toolbar.setTitle(navigationView.getMenu().getItem(1).getTitle());

            navigationView.setCheckedItem(id);
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, EmptyFragment.newInstance()).commit();
        }
        return true;
    }


    @Override
    public void onNavItemDelete(int pos) {
        dbHelper.delMenuItem(pos, navigationView.getMenu());

        currentFragment.save = false;
        deleteFile("data"+pos);

        if (navigationView.getMenu().size() > 1) {

            MenuItem replacement = navigationView.getMenu().getItem(1);

            int id = replacement.getItemId();
            currentFragment = TimerFragment.newInstance(Integer.toString(id), dbHelper);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

            toolbar.setTitle(replacement.getTitle());

            navigationView.setCheckedItem(id);
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, EmptyFragment.newInstance()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final MenuItem current = navigationView.getCheckedItem();
        switch (item.getItemId()){
            case R.id.action_settings:

                return true;

            case R.id.action_edit_timer:
                if (navigationView.getMenu().size() > 1) {

                    NavItemEditDialog d = new NavItemEditDialog(current.getItemId(), current.getTitle().toString());
                    d.show(getSupportFragmentManager(), "Edit Timer");
                }
                return true;

            case R.id.action_delete:
                if (navigationView.getMenu().size() > 1) {
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
                }

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
    protected void onPause() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(SHARED_ID, navigationView.getCheckedItem().getItemId());
        editor.apply();
        super.onPause();
    }

}
