package phone.vishnu.quotes.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;
import com.turkialkhateeb.materialcolorpicker.ColorListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.MessageButtonBehaviour;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;
import io.github.dreierf.materialintroscreen.animations.IViewTranslation;
import phone.vishnu.quotes.R;

public class SplashActivity extends MaterialIntroActivity {

    private final int PICK_IMAGE_ID = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();

        SharedPreferences sharedPreferences = getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);

        //FIXME: FIXED
        String FIRST_RUN_BOOLEAN = "firstRunPreference";
        if (sharedPreferences.getBoolean(FIRST_RUN_BOOLEAN, true)) {

         /*   if (("-1").equals(sharedPreferences.getString(BACKGROUND_PREFERENCE_NAME, "-1")))
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        sharedPreferences.edit().putString(
                                BACKGROUND_PREFERENCE_NAME,
                                DownloadImageFromPath("https://raw.githubusercontent.com/VishnuSanal/Quotes/master/background.png")
                        ).apply();
                    }
                });*/
            showTour();
            sharedPreferences.edit().putBoolean(FIRST_RUN_BOOLEAN, false).apply();
        } else {
            initTasks();
        }
    }

    private void showTour() {
        enableLastSlideAlphaExitTransition(true);

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        /*Screen 1*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_quotes)
                .title("Spread positivity with us")
                .description("Would you try?")
                .build());

        /*Permission Screen*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_check_box)
                .neededPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET})
                .title("Accept Permissions")
                .description("Accept the permissions for the app to run")
                .build());

        /*BG Image*/
        //FIXME:There's an error here
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.background)
                .title("Choose background image")
                .description("Would you like to select a background image from your phone? Do nothing to use the above default background image. You can change this later from the overflow menu on the bottom of the screen")
                .build(), new MessageButtonBehaviour(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_ID);
            }
        }, "Choose Image"));

        /*Accent Color*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.color.cardBackgroundColor)
                .title("Choose accent color")
                .description("Would you like to select a custom accent color for the app? Do nothing to use the above default accent color. You can change this later from the overflow menu on the bottom of the screen")
                .build(), new MessageButtonBehaviour(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String COLOR_PREFERENCE_NAME = "colorPreference";

                final SharedPreferences prefs = SplashActivity.this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);

                ColorChooserDialog dialog = new ColorChooserDialog(SplashActivity.this);
                dialog.setTitle("Choose Color");
                dialog.setColorListener(new ColorListener() {
                    @Override
                    public void OnColorClick(View v, int color) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(COLOR_PREFERENCE_NAME, "#" + Integer.toHexString(color).substring(2));
                        editor.apply();
                    }
                });
                dialog.show();
            }
        }, "Choose Color"));

        /*Noti*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_notifications)
                .title("Daily Notification of Quotes")
                .description("You can receive daily notifications with Quotes")
                .build());

        /*Share*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_share)
                .title("Share Quotes in Social Media")
                .description("Click on this icon from a quote and select the required app from the chooser")
                .build());

        /*Fav*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_favorite)
                .title("Add a Quote to Favorites")
                .description("Click on this icon from a quote. You can view the favorite quotes by clicking on the overflow menu on the bottom of the screen")
                .build());

        /*End Screen*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .title("That's it")
                .image(R.drawable.ic_whatshot)
                .description("Get Started")
                .build());
    }

    @Override
    public void onFinish() {
        super.onFinish();
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        SplashActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICK_IMAGE_ID) && (resultCode == Activity.RESULT_OK)) {
            if (data != null) {
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    String file = generateNoteOnSD(this, bitmap);

                    SharedPreferences sharedPrefs = this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    String BACKGROUND_PREFERENCE_NAME = "backgroundPreference";
                    editor.putString(BACKGROUND_PREFERENCE_NAME, file);
                    editor.apply();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initTasks() {
        setContentView(R.layout.activity_splash);
        int SPLASH_TIMEOUT = 1;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, SPLASH_TIMEOUT * 1000);
    }

    private String generateNoteOnSD(Context context, Bitmap image) {
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Quotes");

        if (!root.exists()) root.mkdirs();

        String file = root.toString() + File.separator + ".Quotes_Background" + ".jpg";

        try {
            FileOutputStream fOutputStream = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fOutputStream);

            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            fOutputStream.flush();
            fOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaScannerConnection.scanFile(context, new String[]{file}, null, null);
        return file;
    }

 /*   private String DownloadImageFromPath(String path) {

        InputStream inputStream = null;
        Bitmap bitmap = null;
        int responseCode = -1;
        String noteOnSD = "";

        try {
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                inputStream = urlConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                noteOnSD = generateNoteOnSD(this, bitmap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return noteOnSD;
    }*/

}