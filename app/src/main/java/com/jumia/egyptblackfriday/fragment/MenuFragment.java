package com.jumia.egyptblackfriday.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.jumia.egyptblackfriday.R;

import static com.jumia.egyptblackfriday.MainActivity.webView;
import static com.jumia.egyptblackfriday.constant.AppConstant.DEAL;
import static com.jumia.egyptblackfriday.constant.AppConstant.Homeurl;
import static com.jumia.egyptblackfriday.constant.AppConstant.Hot;
import static com.jumia.egyptblackfriday.constant.AppConstant.PRIVACY;


public class MenuFragment extends BottomSheetDialogFragment {
    BottomNavigationView bottomAppBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        final NavigationView navigationView = view.findViewById(R.id.navigation_view);
        final FrameLayout frameLayout = view.findViewById(R.id.frame_layout);
        bottomAppBar=view.findViewById(R.id.app_bar);
        frameLayout.setVisibility(View.VISIBLE);

        bottomAppBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.back:
                        if(webView.canGoBack())
                        {
                            webView.goBack();
                        }
                        break;
                    case R.id.menu: {
//                        MenuFragment bottomNavDrawerFragment = new MenuFragment();
//                        bottomNavDrawerFragment.show(getSupportFragmentManager(), bottomNavDrawerFragment.getTag());
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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.shareapp: {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.jumia.egyptblackfriday");
                        startActivity(shareIntent);
                        break;
                    }
                    case R.id.sharepage: {
//                        frameLayout.setVisibility(View.GONE);
                        String current_url = webView.getUrl();
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, current_url);
                        startActivity(shareIntent);
                        break;
                    }
                    case R.id.rate: {
                        //rate us
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.jumia.egyptblackfriday")));
                        break;
                    }
                    case R.id.browser: {
                        //open in broswer
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Homeurl)));
                        break;
                    }

                    case R.id.privacy:
                        final AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
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
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                return true;
            }
        });
        return view;
    }


}