
import 'login.dart';
import 'package:flutter/material.dart';
import 'main.dart';

class AuthentificationScreen extends StatefulWidget {
  @override
  _AuthentificationScreenState createState() => _AuthentificationScreenState();
}

class _AuthentificationScreenState extends State<AuthentificationScreen> {
  TextEditingController authentificationKey = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Authentifizierung'),
      ),
      body: Padding(
        padding: EdgeInsets.all(10),
        child: ListView(
          children: <Widget>[
            Text(
                'Es wurde ein Code an $eMail geschickt. Bitte geben sie den Code hier ein:',
              textAlign: TextAlign.center,
              style: TextStyle(
                  fontWeight: FontWeight.w500,
                  fontSize: 21
              ),

            ),
            Container(
                height: 50,
                padding: EdgeInsets.fromLTRB(10, 0, 10, 0),
                child: RaisedButton(
                  textColor: Colors.white,
                  color: Colors.blue,
                  child: Text('Eingeben'),
                  onPressed: () {
                    Navigator.of(context).push(MaterialPageRoute(
                        builder: (BuildContext context) => MyApp()
                    ));

                  },
                )),
          ],
        ),
      ),
    );
  }
}