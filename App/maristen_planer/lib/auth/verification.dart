import 'package:flutter/material.dart';
import '../main.dart';
import '../utils.dart';

class VerificationScreen extends StatefulWidget {
  final Future<Json> response;

  @override
  _VerificationScreenState createState() => _VerificationScreenState(response);

  VerificationScreen(this.response);
}

class _VerificationScreenState extends State<VerificationScreen> {
  final Future<Json> response;

  Widget _body(String status, String? fName, String? lName, int? id) {
    switch (status) {
      case "SUCCESS":
        print(id);
        WidgetsBinding.instance?.addPostFrameCallback((_) =>
            Navigator.of(context).push(MaterialPageRoute(
                builder: (BuildContext context) => MyApp()
            ))
        );
        return Text('Login erfolgreich! Sie werden in Kürze weitergeleitet.');
      case "WRONG_CODE":
        return Text('Der Code war falsch! Bitte versuchen Sie es noch einmal.');
      case "WRONG_EMAIL":
        return Text('Falsche E-Mail.');
    }
    return Text('...');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: FutureBuilder<Json>(
          future: response,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final Json result = snapshot.data!['result'][0];
              return Center(
                child: Column(
                  children: <Widget>[
                    _body(result['status'], result['first_name'], result['last_name'], result['id']),
                    ElevatedButton(onPressed: () {
                      Navigator.pop(context);
                    }, child: Text('Zurück'))
                  ]
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
    );
  }

  _VerificationScreenState(this.response);
}