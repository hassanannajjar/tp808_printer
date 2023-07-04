package com.example.tp808_printer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Iterator;
import java.util.List;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import print.Print;
import print.PublicFunction;
import print.WifiTool;

public class MainActivity extends FlutterActivity {

  private static final String CHANNEL = "jigsaw.gaza.dev/tp808_printer";
  private UsbManager mUsbManager = null;
  private Context thisCon = null;
  private UsbDevice device = null;
  private PendingIntent mPermissionIntent = null;
  private static final String ACTION_USB_PERMISSION = "com.PRINTSDKSample";
  private PublicFunction PFun = null;
  private PublicAction PAct = null;
  private ExecutorService executorService = Executors.newSingleThreadExecutor();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    thisCon = this.getApplicationContext();
    int flags =
      PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
    mPermissionIntent =
      PendingIntent.getBroadcast(
        thisCon,
        0,
        new Intent(ACTION_USB_PERMISSION),
        flags
      );
    PFun = new PublicFunction(thisCon);
    PAct = new PublicAction(thisCon);
    InitSetting();
  }

  @Override
  public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
    super.configureFlutterEngine(flutterEngine);

    new MethodChannel(
      flutterEngine.getDartExecutor().getBinaryMessenger(),
      CHANNEL
    )
      .setMethodCallHandler((call, result) -> {
        try {
          String status = connectUSB();
          result.success(status);
        } catch (Exception e) {
          result.success(e.getMessage());
        }

        // This method is invoked on the main thread.
        if (call.method.equals("connectUsb")) {
          try {
            String status = connectUSB();
            result.success(status);
          } catch (Exception e) {
            result.success(e.getMessage());
          }
        } else if (call.method.equals("printTestText")) {
          PrintTestText();
          result.success("printerStatus");
        } else if (call.method.equals("printTestImage")) {
          byte[] bitmapBytes = call.argument("bitmap");
          int size = call.argument("size");
          Bitmap bitmap = BitmapFactory.decodeStream(
            new ByteArrayInputStream(bitmapBytes)
          );
          try {
            String printerStatus = printImage(
              bitmap,
              size,
            );
            result.success(printerStatus);
          } catch (Exception e) {
            result.success(e.getMessage());
          }
        } else {
          result.notImplemented();
        }
      });
  }

  private void InitSetting() {
    String SettingValue = "";
    SettingValue = PFun.ReadSharedPreferencesData("Codepage");
    if (SettingValue.equals("")) PFun.WriteSharedPreferencesData(
      "Codepage",
      "0,PC437(USA:Standard Europe)"
    );

    SettingValue = PFun.ReadSharedPreferencesData("Cut");
    if (SettingValue.equals("")) PFun.WriteSharedPreferencesData("Cut", "0"); //

    SettingValue = PFun.ReadSharedPreferencesData("Cashdrawer");
    if (SettingValue.equals("")) PFun.WriteSharedPreferencesData(
      "Cashdrawer",
      "0"
    );

    SettingValue = PFun.ReadSharedPreferencesData("Buzzer");
    if (SettingValue.equals("")) PFun.WriteSharedPreferencesData("Buzzer", "0");

    SettingValue = PFun.ReadSharedPreferencesData("Feeds");
    if (SettingValue.equals("")) PFun.WriteSharedPreferencesData("Feeds", "0");
  }

  private String connectUSB() throws Exception {
    String printerStatus = "";
    mUsbManager = (UsbManager) thisCon.getSystemService(Context.USB_SERVICE);
    HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
    Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

    boolean HavePrinter = false;
    while (deviceIterator.hasNext()) {
      device = deviceIterator.next();
      int count = device.getInterfaceCount();
      for (int i = 0; i < count; i++) {
        UsbInterface intf = device.getInterface(i);
        if (intf.getInterfaceClass() == 7) {
          Print.PortOpen(thisCon, device);
          printerStatus = "Connected to printer => " + device.getProductName();
          HavePrinter = true;
          mUsbManager.requestPermission(device, mPermissionIntent);
        }
      }
    }
    if (!HavePrinter) printerStatus = "Can't find printer";

    return printerStatus;
  }

  public String printImage(
    final Bitmap bitmap,
    final int size,
  ) throws Exception {
    final AtomicReference<String> printStatus = new AtomicReference<>(""); // Create an AtomicReference
    executorService.execute(
      new Runnable() {
        @Override
        public void run() {
          PAct.BeforePrintAction();
          Bitmap bitmapPrint = bitmap;
          if (size != 0) bitmapPrint =
            Utility.Tobitmap(
              bitmapPrint,
              size,
              Utility.getHeight(
                size,
                bitmapPrint.getWidth(),
                bitmapPrint.getHeight()
              )
            );
          int printImage = 0;
          try {
            printImage = Print.PrintBitmap(bitmapPrint, 0, 0);
            if (printImage >= 0) {
              printStatus.set("print image succeed");
            } else {
              printStatus.set("print image Failed");
            }
            Print.PrintText("\n \n \n \n \n \n");
            bitmap.recycle();
            bitmapPrint.recycle();
            PAct.AfterPrintAction();
            Print.CutPaper(Print.PARTIAL_CUT);
          } catch (Exception e) {
            printStatus.set("print image Failed :=> " + e.getMessage());
          }
        }
      }
    );
    return printStatus.get();
  }

  public String PrintTestText() {
    try {
      PAct.BeforePrintAction();
      String strPrintText = "Print Jigsaw Kiosk test";
      Print.PrintText(strPrintText + "\n", 0, 0, 0);
      Print.PrintText(strPrintText + "\n", 0, 2, 0);
      Print.PrintText(strPrintText + "\n", 0, 4, 0);
      Print.PrintText(strPrintText + "\n", 0, 1, 0);
      PAct.AfterPrintAction();
      return "print success";
    } catch (Exception e) {
      return PAct.toString() + e.getMessage();
    }
  }
}
