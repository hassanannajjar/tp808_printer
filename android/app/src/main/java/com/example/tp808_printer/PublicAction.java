package com.example.tp808_printer;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import print.Print;
import print.PublicFunction;

public class PublicAction {
    private Context context = null;
    private final String CODEPAGE_KHEMR = "Khemr";

    public PublicAction() {

    }

    public PublicAction(Context con) {
        context = con;
    }

    public void BeforePrintAction() {
        try {
            Print.LanguageEncode = "gb2312";
        } catch (Exception e) {
            Log.e("Print", (new StringBuilder("PublicAction --> BeforePrintAction ")).append(e.getMessage()).toString());
        }
    }

    public void BeforePrintActionText() {
        try {
            PublicFunction PFun = new PublicFunction(context);
            if (!TextUtils.isEmpty(PFun.ReadSharedPreferencesData("Codepage"))) {
                String codepage = PFun.ReadSharedPreferencesData("Codepage").split(",")[1];
                String sLEncode = PFun.getLanguageEncode(codepage);
                //设置Codepage
                Print.LanguageEncode = sLEncode;
//				if (CODEPAGE_KHEMR.equals(codepage)){
//					//开启高棉语
//					Print.setKhmerSwitch(true);
//					Print.setKhemrOrder();
//				}
            }
        } catch (Exception e) {
            Log.e("Print", (new StringBuilder("PublicAction --> BeforePrintAction ")).append(e.getMessage()).toString());
        }
    }

    public void AfterPrintActionText() {
        try {
//			PublicFunction PFun=new PublicFunction(context);
//			if (!TextUtils.isEmpty(PFun.ReadSharedPreferencesData("Codepage"))){
//				String codepage = PFun.ReadSharedPreferencesData("Codepage").split(",")[1];
//				if (CODEPAGE_KHEMR.equals(codepage)){
//					//关闭高棉语
//					Print.setKhemrEnd();
//				}
//			}
        } catch (Exception e) {
            Log.e("Print", (new StringBuilder("PublicAction --> AfterPrintAction ")).append(e.getMessage()).toString());
        }
    }

    public void AfterPrintAction() {

    }

    public String LanguageEncode() {
        try {
            PublicFunction PFun = new PublicFunction(context);
            String sLanguage = PFun.ReadSharedPreferencesData("Codepage").split(",")[1].toString();
            String sLEncode = "gb2312";
            int intLanguageNum = 0;

            sLEncode = PFun.getLanguageEncode(sLanguage);
            intLanguageNum = PFun.getCodePageIndex(sLanguage);

            Print.LanguageEncode = sLEncode;
//			Print.SetCharacterSet((byte)intLanguageNum);

            return sLEncode;
        } catch (Exception e) {
            Log.e("SDKSample", (new StringBuilder("PublicAction --> AfterPrintAction ")).append(e.getMessage()).toString());
            return "";
        }
    }
}