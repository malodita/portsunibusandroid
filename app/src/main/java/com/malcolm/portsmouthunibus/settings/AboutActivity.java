package com.malcolm.portsmouthunibus.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.webkit.WebView;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.malcolm.portsmouthunibus.BuildConfig;
import com.malcolm.portsmouthunibus.R;

public class AboutActivity extends MaterialAboutActivity {

    @Override
    protected MaterialAboutList getMaterialAboutList(Context context) {
        MaterialAboutCard.Builder app = new MaterialAboutCard.Builder()
                .title("About");

        app.addItem(new MaterialAboutTitleItem.Builder()
                .text(R.string.app_name)
                .icon(R.mipmap.ic_launcher_round)
                .build());
        app.addItem(new MaterialAboutActionItem.Builder()
                .text("Version").subText(BuildConfig.VERSION_NAME)
                .icon(R.drawable.ic_info)
                .build());
        app.addItem(new MaterialAboutActionItem.Builder()
                .text("Changelog")
                .icon(R.drawable.ic_history)
                .setOnClickListener(() -> displayLicensesDialog("changelog.html"))
                .build());
        app.addItem(new MaterialAboutActionItem.Builder()
                .text("Privacy Policy")
                .subText("For more information please search Firebase Privacy Policy")
                .setOnClickListener(() -> displayLicensesDialog("privacypolicy.html")).build());

        MaterialAboutCard.Builder licenses = new MaterialAboutCard.Builder().title("Libraries & Licenses");
        licenses.addItem(new MaterialAboutActionItem.Builder()
                .text("Bottom Bar")
                .icon(R.drawable.ic_github_circle)
                .subText("Iiro Krankka (Roughike)")
                .setOnClickListener(() -> displayLicensesDialog("bottombar.html")).build());
        licenses.addItem(new MaterialAboutActionItem.Builder()
                .text("Lottie").subText("Airbnb")
                .icon(R.drawable.ic_github_circle)
                .setOnClickListener(() -> displayLicensesDialog("lottie.html")).build());
        licenses.addItem(new MaterialAboutActionItem.Builder()
                .text("Ink Pager Indicator")
                .subText("David Păcioianu")
                .icon(R.drawable.ic_github_circle)
                .setOnClickListener(() -> displayLicensesDialog("inkpagerindicator.html"))
                .build());
        licenses.addItem(new MaterialAboutActionItem.Builder()
                .text("Material About Library").subText("Daniel Stone")
                .icon(R.drawable.ic_github_circle)
                .setOnClickListener(() -> displayLicensesDialog("materialaboutlibrary.html")).build());
        licenses.addItem(new MaterialAboutActionItem.Builder()
                .text("Retrofit")
                .subText("Square")
                .icon(R.drawable.ic_github_circle)
                .setOnClickListener(() -> displayLicensesDialog("retrofit.html")).build());
        licenses.addItem(new MaterialAboutActionItem.Builder()
                .text("Leak Canary")
                .subText("Square")
                .icon(R.drawable.ic_github_circle)
                .setOnClickListener(() -> displayLicensesDialog("leakcanary.html"))
                .build());
        licenses.addItem(new MaterialAboutActionItem.Builder()
                .text("TapTargetView")
                .subText("Keepsafe")
                .icon(R.drawable.ic_github_circle)
                .setOnClickListener(() -> displayLicensesDialog("taptargetview.html")).build());
        licenses.addItem(new MaterialAboutActionItem.Builder()
                .text("Butterknife")
                .subText("Jake Wharton").icon(R.drawable.ic_github_circle)
                .setOnClickListener(() -> displayLicensesDialog("butterknife.html")).build());
        licenses.addItem(new MaterialAboutActionItem.Builder()
                .text("Epoxy")
                .subText("Airbnb")
                .icon(R.drawable.ic_github_circle)
                .setOnClickListener(() -> displayLicensesDialog("epoxy.html")).build());
        licenses.addItem(new MaterialAboutActionItem.Builder()
                .text("Picasso")
                .subText("Square")
                .icon(R.drawable.ic_github_circle)
                .setOnClickListener(() -> displayLicensesDialog("picasso.html")).build());

        MaterialAboutCard.Builder personal = new MaterialAboutCard.Builder().title("Developer");
        personal.addItem(new MaterialAboutActionItem.Builder()
                .text("Malcolm Odita")
                .subText("In his spare time, he takes pictures")
                .icon(R.drawable.ic_google_photos)
                .setOnClickListener(() -> {
                    String url = "https://malcolmphoto.co.uk/";
                    Intent view = new Intent(Intent.ACTION_VIEW);
                    view.setData(Uri.parse(url));
                    startActivity(view);
                })
                .build());
        MaterialAboutCard.Builder thanks = new MaterialAboutCard.Builder().title("Special Thanks");
        thanks.addItem(new MaterialAboutActionItem.Builder()
                .text("Austin Andrews").subText("For icon use")
                .icon(R.drawable.ic_twitter)
                .setOnClickListener(() -> {
                    String url = "https://twitter.com/Templarian";
                    Intent view = new Intent(Intent.ACTION_VIEW);
                    view.setData(Uri.parse(url));
                    startActivity(view);
                }).build());
        thanks.addItem(new MaterialAboutActionItem.Builder()
                .text("Corbin Scott")
                .subText("For Lottie asset use")
                .icon(R.drawable.ic_twitter)
                .setOnClickListener(() -> {
                    String url = "https://twitter.com/slykuiper";
                    Intent view = new Intent(Intent.ACTION_VIEW);
                    view.setData(Uri.parse(url));
                    startActivity(view);
                })
                .build());


        return new MaterialAboutList.Builder()
                .addCard(app.build())
                .addCard(personal.build())
                .addCard(licenses.build())
                .addCard(thanks.build())
                .build();    }


    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.mal_title_about);
    }

    private void displayLicensesDialog(String fileName){
        WebView view = (WebView) LayoutInflater.from(this).inflate(R.layout.dialog_licenses, null);
        view.loadUrl("file:///android_asset/" + fileName);
        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }


}
