import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  MyApp({Key key}) : super(key: key);

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String message = "No Message from Native App";
  String messageFromNative = "Function invoking from Native";
  static const platformScan = const MethodChannel('scanchannel');
  static const getBonded = const MethodChannel('bondchannel');
  BluetoothDevice device;
  List<BluetoothDevice> btDevicelist = [];
  List<dynamic> bondedDevicelist = [];

  Future<void> bondDevice() async {
    try {
      bondedDevicelist = await getBonded.invokeMethod('getBondeddevice');
      print(' Bonded Device : $bondedDevicelist');
    } on PlatformException catch (e) {
      print("error + '${e.message}' ");
      message = "Failed to get Native App function: '${e.message}'.";
    }
    setState(() {
      message = bondedDevicelist.toString();
    });
  }

  Future<void> scanDevice() async {
    try {
      btDevicelist =
          await platformScan.invokeMethod('scanDeviceNativeFunction');
      print(' Bluetooth device : $btDevicelist');
    } on PlatformException catch (e) {
      print("error + '${e.message}' ");
      message = "Failed to get Native App function: '${e.message}'.";
    }
    setState(() {
      message = messageFromNative;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text('Native Demo'),
        ),
        body: Center(
          child: Column(
            children: [
              Text(message),
              ElevatedButton(child: Text('BONDED'), onPressed: bondDevice),
              ElevatedButton(child: Text('SCAN'), onPressed: scanDevice),
              Expanded(
                child: ListView.builder(
                  itemCount: bondedDevicelist.length,
                  itemBuilder: (BuildContext ctxt, int index) {
                    // return new Text(data[index]);
                    return Card(
                      child: Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Text(
                          bondedDevicelist[index].toString(),
                          style: TextStyle(fontSize: 22.0),
                        ),
                      ),
                    );
                  },
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}

class BluetoothDevice {}
