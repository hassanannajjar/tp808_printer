import 'package:flutter/material.dart';
import 'dart:async';
import 'package:tp808_printer/tp808_printer.dart';
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
  Printer808 _printer808 = Printer808();
  @override
  void initState() {
    super.initState();
    _printer808.checkStatus();
  }

  Future<void> _printTestPage() async {
    String imageUrl =
        'https://semicolon-ltd.com/assets/img_blogs/thermal-bill-semicolonLtd-FlyAcc.png';
    final Uint8List imageBytes =
        (await NetworkAssetBundle(Uri.parse(imageUrl)).load(imageUrl))
            .buffer
            .asUint8List();
    _printer808.printImage(imageBytes: imageBytes);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            SizedBox(height: 16.0),
          ],
        ),
      ),
      floatingActionButton: Row(
        mainAxisSize: MainAxisSize.max,
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          FloatingActionButton(
            onPressed: _printer808.checkStatus,
            tooltip: 'Print test page',
            child: const Icon(Icons.checkroom_outlined),
          ),
          FloatingActionButton(
            onPressed: () => _printer808.printText('Jigsaw test '),
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
