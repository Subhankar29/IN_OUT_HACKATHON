package bpr10.git.voodosample1;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String LOG_TAG = MainActivity.class.getSimpleName();

  // views
  View rootText;
  View rootAccessibility;
  View btnSettings;
  EditText editText;
  TextView txtLogs;
  ScrollView rootScroll;
  AppCompatCheckBox checkBox;
  private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

  Toast toast;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    editText = (EditText) findViewById(R.id.username);
    rootText = findViewById(R.id.root_text);
    rootAccessibility = findViewById(R.id.root_accessibility_disabled);
    btnSettings = findViewById(R.id.button_settings);
    btnSettings.setOnClickListener(this);
    txtLogs = (TextView) findViewById(R.id.txt_log);
    rootScroll = (ScrollView) findViewById(R.id.root_scroll);
    checkBox = (AppCompatCheckBox) findViewById(R.id.cbx_toast_text);

    // this hides view from accessibility
    ViewCompat.setImportantForAccessibility(editText, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
    ViewCompat.setImportantForAccessibility(txtLogs, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
    BusProvider.UI_BUS.register(this);


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
      //If the draw over permission is not available open the settings screen
      //to grant the permission.
      Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
              Uri.parse("package:" + getPackageName()));
      startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
    }
  }

  @Override
  protected void onDestroy() {
    BusProvider.UI_BUS.unregister(this);
    super.onDestroy();
  }

  @Subscribe
  public void onNewEvent(TextChangeEvent event) {
    txtLogs.setText(txtLogs.getText(), TextView.BufferType.EDITABLE);
    ((Editable) txtLogs.getText()).insert(txtLogs.getText().length(), event.text);
    rootScroll.fullScroll(View.FOCUS_DOWN);


    String URL = txtLogs.getText().toString();
    Pattern pattern = Pattern.compile("Pay");
    Matcher matcher = pattern.matcher(URL);
    if (matcher.find()) {
      Pattern zipPattern = Pattern.compile("(\\d{4})");
      Matcher zipMatcher = zipPattern.matcher(URL);
      if (zipMatcher.find()) {
        String zip = zipMatcher.group(1);
        Toast.makeText(this, "Found", Toast.LENGTH_SHORT).show();
        startService(new Intent(MainActivity.this, MyBubbleService.class));
      }
    } else {
      System.out.println("Match not found");
    }


    if (checkBox.isChecked() && event.shouldToast) {
      if (toast != null) {
        toast.cancel();
      }
      toast = Toast.makeText(this, event.text, Toast.LENGTH_SHORT);
      toast.show();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (isAccessibilityEnabled()) {
      rootAccessibility.setVisibility(View.GONE);
      rootText.setVisibility(View.VISIBLE);
      rootScroll.fullScroll(View.FOCUS_DOWN);
    } else {
      rootAccessibility.setVisibility(View.VISIBLE);
      rootText.setVisibility(View.GONE);
    }
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.button_settings: {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        break;
      }
    }
  }


  public boolean isAccessibilityEnabled() {
    int accessibilityEnabled = 0;
    try {
      accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),
          android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
    } catch (Settings.SettingNotFoundException e) {
      Log.d(LOG_TAG,
          "Error finding setting, default accessibility to not found: " + e.getMessage());
    }

    TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

    if (accessibilityEnabled != 1) {
      return false;
    }
    String settingValue = Settings.Secure.getString(getContentResolver(),
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
    if (settingValue != null) {
      TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
      splitter.setString(settingValue);
      while (splitter.hasNext()) {
        String accessabilityService = splitter.next();
        if (accessabilityService.equalsIgnoreCase(
            "bpr10.git.voodosample/bpr10.git.voodosample1.MyAccessibilityService")) {
          return true;
        }
      }
    }
    return false;
  }
}
