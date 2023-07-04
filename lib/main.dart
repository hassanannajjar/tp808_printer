import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  void initState() {
    super.initState();
    _checkPrinterStatus();
  }

  static const platform = MethodChannel('jigsaw.gaza.dev/tp808_printer');
  String _printerStatus = 'Check printer';
  String _inputText =
      'https://semicolon-ltd.com/assets/img_blogs/thermal-bill-semicolonLtd-FlyAcc.png';

  Future<void> _checkPrinterStatus() async {
    String printerStatus;
    try {
      printerStatus = await platform.invokeMethod('connectUsb');
    } on PlatformException catch (e) {
      printerStatus = "Failed to read: '${e.message}'.";
    }

    setState(() {
      _printerStatus = printerStatus;
    });
  }

  Future<void> _printTestText() async {
    String printerStatus;
    try {
      printerStatus = await platform.invokeMethod('printTestText');
    } on PlatformException catch (e) {
      printerStatus = "Failed to read: '${e.message}'.";
    }
    setState(() {
      _printerStatus = printerStatus;
    });
  }

  Future<void> _printTestPage() async {
    String imageUrl =
        'https://semicolon-ltd.com/assets/img_blogs/thermal-bill-semicolonLtd-FlyAcc.png';
    final Uint8List imageBytes =
        (await NetworkAssetBundle(Uri.parse(imageUrl)).load(imageUrl))
            .buffer
            .asUint8List();
    String printerStatus;
    try {
      printerStatus = await platform.invokeMethod('printTestImage', {
        'bitmap': imageBytes,
        'light': 0,
        'size': 550,
        'isRotate': false,
        'sype': 0,
      });
    } on PlatformException catch (e) {
      printerStatus = "Failed to read: '${e.message}'.";
    }
    setState(() {
      _printerStatus = printerStatus;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            TextField(
              onChanged: (value) {
                setState(() {
                  _inputText = value;
                });
              },
              decoration: InputDecoration(
                labelText: 'Enter some text',
              ),
            ),
            SizedBox(height: 16.0),
            Text(
              _printerStatus,
              style: Theme.of(context).textTheme.headlineMedium,
            ),
          ],
        ),
      ),
      floatingActionButton: Row(
        mainAxisSize: MainAxisSize.max,
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          FloatingActionButton(
            onPressed: _checkPrinterStatus,
            tooltip: 'Print test page',
            child: const Icon(Icons.checkroom_outlined),
          ),
          FloatingActionButton(
            onPressed: _printTestText,
            tooltip: 'Print test page',
            child: const Icon(Icons.print),
          ),
          FloatingActionButton(
            onPressed: _printTestPage,
            tooltip: 'Print test page',
            child: const Icon(Icons.image),
          ),
        ],
      ),
    );
  }
}
