package com.example.admin.dynamiclink;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String DEEP_LINK_URL = "https://youtube.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        validateAppCode();

        // Create a deep link and display it in the UI
        final Uri deepLink = buildDeepLink(Uri.parse(DEEP_LINK_URL), 0);
        ((TextView) findViewById(R.id.link_view_send)).setText(deepLink.toString());

        // Share button click listener
        findViewById(R.id.button_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDeepLink(deepLink.toString());
            }
        });
        // [END_EXCLUDE]

        // [START get_deep_link]
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // [START_EXCLUDE]
                        // Display deep link in the UI
                        if (deepLink != null) {
                            Log.d(TAG, "getDynamicLink: ound");

                            ((TextView) findViewById(R.id.link_view_receive))
                                    .setText(deepLink.toString());
                        } else {
                            Log.d(TAG, "getDynamicLink: no link found");
                        }
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
        // [END get_deep_link]
    }



    public Uri buildDeepLink(@NonNull Uri deepLink, int minVersion) {
        String domain = "https://hangoclong.page.link";
        String packageName = getApplicationContext().getPackageName();

        // Set dynamic link parameters:
        //  * Domain (required)
        //  * Android Parameters (required)
        //  * Deep link
        // [START build_dynamic_link]
        DynamicLink.Builder builder = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setDynamicLinkDomain(domain)
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setLink(deepLink);


        // Build the dynamic link
        DynamicLink link = builder.buildDynamicLink();
        // [END build_dynamic_link]

        // Return the dynamic link as a URI
        return link.getUri();
    }

    private void shareDeepLink(String deepLink) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Firebase Deep Link");
        intent.putExtra(Intent.EXTRA_TEXT,deepLink);

        startActivity(intent);
    }

    private void validateAppCode() {
        String domain = "https://hangoclong.page.link";
        if (domain.contains("com.example.admin.dynamiclink")) {
            new AlertDialog.Builder(this)
                    .setTitle("Invalid Configuration")
                    .setMessage("Please set your Dynamic Links domain in app/build.gradle")
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
        }
    }
}
