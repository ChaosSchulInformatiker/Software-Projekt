import 'package:flutter/material.dart';
import 'package:maristen_planer/auth/login.dart';
import 'package:maristen_planer/main.dart';
import 'package:maristen_planer/properties.dart';

import 'constants.dart';

class LoadingScreen extends StatefulWidget {
  @override
  _LoadingScreenState createState() => _LoadingScreenState();
}

class _LoadingScreenState extends State<LoadingScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Center(
            child: FutureBuilder<int?>(
              future: getAccountId(),
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  WidgetsBinding.instance?.addPostFrameCallback((_) =>
                      Navigator.of(context).pushAndRemoveUntil(MaterialPageRoute(
                          builder: (BuildContext context) => snapshot.data! == -1 ? LoginScreen() : MyApp()
                      ), (route) => false)
                  );
                  print(snapshot.data);

                  return Scaffold(
                      appBar: AppBar(
                        title: Text('Loading...'),
                        backgroundColor: maristenBlue,
                      ),
                      body: Center(
                          child: Column(
                              children: <Widget>[
                                Text('Sie werden jetzt weitergeleitet'),
                                //ElevatedButton(onPressed: () {
                                //  Navigator.pop(context);
                                //}, child: Text('Alternativer Weiter Button'))
                              ]
                          )
                      )
                  );
                } else if (snapshot.hasError) {
                  return Text("Fehler: ${snapshot.error}",
                      style:
                      TextStyle(color: Colors.red, fontWeight: FontWeight.bold));
                }

                return Center(child: CircularProgressIndicator());
              },
            )
        )
    );
  }
}