import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:maristen_planer/constants.dart';
import 'package:maristen_planer/properties.dart';
import 'package:maristen_planer/widgets/classselection.dart' as cs;
import 'package:maristen_planer/widgets/schedule.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../utils.dart';

class VerificationScreen extends StatefulWidget {
  final Future<Json> response;

  @override
  _VerificationScreenState createState() => _VerificationScreenState(response);

  VerificationScreen(this.response);
}

class _VerificationScreenState extends State<VerificationScreen> {
  final Future<Json> response;

  Widget _body(String status, String? fName, String? lName, int? id, String? clazz_, String? subjects_) {
    switch (status) {
      case "SUCCESS":
        print(id);
        saveAccountId(id!);
        clazz = clazz_;
        subjects = subjects_;
        WidgetsBinding.instance?.addPostFrameCallback((_) => _proceedToSelection());
        return Column(children: <Widget>[
          Text('Login erfolgreich! Sie werden in Kürze weitergeleitet.',),
          Text('Alternativ drücken Sie bitte diesen Button:'),
          ElevatedButton(
            child: Text('Weiter'),
            onPressed: _proceedToSelection
          )
        ]);
      case "WRONG_CODE":
        return Text('Der Code war falsch! Bitte versuchen Sie es noch einmal.');
      case "WRONG_EMAIL":
        return Text('Falsche E-Mail.');
    }
    return Text('...');
  }

  void _proceedToSelection() {
    Navigator.of(context).pushAndRemoveUntil(MaterialPageRoute(
        builder: (BuildContext context) => cs.ClassSelection()
    ), (route) => false);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(

        body: Center(
        child: FutureBuilder<Json>(
          future: response,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final Json result = snapshot.data!['result'][0];
              return Scaffold(
                  appBar: AppBar(
                    title: Text('Info'),
                    backgroundColor: maristenBlue,
                  ),
                  body: Center(
                    child: Column(
                      children:  (()  {
                        if (clazz == null || subjects == null) {
                        clazz = result['class'];
                        subjects = result['subjects'];
                        (() async {
                          final prefs = await SharedPreferences.getInstance();
                          if (clazz != null) prefs.setString('class', clazz!);
                          if (subjects != null) prefs.setString('subjects', subjects!);
                        } )();
                        }
                        return
                        <Widget>[
                        _body(result['status'], result['first_name'], result['last_name'], result['id'], clazz, subjects),
                        ElevatedButton(onPressed: () {
                        Navigator.pop(context);
                        }, child: Text('Zurück'))

                        ];
                      })()
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

  _VerificationScreenState(this.response);
}
