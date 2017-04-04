package com.ohwittmannone.just_paws;

import android.animation.Animator;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ohwittmannone.just_paws.admin.AdminMgmt;
import com.ohwittmannone.just_paws.fragments.AboutUsFragment;
import com.ohwittmannone.just_paws.fragments.AdoptFosterFragment;
import com.ohwittmannone.just_paws.fragments.AnimalFragment;
import com.ohwittmannone.just_paws.login.LoginActivity;
import com.ohwittmannone.just_paws.models.User;
import com.ohwittmannone.just_paws.utils.Cache;
import com.ohwittmannone.just_paws.utils.Common;

public class MainActivity extends BaseCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton mainFab, emailFab, instagramFab, facebookFab, twitterFab, youtubeFab;
    LinearLayout emailFabLayout, instagramFabLayout, facebookFabLayout, twitterFabLayout, youtubeFabLayout;
    boolean isFabOpen = false;
    public static String FACEBOOK_URL = "https://www.facebook.com/JustPawsRescue/";
    public static String FACEBOOK_PAGE_ID = "690542627671687";
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference reference;

    private Boolean adminStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupCollapsibleToolbar("Adoptable Animals", true);

        new getAdminStatus().execute();

        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = AnimalFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();


        //setting toggle for nav drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //refreshState();


        //fabs
        emailFabLayout = (LinearLayout) findViewById(R.id.emailFabLayout);
        instagramFabLayout = (LinearLayout) findViewById(R.id.instagramFabLayout);
        facebookFabLayout = (LinearLayout) findViewById(R.id.facebookFabLayout);
        twitterFabLayout = (LinearLayout) findViewById(R.id.twitterFabLayout);
        youtubeFabLayout = (LinearLayout) findViewById(R.id.youtubeFabLayout);
        mainFab = (FloatingActionButton) findViewById(R.id.mainfab);
        emailFab = (FloatingActionButton) findViewById(R.id.emailfab);
        instagramFab = (FloatingActionButton) findViewById(R.id.instagramfab);
        facebookFab = (FloatingActionButton) findViewById(R.id.facebookfab);
        twitterFab = (FloatingActionButton) findViewById(R.id.twitterfab);
        youtubeFab = (FloatingActionButton) findViewById(R.id.youtubefab);

        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFabOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        emailFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Email = new Intent(Intent.ACTION_SEND);
                Email.setType("text/email");
                Email.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{"info.justpaws@gmail.com"});
                Email.putExtra(Intent.EXTRA_TEXT, "Hello Just Paws,\n\n\n\n Sent via the Just Paws app." + "");
                startActivity(Intent.createChooser(Email, "Email with:"));
            }
        });

        instagramFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://instagram.com/_u/justpawsanimalrescue");
                Intent instagramIntent = new Intent(Intent.ACTION_VIEW, uri);

                instagramIntent.setPackage("com.instagram.android");

                try {
                    startActivity(instagramIntent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/justpawsanimalrescue")));
                }
            }
        });

        facebookFab.setOnClickListener(new View.OnClickListener() {

            //method to get the right URL to use in the intent
            public String getFacebookPageURL(Context context) {
                PackageManager packageManager = context.getPackageManager();
                try {
                    int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;

                    boolean activated = packageManager.getApplicationInfo("com.facebook.katana", 0).enabled;
                    if (activated) {
                        if ((versionCode >= 3002850)) {
                            return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
                        } else {
                            return "fb://page/" + FACEBOOK_PAGE_ID;
                        }
                    } else {
                        return FACEBOOK_URL;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    return FACEBOOK_URL;
                }
            }

            @Override
            public void onClick(View view) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(view.getContext());
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);

            }
        });

        twitterFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent twitterIntent = null;
                try {
                    // get the Twitter app if possible
                    view.getContext().getPackageManager().getPackageInfo("com.twitter.android", 0);
                    twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=2979962361"));
                    twitterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/JustPawsRescue"));
                }
                view.getContext().startActivity(twitterIntent);

            }
        });

        youtubeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent youtubeIntent = null;
                try {
                    youtubeIntent = new Intent(Intent.ACTION_VIEW);
                    youtubeIntent.setPackage("com.google.android.youtube");
                    youtubeIntent.setData(Uri.parse("https://www.youtube.com/channel/UC7lUsGYH8zO-TckIPOfOALQ?&ab_channel=JustPawsAnimalRescue"));
                    startActivity(youtubeIntent);
                } catch (ActivityNotFoundException e) {
                    youtubeIntent = new Intent(Intent.ACTION_VIEW);
                    youtubeIntent.setData(Uri.parse("https://www.youtube.com/channel/UC7lUsGYH8zO-TckIPOfOALQ?&ab_channel=JustPawsAnimalRescue"));
                    startActivity(youtubeIntent);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }


    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.available_animals) {
            fragmentClass = AnimalFragment.class;
            setupCollapsibleToolbar("Adoptable Animals", true);
        } else if (id == R.id.about_us) {
            fragmentClass = AboutUsFragment.class;
            setupCollapsibleToolbar("About Us", true);
        } else if (id == R.id.adopt_foster) {
            fragmentClass = AdoptFosterFragment.class;
            setupCollapsibleToolbar("Adopt or Foster", true);
        } else if (id == R.id.login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.logout) {
            fragmentClass = AnimalFragment.class;
            FirebaseAuth.getInstance().signOut();
            Cache.getInstance(this).logout();
            setupCollapsibleToolbar("Adoptable Animals", true);
            refreshState();

        }else if (id == R.id.adminMgmt){
            Intent intent = new Intent(this, AdminMgmt.class);
            startActivity(intent);
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (id != R.id.login && id != R.id.adminMgmt) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        }

        return true;
    }

    //fab methods
    private void showFABMenu() {
        isFabOpen = true;
        emailFabLayout.setVisibility(View.VISIBLE);
        instagramFabLayout.setVisibility(View.VISIBLE);
        facebookFabLayout.setVisibility(View.VISIBLE);
        twitterFabLayout.setVisibility(View.VISIBLE);
        youtubeFabLayout.setVisibility(View.VISIBLE);

        mainFab.animate().rotationBy(180);
        emailFabLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_60));
        instagramFabLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_120));
        facebookFabLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_180));
        twitterFabLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_240));
        youtubeFabLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_300));
    }

    private void closeFABMenu() {
        isFabOpen = false;

        mainFab.animate().rotationBy(-180);
        emailFabLayout.animate().translationY(0);
        instagramFabLayout.animate().translationY(0);
        facebookFabLayout.animate().translationY(0);
        twitterFabLayout.animate().translationY(0);
        youtubeFabLayout.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFabOpen) {
                    emailFabLayout.setVisibility(View.GONE);
                    instagramFabLayout.setVisibility(View.GONE);
                    facebookFabLayout.setVisibility(View.GONE);
                    twitterFabLayout.setVisibility(View.GONE);
                    youtubeFabLayout.setVisibility(View.GONE);
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

    private void
    refreshState() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (!Cache.getInstance(getApplicationContext()).getLoginState()) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer_with_login);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.getMenu().getItem(0).setChecked(true);
        } else if (Cache.getInstance(getApplicationContext()).getLoginState()) {
            navigationView.getMenu().clear();
            if (adminStatus){
                navigationView.inflateMenu(R.menu.activity_main_drawer_admin_logout);
            }
            else {
                navigationView.inflateMenu(R.menu.activity_main_drawer_with_logout);
            }
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.getMenu().getItem(0).setChecked(true);
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Cache.getInstance(getApplicationContext()).logout();
                refreshState();
                return;
            }
        }

    }

    class getAdminStatus extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                reference = FirebaseDatabase.getInstance().getReference(Common.USER).child(user.getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User userModel = (User) dataSnapshot.getValue(User.class);
                        adminStatus = userModel.isAdmin();
                        refreshState();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            return null;
        }
    }


}
