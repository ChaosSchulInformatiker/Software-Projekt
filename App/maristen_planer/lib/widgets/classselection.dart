import 'package:flutter/material.dart';
import 'package:maristen_planer/constants.dart';
import 'package:maristen_planer/main.dart';

class ClassSelection extends StatefulWidget {
  @override
  _ClassSelectionState createState() => _ClassSelectionState();
}

class _ClassSelectionState extends State<ClassSelection> {

  String dropdownValue = 'Eins';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Klassen-Auswahl'),
      ),
      body: Padding(
        padding: EdgeInsets.all(10),
        child: ListView(
          children: <Widget>[
            DropdownButton<String>(
              value: dropdownValue,
              icon: const Icon(Icons.expand_more),
              iconSize: 24,
              elevation: 16,
              style: const TextStyle(
                color: maristenBlueLight
              ),
              underline: Container(
                height: 2,
                color: maristenBlueLight,
              ),
              onChanged: (String? newValue) {
                setState(() {
                  dropdownValue = newValue!;
                });
              },
              items: <String>['Eins', 'Zwei', 'Drei'].map<DropdownMenuItem<String>>((String value) {
                return DropdownMenuItem<String>(
                  value: value,
                  child: Text(value, textAlign: TextAlign.center,),
                );
              }).toList(),
            ),
            Container(
              height: 50,
              padding: EdgeInsets.fromLTRB(10, 10, 10, 0),
                child: ElevatedButton(
                  style: ButtonStyle(backgroundColor: MaterialStateProperty.all<Color>(maristenBlueLight)),
                  child: Text('Weiter'),
                  onPressed: () {
                    Navigator.of(context).push(MaterialPageRoute(
                        builder: (BuildContext context) => MyApp(),
                    ));
                  },
                )
            )
          ],
        ),
      ),
    );
  }
}