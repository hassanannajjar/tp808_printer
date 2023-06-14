package com.example.tp808_printer;

import io.flutter.embedding.android.FlutterActivity;
import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.content.Context;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import print.Print;
import print.PublicFunction;
import print.WifiTool;
// import rx.functions.Action1;
         
            // InitCombox();
            // this.spnPrinterList.setOnItemSelectedListener(new OnItemSelectedPrinter());
           
public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "jigsaw.gaza.dev/tp808_printer";
    private String ConnectType = "";
    private UsbManager mUsbManager = null;
    private Context thisCon = null;
    private UsbDevice device = null;
    private PendingIntent mPermissionIntent = null;
    private static final String ACTION_USB_PERMISSION = "com.PRINTSDKSample";
    private PublicFunction PFun = null;
    private PublicAction PAct = null;


  @Override
  public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
  super.configureFlutterEngine(flutterEngine);
  
    new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
        .setMethodCallHandler(
          (call, result) -> {
            thisCon = this.getApplicationContext();
            int flags = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
            mPermissionIntent = PendingIntent.getBroadcast(thisCon, 0, new Intent(ACTION_USB_PERMISSION), flags);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            thisCon.registerReceiver(mUsbReceiver, filter);
            PFun = new PublicFunction(thisCon);
            PAct = new PublicAction(thisCon);
            InitSetting();
            connectUSB();
            // This method is invoked on the main thread.
            if (call.method.equals("connectUsb")) {
              String text = call.arguments();
              String printerStatus = connectUSB();
              PrintTestPage(text);
            result.success(printerStatus);
            } else {
              result.notImplemented();
            }
          }
        );
  }

      private void InitSetting() {
        String SettingValue = "";
        SettingValue = PFun.ReadSharedPreferencesData("Codepage");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Codepage", "0,PC437(USA:Standard Europe)");

        SettingValue = PFun.ReadSharedPreferencesData("Cut");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Cut", "0");    //

        SettingValue = PFun.ReadSharedPreferencesData("Cashdrawer");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Cashdrawer", "0");

        SettingValue = PFun.ReadSharedPreferencesData("Buzzer");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Buzzer", "0");

        SettingValue = PFun.ReadSharedPreferencesData("Feeds");
        if (SettingValue.equals(""))
            PFun.WriteSharedPreferencesData("Feeds", "0");
    }


     private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (Print.PortOpen(thisCon, device) != 0) {
                                // txtTips.setText(thisCon.getString(R.string.activity_main_connecterr));
                                return;
                            } else
                                return;
                                // txtTips.setText(thisCon.getString(R.string.activity_main_connected));

                        } else {
                            return;
                        }
                    }
                }
                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        int count = device.getInterfaceCount();
                        for (int i = 0; i < count; i++) {
                            UsbInterface intf = device.getInterface(i);
                            //Class ID 7代表打印机
                            if (intf.getInterfaceClass() == 7) {
                                Print.PortClose();
                                // txtTips.setText(R.string.activity_main_tips);
                            }
                        }
                    }
                }
               
            } catch (Exception e) {
                // Log.e("SDKSample", (new StringBuilder("Activity_Main --> mUsbReceiver ")).append(e.getMessage()).toString());
            }
        }
    };

    private String connectUSB() {
        String printerStatus = "";
        ConnectType = "USB";
        //USB not need call "iniPort"
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
                    printerStatus = "PRINT_TAG"+ "vendorID--" + device.getVendorId() + "ProductId--" + device.getProductId();
							if (device.getVendorId()==8401&&device.getProductId()==28680){
							
                    HavePrinter = true;
                    mUsbManager.requestPermission(device, mPermissionIntent);
							}
                }
            }
        }
        if (!HavePrinter)
         printerStatus = "Can't find printer";


        return printerStatus;
    }


    public void PrintTestPage(String text) {
        try {
            PAct.BeforePrintAction();

            String strPrintText = text;
            Print.PrintText(strPrintText + "\n", 0, 0, 0);
            Print.PrintText(strPrintText + "\n", 0, 2, 0);
            Print.PrintText(strPrintText + "\n", 0, 4, 0);
            Print.PrintText(strPrintText + "\n", 0, 1, 0);

            //"UPC-A,UPC-E,EAN8,EAN13,CODE39,ITF,CODEBAR,CODE128,CODE93,QRCODE"
            Barcode_BC_UPCA();
            Barcode_BC_UPCE();
            Barcode_BC_EAN8();
            Barcode_BC_EAN13();
            Barcode_BC_CODEBAR();
            Barcode_BC_ITF();
            Barcode_BC_CODE128();
            PAct.AfterPrintAction();
        } catch (Exception e) {
            // return e;
            // Log.e("SDKSample", (new StringBuilder("Activity_Main --> onClickWIFI ")).append(e.getMessage()).toString());
        }
    }

    private int Barcode_BC_UPCA() throws Exception {
        Print.PrintText("BC_UPCA:\n");
        return Print.PrintBarCode(Print.BC_UPCA,
                "075678164125");
    }

    private int Barcode_BC_UPCE() throws Exception {
        Print.PrintText("BC_UPCE:\n");
        return Print.PrintBarCode(Print.BC_UPCE,
                "01227000009");//04252614
    }

    private int Barcode_BC_EAN8() throws Exception {
        Print.PrintText("BC_EAN8:\n");
        return Print.PrintBarCode(Print.BC_EAN8,
                "04210009");
    }

    private int Barcode_BC_EAN13() throws Exception {
        Print.PrintText("BC_EAN13:\n");
        return Print.PrintBarCode(Print.BC_EAN13,
                "6901028075831");
    }

    private int Barcode_BC_CODE93() throws Exception {
        Print.PrintText("BC_CODE93:\n");
        return Print.PrintBarCode(Print.BC_CODE93,
                "TEST93");
    }

    private int Barcode_BC_CODE39() throws Exception {
        Print.PrintText("BC_CODE39:\n");
        return Print.PrintBarCode(Print.BC_CODE39,
                "123456789");
    }

    private int Barcode_BC_CODEBAR() throws Exception {
        Print.PrintText("BC_CODEBAR:\n");
        return Print.PrintBarCode(Print.BC_CODEBAR,
                "A40156B");
    }

    private int Barcode_BC_ITF() throws Exception {
        Print.PrintText("BC_ITF:\n");
        return Print.PrintBarCode(Print.BC_ITF,
                "123456789012");
    }

    private int Barcode_BC_CODE128() throws Exception {
        Print.PrintText("BC_CODE128:\n");
        return Print.PrintBarCode(Print.BC_CODE128,
                "{BS/N:{C\014\042\070\116{A3");    // decimal 1234 = octonary 1442
    }



}


