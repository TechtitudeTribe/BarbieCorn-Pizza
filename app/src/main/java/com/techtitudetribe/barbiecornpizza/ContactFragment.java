package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ContactFragment extends Fragment {

    private static String number = "+91 9927357499";
    private static final int REQUEST_CALL = 1;
    private ImageView facebook,gmail,phone,close;
    private TextView message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.contact_fragment, container, false);


        facebook = (ImageView) v.findViewById(R.id.need_help_facebook);
        gmail = (ImageView) v.findViewById(R.id.need_help_mail);
        phone = (ImageView) v.findViewById(R.id.need_help_phone);
        message = (TextView) v.findViewById(R.id.need_help_message);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = openFacebook(getActivity());
                startActivity(facebookIntent);
            }
        });
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = openGmail(getActivity());
                startActivity(emailIntent);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageIntent = openWhatsapp(getActivity());
                startActivity(messageIntent);
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        return v;
    }

    private void makePhoneCall() {
        String number = "9927357499";
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(getActivity(),"Permission Denied...",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Intent openFacebook(Context context) {
        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100027918345946"));
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "Ple" +
                    "ase install facebook...", Toast.LENGTH_SHORT).show();
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/profile.php?id=100027918345946"));
        }

    }
    public static Intent openGmail(Context context) {
        try {
            context.getPackageManager()
                    .getPackageInfo("com.google.android.gm", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:BarbieCornp@gmail.com"));
        } catch (Exception ex) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com/mail/u/2/#inbox?compose=new"));
        }
    }
    public static Intent openWhatsapp(Context context) {
        try {
            context.getPackageManager()
                    .getPackageInfo("com.whatsapp", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + number));
        } catch (Exception ex) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + number));
        }
    }
}