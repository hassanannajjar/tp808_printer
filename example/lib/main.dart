import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:tp808_printer/tp808_printer.dart';

void main() {
  Printer808.instance.checkStatus();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

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
  int _counter = 0;

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              'You have pushed the button this many times:',
            ),
            Text(
              '$_counter',
              style: Theme.of(context).textTheme.headlineMedium,
            ),
          ],
        ),
      ),
      floatingActionButton: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          FloatingActionButton(
            onPressed: Printer808.instance.checkStatus,
            tooltip: 'Check status',
            child: const Icon(Icons.usb),
          ),
          FloatingActionButton(
            onPressed: () async {
              final Uint8List imageBytes = (await NetworkAssetBundle(
                Uri.parse(
                  'https://semicolon-ltd.com/assets/img_blogs/thermal-bill-semicolonLtd-FlyAcc.png',
                ),
              ).load(
                'https://semicolon-ltd.com/assets/img_blogs/thermal-bill-semicolonLtd-FlyAcc.png',
              ))
                  .buffer
                  .asUint8List();
              Printer808.instance.printImage(imageBytes: imageBytes);
            },
            tooltip: 'Print',
            child: const Icon(Icons.print),
          ),
        ],
      ),
    );
  }
}
