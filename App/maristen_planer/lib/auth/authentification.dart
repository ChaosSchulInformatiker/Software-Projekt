import 'package:maristen_planer/auth/verification.dart';
import 'package:maristen_planer/requests.dart';
import 'package:maristen_planer/constants.dart';

import 'package:flutter/material.dart';
import '../utils.dart';

class AuthentificationScreen extends StatefulWidget {
  final Future<Json> response;

  @override
  _AuthentificationScreenState createState() => _AuthentificationScreenState(response);

  AuthentificationScreen(this.response);
}

class _AuthentificationScreenState extends State<AuthentificationScreen> {
  final Future<Json> response;

  TextEditingController authentificationKey = TextEditingController();

  Widget _body(String email, bool valid) => Padding(
    padding: EdgeInsets.all(10),
    child: valid ? ListView(
      children: <Widget>[
        Text('Es wurde ein Code an $email geschickt. Bitte geben sie den Code hier ein:',
          textAlign: TextAlign.center,
          style: TextStyle(
            fontWeight: FontWeight.w500,
            fontSize: 21
          ),
        ),
        Container(
          padding: EdgeInsets.fromLTRB(10, 15, 10, 10),
          child: TextField(
            autofocus: true,
            keyboardType: TextInputType.number,
            textAlign: TextAlign.center,
            textAlignVertical: TextAlignVertical.center,
            controller: authentificationKey,
            decoration: InputDecoration(
              border: OutlineInputBorder(),
              //labelText: 'Schul E-Mail',
            ),
            /*onChanged: (s) {
              int i = int.parse(s) ?? -1;
              if (i >= 1000000 || i < 100000) {

              }
            },*/
          ),
        ),
        Container(
            height: 50,
            padding: EdgeInsets.fromLTRB(10, 10, 10, 0),
            child: ElevatedButton(
              style: ButtonStyle(backgroundColor: MaterialStateProperty.all<Color>(maristenBlueLight)),
              child: Text('Verifizieren'),
              onPressed: () {
                Navigator.of(context).push(MaterialPageRoute(
                    builder: (BuildContext context) => VerificationScreen(loginRequest(email, authentificationKey.text))
                ));
              },
            )
        ),
      ],
    ) :
    ListView(
      children: <Widget>[
        Text('Die angegebene E-Mail Adresse ist leider nicht gültig: $email',
          textAlign: TextAlign.center,
          style: TextStyle(
            fontWeight: FontWeight.w500,
            fontSize: 21,
            color: Colors.red
          ),
        ),
      ]
    ),
  );

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Authentifizierung'),
      ),
      body: FutureBuilder<Json>(
        future: response,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            final Json result = snapshot.data!['result'][0];
            //email = result['e_mail'];
            //valid = result['valid'];
            return _body(result['e_mail'], result['valid']);
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

  _AuthentificationScreenState(this.response);
}