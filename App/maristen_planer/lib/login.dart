import 'package:flutter/material.dart';
import 'package:maristen_planer/constants.dart';
import 'package:maristen_planer/main.dart';
import 'package:maristen_planer/requests.dart';

class LoginScreen extends StatefulWidget {
  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState  extends State<LoginScreen> {
  final TextEditingController fNameController = TextEditingController();
  final TextEditingController lNameController = TextEditingController();
  final TextEditingController eMailController = TextEditingController();
  bool editedEmail = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('MaristenPlaner'),
      ),
      body: Padding(
        padding: EdgeInsets.all(10),
        child: ListView(
          children: <Widget>[
            Container(
              alignment: Alignment.center,
              padding: EdgeInsets.all(10),
              child: Text(
                'MaristenPlaner Login',
                style: TextStyle(
                    color: Colors.blue,
                    fontWeight: FontWeight.w500,
                    fontSize: 30
                ),
              ),
            ),
            Container(
              padding: EdgeInsets.all(10),
              child: TextField(
                controller: fNameController,
                decoration: InputDecoration(
                  border: OutlineInputBorder(),
                  labelText: 'Vorname',
                ),
                onChanged: (s) {
                  if (!editedEmail) {
                    eMailController.text = getEmailAccount(
                        s, lNameController.text);
                  } else if (eMailController.text == getEmailAccount(
                      s, lNameController.text)) {
                    editedEmail = false;
                  }
                },
              ),
            ),
            Container(
              padding: EdgeInsets.all(10),
              child: TextField(
                controller: lNameController,
                decoration: InputDecoration(
                  border: OutlineInputBorder(),
                  labelText: 'Nachname',
                ),
                onChanged: (s) {
                  if (!editedEmail) {
                    eMailController.text = getEmailAccount(
                        fNameController.text, s);
                  } else if (eMailController.text == getEmailAccount(
                      fNameController.text, s)) {
                    editedEmail = false;
                  }
                },
              ),
            ),
            Container(
              padding: EdgeInsets.all(10),
              child: TextField(
                controller: eMailController,
                decoration: InputDecoration(
                  border: OutlineInputBorder(),
                  labelText: 'Schul E-Mail',
                ),
                onChanged: (s) {
                  editedEmail = true;
                },
              ),
            ),
            Container(
                height: 50,
                padding: EdgeInsets.fromLTRB(10, 10, 10, 0),
                child: ElevatedButton(
                  child: Text('Login'),
                  onPressed: () {
                    print(fNameController.text);
                    getEmailAccount(fNameController.text, lNameController.text);
                    print(eMail);
                    Navigator.of(context).push(MaterialPageRoute(
                        builder: (BuildContext context) => MyApp()
                    ));
                    registerRequest(fNameController.text, lNameController.text);
                  },
                )),
          ],
        ),
      ),
    );
  }
}

String eMail = '';

String getEmailAccount(String fName, String lName) {
  final buffer = StringBuffer();
  for (var c in fName.runes)
    appendTransformedChar(buffer, c);
  buffer.write('.');
  for (var c in lName.runes)
    appendTransformedChar(buffer, c);
  buffer.write('@maristenkolleg.de');
  return buffer.toString();
}

void appendTransformedChar(StringBuffer b, int c) {
  if (c >= 0x41 && c <= 0x5a) b.write(String.fromCharCode(c + 0x20));
  else if (c >= 0x61 && c <= 0x7a) b.write(String.fromCharCode(c));
  else if (c == 0xe4 || c == 0xc4) b.write('ae');
  else if (c == 0xf6 || c == 0xd6) b.write('oe');
  else if (c == 0xfc || c == 0xdc) b.write('ue');
  else if (c == 0xdf) b.write('ss');
}
