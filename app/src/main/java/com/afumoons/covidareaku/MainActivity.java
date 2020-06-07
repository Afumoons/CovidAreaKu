package com.afumoons.covidareaku;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Variables
    private static final int MY_REQUEST_CODE = 1212; // Any number
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;
    TextView txt_email;
    TextView txt_name;
    ImageView iv_userimage;
    List<AuthUI.IdpConfig> providers;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //================Hooks================
        hooksFunction();

        //================Toolbar================
        setSupportActionBar(toolbar);

        //================Navigation drawer menu================
        //Hide / show item
        menu.findItem(R.id.nav_logout).setVisible(false);
        menu.findItem(R.id.nav_profile).setVisible(false);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        //================Init provider================
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),// Email Builder
                new AuthUI.IdpConfig.GoogleBuilder().build() // Google Builder
        );
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_home2:
                intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_login:
                showSignInOptions();
                break;
            case R.id.nav_logout:
                //Logout
                AuthUI.getInstance().signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Hooks
                        hooksFunction();
                        //Remove user information
                        txt_name.setText("Login");
                        txt_email.setText("Untuk informasi user");
                        iv_userimage.setImageResource(R.drawable.user_icon);
                        Toast.makeText(MainActivity.this, "Logout berhasil", Toast.LENGTH_SHORT).show();
                        menu.findItem(R.id.nav_logout).setVisible(false);
                        menu.findItem(R.id.nav_profile).setVisible(false);
                        menu.findItem(R.id.nav_login).setVisible(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                //Get User
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //Hooks
                hooksFunction();
                //Show User Profile on Header
                txt_name.setText(user.getDisplayName());
                txt_email.setText(user.getEmail());
                Picasso.get().load(user.getPhotoUrl()).into(iv_userimage);
                Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();
                //Set Button Signout
                menu.findItem(R.id.nav_logout).setVisible(true);
                menu.findItem(R.id.nav_profile).setVisible(true);
                menu.findItem(R.id.nav_login).setVisible(false);
            } else {
                Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showSignInOptions() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers).setTheme(R.style.MyTheme).build(), MY_REQUEST_CODE
        );
    }

    private void hooksFunction() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        menu = navigationView.getMenu();
        txt_name = findViewById(R.id.txt_name);
        txt_email = findViewById(R.id.txt_email);
        iv_userimage = findViewById(R.id.iv_userimage);
    }
}
