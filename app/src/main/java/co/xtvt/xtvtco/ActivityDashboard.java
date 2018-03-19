package co.xtvt.xtvtco;

import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.TabLayout;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityDashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Boolean exit = false;
    private AppBarLayout appBarLayout;
    private NavigationView navigationView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Toolbar toolbar;
    FloatingActionButton fabPlus, fabActivity, fabPost, fabMedia;
    Animation fabOpen, fabClose, fabRotateClockwise, fabRotateAntiClockwise;
    Boolean isOpen = false;
    View shadowView;
    TextView textViewActivities, textViewPosts, textViewMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.home);

        shadowView = findViewById(R.id.shadowView);

        fabPlus = findViewById(R.id.fab_plus);
        fabActivity = findViewById(R.id.fab_activity);
        fabPost = findViewById(R.id.fab_post);
        fabMedia = findViewById(R.id.fab_media);

        textViewActivities = findViewById(R.id.textViewActivities);
        textViewPosts = findViewById(R.id.textViewPosts);
        textViewMedia = findViewById(R.id.textViewMedia);

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabRotateClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fabRotateAntiClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabVisibility();
            }
        });

        /*fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        toolbar.setTitle(R.string.home);
                        break;
                    case 1:
                        toolbar.setTitle(R.string.activities);
                        break;
                    case 2:
                        toolbar.setTitle(R.string.people);
                        break;
                    case 3:
                        toolbar.setTitle(R.string.posts);
                        break;
                    case 4:
                        toolbar.setTitle(R.string.media);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void fabVisibility(){
        if (isOpen){
            fabActivity.startAnimation(fabClose);
            textViewActivities.startAnimation(fabClose);
            fabPost.startAnimation(fabClose);
            textViewPosts.startAnimation(fabClose);
            fabMedia.startAnimation(fabClose);
            textViewMedia.startAnimation(fabClose);
            fabPlus.startAnimation(fabRotateAntiClockwise);
            fabActivity.setClickable(false);
            fabPost.setClickable(false);
            fabMedia.setClickable(false);
            shadowView.setVisibility(View.GONE);
            isOpen = false;
        }else {
            fabActivity.startAnimation(fabOpen);
            textViewActivities.startAnimation(fabOpen);
            fabPost.startAnimation(fabOpen);
            textViewPosts.startAnimation(fabOpen);
            fabMedia.startAnimation(fabOpen);
            textViewMedia.startAnimation(fabOpen);
            fabPlus.startAnimation(fabRotateClockwise);
            fabActivity.setClickable(true);
            fabPost.setClickable(true);
            fabMedia.setClickable(true);
            shadowView.setVisibility(View.VISIBLE);
            isOpen = true;
        }
     }

    @Override
    public void onBackPressed() {
        if (shadowView.getVisibility() == View.VISIBLE) {
            fabVisibility();
        } else {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                if (exit) {
                    finish(); // finish activity
                } else {
                    Toast.makeText(this, R.string.press_back_again_to_exit,
                            Toast.LENGTH_SHORT).show();
                    exit = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exit = false;
                        }
                    }, 3 * 1000);

                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_dashboard, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_drawer_profile) {
            Intent intent = new Intent(ActivityDashboard.this, ActivityGeneral.class);
            intent.putExtra("FragmentToLoad", "Profile");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_drawer_notifications) {
            Intent intent = new Intent(ActivityDashboard.this, ActivityGeneral.class);
            intent.putExtra("FragmentToLoad", "Notifications");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_drawer_activity) {
            Intent intent = new Intent(ActivityDashboard.this, ActivityGeneral.class);
            intent.putExtra("FragmentToLoad", "Activities");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_drawer_logout){
            logOutDialog();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logOutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.logout_confirmation)
                .setPositiveButton(getString(R.string.ok).toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mAuth.signOut();
                        Intent intent = new Intent(ActivityDashboard.this, ActivityLogin.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    FragmentMainHome fragmentMainHome = new FragmentMainHome();
                    return fragmentMainHome;
                case 1:
                    FragmentMainActivities fragmentMainActivities = new FragmentMainActivities();
                    return fragmentMainActivities;
                case 2:
                    FragmentMainPeople fragmentMainPeople = new FragmentMainPeople();
                    return fragmentMainPeople;
                case 3:
                    FragmentMainPosts fragmentMainPosts = new FragmentMainPosts();
                    return fragmentMainPosts;
                case 4:
                    FragmentMainMedia fragmentMainMedia = new FragmentMainMedia();
                    return fragmentMainMedia;

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show total pages.
            return 5;
        }
    }
}
