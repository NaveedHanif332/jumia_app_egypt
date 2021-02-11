package com.jumia.egyptblackfriday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jumia.egyptblackfriday.fragment.MenuFragment;

import static com.jumia.egyptblackfriday.constant.AppConstant.BASEURL;
import static com.jumia.egyptblackfriday.constant.AppConstant.DEAL;
import static com.jumia.egyptblackfriday.constant.AppConstant.Homeurl;
import static com.jumia.egyptblackfriday.constant.AppConstant.Hot;
import static com.jumia.egyptblackfriday.constant.AppConstant.INTERID;
import static com.jumia.egyptblackfriday.constant.AppConstant.PRIVACY;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    public static WebView webView;
    private String url = BASEURL;
    public static SwipeRefreshLayout swipeRefreshLayout;
    static ProgressBar progressBar;
    private AdView mAdView;
    LottieAnimationView lottieAnimationView;
    LinearLayout animation, main;
    BottomNavigationView bottomAppBar;
    private InterstitialAd mInterstitialAd;
    private boolean ads_shown = false;
    private boolean p_code=false;


    @SuppressLint({"SetJavaScriptEnabled", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.web_view);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);

        progressBar = findViewById(R.id.progress_bar);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        animation = findViewById(R.id.animation_layout);
        main = findViewById(R.id.main_layout);
        animation.setVisibility(View.INVISIBLE);
        main.setVisibility(View.VISIBLE);
        bottomAppBar = findViewById(R.id.app_bar);
        lottieAnimationView = findViewById(R.id.animationView);
        lottieAnimationView.setVisibility(View.GONE);
        progressBar.setProgress(0);

        swipeRefreshLayout.setEnabled(true);

        if (savedInstanceState == null) {
            check_connection();
        } else {
            webView.restoreState(savedInstanceState);
        }
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.design_default_color_on_secondary),
                getResources().getColor(R.color.design_default_color_secondary_variant), getResources().getColor(R.color.design_default_color_primary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId  = "message";
            String channelName = "Channel human readable title";
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }
        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null || url.startsWith("http://") || url.startsWith("https://"))
                    return false;
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(intent);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        bottomAppBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (!ads_shown)
                    showAds();
                switch (item.getItemId()) {
                    case R.id.back:
                        if (webView.canGoBack()) {
                            webView.goBack();
                        } else item.setIcon(R.drawable.noback);
                        break;
                    case R.id.menu: {
                        MenuFragment bottomNavDrawerFragment = new MenuFragment();
                        bottomNavDrawerFragment.show(getSupportFragmentManager(), bottomNavDrawerFragment.getTag());
                        break;
                    }
                    case R.id.home:
                        webView.loadUrl(Homeurl);
                        break;
                    case R.id.deal:
                        webView.loadUrl(DEAL);
                        break;
                    case R.id.hot:
                        webView.loadUrl(Hot);
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);

                if (newProgress == 100) {


                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void showAds() {
        if (!ads_shown) {
            if (mInterstitialAd != null) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mInterstitialAd.isLoaded() && !ads_shown) {
                            mInterstitialAd.show();
                            ads_shown = true;
                        } else {
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mInterstitialAd.isLoaded() && !ads_shown) {
                                        mInterstitialAd.show();
                                        ads_shown = true;
                                        return;
                                    }
                                }
                            }, 10000);
                        }
                    }
                }, 10000);

            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent it = getIntent();
        ads_shown = it.getBooleanExtra("ads_shown", false);
        p_code=it.getBooleanExtra("p_code",false);
        if(p_code)
        {

           showprivacy();
        }
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(INTERID);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void showprivacy() {
        final AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
        b.setTitle("Black Friday privacy policy ");
        b.setMessage(PRIVACY);
        b.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                b.setCancelable(true);
            }
        }).setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                b.setCancelable(true);
            }
        }).show();
    }



    public void check_connection() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean connected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                if (connected) {
                    animation.setVisibility(View.VISIBLE);
                    main.setVisibility(View.INVISIBLE);
                    lottieAnimationView.playAnimation();
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                } else {
                    animation.setVisibility(View.INVISIBLE);
                    main.setVisibility(View.VISIBLE);
                    webView.loadUrl(url);
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack())
            webView.goBack();
        else {
            final AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Exit Jumia Black Friday");
            b.setMessage("Are you sure you want to exit?");
            b.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    b.setCancelable(true);
                }
            }).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                }
            }).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }
}
