library tp808_printer;

import 'dart:developer' as developer;
import 'package:flutter/services.dart';

/// A Calculator.
class Printer808 {
  static const MethodChannel _methodChannel =
      MethodChannel('jigsaw.gaza.dev/tp808_printer');

  static final Printer808 _instance = Printer808();

  static Printer808 get instance => _instance;

  Future<void> checkStatus() async {
    _consoleLog('value', key: 'Printer808 checkStatus');
    try {
      dynamic value = await _methodChannel.invokeMethod('connectUsb');
      _consoleLog(value, key: 'Printer808 checkStatus');
    } on PlatformException catch (e) {
      _consoleLog("Failed to read: '${e.message}'.",
          key: 'Printer808 checkStatus');
    }
  }

  /// pass simple text to print it
  Future<void> printText(String? text) async {
    try {
      dynamic value = await _methodChannel.invokeMethod(
        'printText',
        {'text': text ?? '\n \n \n \n Jigsaw text \n \n \n \n'},
      );
      _consoleLog(value, key: 'Printer808 printText');
    } on PlatformException catch (e) {
      _consoleLog("Failed to read: '${e.message}'.",
          key: 'Printer808 printText');
    }
  }

  /// example for the image bytes from internet
  // final Uint8List imageBytes =
  //     (await NetworkAssetBundle(Uri.parse('https://semicolon-ltd.com/assets/img_blogs/thermal-bill-semicolonLtd-FlyAcc.png')).load('https://semicolon-ltd.com/assets/img_blogs/thermal-bill-semicolonLtd-FlyAcc.png'))
  //         .buffer
  //         .asUint8List();
  Future<void> printImage({
    Uint8List? imageBytes,
    int size = 550,
  }) async {
    try {
      dynamic value = await _methodChannel.invokeMethod('printImage', {
        'bitmap': imageBytes,
        'size': size,
      });
      _consoleLog(value, key: 'Printer808 printImage');
    } on PlatformException catch (e) {
      _consoleLog("Failed to read: '${e.message}'.",
          key: 'Printer808 printImage');
    }
  }
}

void _consoleLog(dynamic value, {String key = 'value'}) {
  developer.log('📔:\x1B[32m ******** Log $key **********:📔');
  developer.log('\x1B[35m $key :\x1B[37m $value');
  developer.log('📓:\x1B[32m  *************** END **************:📓');
}
