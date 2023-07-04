library tp808_printer;

import 'package:flutter/services.dart';

/// A Calculator.
class Printer808 {
  static const platform = MethodChannel('jigsaw.gaza.dev/tp808_printer');
  Future<String> checkStatus() async {
    try {
      return await platform.invokeMethod('connectUsb');
    } on PlatformException catch (e) {
      return "Failed to read: '${e.message}'.";
    }
  }

  /// pass simple text to print it
  Future<String> printText(String? text) async {
    try {
      return await platform.invokeMethod(
        'printText',
        {'text': text ?? '\n \n \n \n Jigsaw text \n \n \n \n'},
      );
    } on PlatformException catch (e) {
      return "Failed to read: '${e.message}'.";
    }
  }

  /// example for the image bytes from internet
  // final Uint8List imageBytes =
  //     (await NetworkAssetBundle(Uri.parse('https://semicolon-ltd.com/assets/img_blogs/thermal-bill-semicolonLtd-FlyAcc.png')).load('https://semicolon-ltd.com/assets/img_blogs/thermal-bill-semicolonLtd-FlyAcc.png'))
  //         .buffer
  //         .asUint8List();
  Future<String> printImage({
    Uint8List? imageBytes,
    int size = 550,
  }) async {
    try {
      return await platform.invokeMethod('printImage', {
        'bitmap': imageBytes,
        'size': size,
      });
    } on PlatformException catch (e) {
      return "Failed to read: '${e.message}'.";
    }
  }
}
