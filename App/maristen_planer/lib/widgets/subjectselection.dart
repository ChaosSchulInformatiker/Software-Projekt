import 'package:flutter/material.dart';
import 'package:maristen_planer/constants.dart';
import 'package:maristen_planer/main.dart';

class SubjectSelection extends StatefulWidget {
  final Map<String, dynamic> subjects;
  SubjectSelection(this.subjects);
  @override
  _SubjectSelectionState createState() => _SubjectSelectionState(subjects);
}

class _SubjectSelectionState extends State<SubjectSelection> {
  final Map<String, dynamic> subjects;
  _SubjectSelectionState(this.subjects);

  final selectedSubjects = Set<String>();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Fächer-Auswahl'),
      ),
      body: Padding(
        padding: EdgeInsets.all(10),
        child: ListView(
          children: <Widget>[
            /*DropdownButton<String>(
              value: fach,
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
                  fach = newValue!;
                });
              },
              items: subjects.keys.map<DropdownMenuItem<String>>((value) {
                return DropdownMenuItem<String>(
                  value: value,
                  child: Text(value, textAlign: TextAlign.center,),
                );
              }).toList(),
            ),*/
              //ElevatedButton(onPressed: () => _showMultiSelect(context), child: Text("Open Multiselect"));
          SingleChildScrollView(
            child: ListTileTheme(
              contentPadding: EdgeInsets.fromLTRB(14.0, 0.0, 24.0, 0.0),
              child: ListBody(
                children: subjects.keys.map((value) {
                  final checked = selectedSubjects.contains(value);
                  return CheckboxListTile(
                      value: checked,
                      title: Text(value),
                      controlAffinity: ListTileControlAffinity.leading,
                      onChanged: (checked) {
                        setState(() {
                          if (checked == true) {
                            selectedSubjects.add(value);
                          } else if (checked == false) {
                            selectedSubjects.remove(value);
                          }
                        });
                      },
                  );
                }).toList(),
              ),
            ),
          ),
            Container(
                height: 50,
                padding: EdgeInsets.fromLTRB(10, 10, 10, 0),
                child: ElevatedButton(
                  style: ButtonStyle(backgroundColor: MaterialStateProperty.all<Color>(maristenBlueLight)),
                  child: Text('Weiter'),
                  onPressed: () {
                    Navigator.of(context)//..pop()..pop();
                    .push(MaterialPageRoute(builder: (c) => MyApp()));
                  },
                )
            )
          ],
        ),
      ),
    );
  }
}