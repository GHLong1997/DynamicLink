package com.example.framgiahangoclong.dynamiclinkfb;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.net.URL;
import java.net.URLDecoder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String DEEP_LINK_URL = "https://youtube.com";
    Uri shortLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");

        findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });


//
        buildLink(null);
//        builShortLink();
//        final Uri deepLink =  getDynamicLink(Uri.parse(DEEP_LINK_URL), 0);
//        ((TextView) findViewById(R.id.link_view_send)).setText(deepLink.toString());
//        validateAppCode();
//
//        // Create a deep link and display it in the UI
//        final Uri deepLink = buildDeepLink(Uri.parse(DEEP_LINK_URL), 0);
//        Log.d(TAG, "onCreate: " + deepLink.toString());
//        ((TextView) findViewById(R.id.link_view_send)).setText(deepLink.toString());

        // Share button click listener
        findViewById(R.id.button_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                shareDeepLink(deepLink.toString());
//                shareLink(v);
            }
        });
        // [END_EXCLUDE]

        // [START get_deep_link]
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(pendingDynamicLinkData);
//                            if (invite != null) {
//                                String invitationId = invite.getInvitationId();
//                                if (!TextUtils.isEmpty(invitationId)) {
//                                    Log.d(TAG, "onSuccess: " + invitationId);
//                                }
//                            }
                            Intent a = getIntent();
                            a.getData();

                        }
                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // [START_EXCLUDE]
                        // Display deep link in the UI
                        if (deepLink != null) {
                            Log.d(TAG, "getDynamicLink: found");

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

    private Uri getDynamicLink(@NonNull Uri destinationLink, int minVersion) {
        String linkdomain = "hangoclong.page.link";
        DynamicLink.Builder builder = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setDynamicLinkDomain(linkdomain)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
                        .setMinimumVersion(minVersion)
                        .build())
                .setIosParameters(new DynamicLink.IosParameters.Builder(linkdomain).build())
                .setLink(destinationLink);
        DynamicLink link = builder.buildDynamicLink();
        return link.getUri();
    }

    public void buildLink(View v) {
        Uri uri = getDynamicLink(Uri.parse("http://en.proft.me/?status=100"), 0);
        Log.d(TAG, "buildLink: " + uri.toString());
        Task<ShortDynamicLink> task = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLongLink(uri)
                .buildShortDynamicLink()
                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            shortLink = task.getResult().getShortLink();
                            Log.d(TAG, "onComplete: " + shortLink.toString());
                            ((TextView) findViewById(R.id.link_view_receive)).setText(shortLink.toString());
                        } else {
                            ((TextView) findViewById(R.id.link_view_receive)).setText("Error retrieving link");
                        }
                    }
                });
    }

    public Uri buildDeepLink(@NonNull Uri deepLink, int minVersion) {
        String domain = "hangoclong.page.link";
        String packageName = getApplicationContext().getPackageName();

        // Set dynamic link parameters:
        //  * Domain (required)
        //  * Android Parameters (required)
        //  * Deep link
        // [START build_dynamic_link]
        DynamicLink.Builder builder = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setDynamicLinkDomain(domain)
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().setMinimumVersion(minVersion)
                        .build())
                // Open links with com.example.ios on iOS
                .setLink(deepLink);


        // Build the dynamic link
        DynamicLink link = builder.buildDynamicLink();
        return link.getUri();
        // [END build_dynamic_link]

        // Return the dynamic link as a URI
//        return link.getUri();
    }

    private void builShortLink() {
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https:youtube.com"))
                .setDynamicLinkDomain("hangoclong.page.link")
                // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            shortLink = task.getResult().getShortLink();
                            Log.d(TAG, "onComplete: " + shortLink.toString());
                            ((TextView) findViewById(R.id.link_view_receive)).setText(shortLink.toString());
                        } else {
                            ((TextView) findViewById(R.id.link_view_receive)).setText("Error retrieving link");
                        }
                    }
                });
    }

    private void shareDeepLink(String deepLink) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Firebase Deep Link");
        intent.putExtra(Intent.EXTRA_TEXT, deepLink);

        startActivity(intent);
    }

    private void validateAppCode() {
        String domain = "hangoclong.page.link";
        if (domain.contains("com.example.admin.dynamiclink")) {
            new AlertDialog.Builder(this)
                    .setTitle("Invalid Configuration")
                    .setMessage("Please set your Dynamic Links domain in app/build.gradle")
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
        }
    }

    public void shareLink(View v) {
        try {
            URL url = new URL(URLDecoder.decode(shortLink.toString(), "UTF-8"));
            Log.i(TAG, "URL = " + url.toString());
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Firebase Deep Link");
            intent.putExtra(Intent.EXTRA_TEXT, url.toString());
            startActivity(intent);
        } catch (Exception e) {
            Log.i(TAG, "Could not decode Uri: " + e.getLocalizedMessage());
        }
    }


}
