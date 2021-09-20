import 'package:flutter/material.dart';
import 'package:maristen_planer/constants.dart';
import 'package:maristen_planer/main.dart';
import 'package:maristen_planer/requests.dart';
import 'package:maristen_planer/widgets/schedule.dart' as S;
import 'package:shared_preferences/shared_preferences.dart';

class SubjectSelection extends StatefulWidget {
  final Map<String, dynamic> subjects;
  final String selectedClass;
  SubjectSelection(this.subjects, this.selectedClass);
  @override
  _SubjectSelectionState createState() => _SubjectSelectionState(subjects, selectedClass);
}

class _SubjectSelectionState extends State<SubjectSelection> {
  final Map<String, dynamic> subjects;
  final String selectedClass;
  _SubjectSelectionState(this.subjects, this.selectedClass);

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
                children: (() {
                  final list = <CheckboxListTile>[];
                  subjects.forEach((v, k) {
                    final checked = selectedSubjects.contains(v);
                    list.add( CheckboxListTile(
                      value: checked,
                      title: Text("$v ($k)"),
                      controlAffinity: ListTileControlAffinity.leading,
                      onChanged: (checked) {
                        setState(() {
                          if (checked == true) {
                            selectedSubjects.add(v);
                          } else if (checked == false) {
                            selectedSubjects.remove(v);
                          }
                        });
                      },
                    ));
                  });
                  return list;
                  } )(),
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
                    S.clazz = selectedClass;
                    S.subjects = selectedSubjects.join(',');
                    (() async {
                      final prefs = await SharedPreferences.getInstance();
                      prefs.setString("class", S.clazz!);
                      prefs.setString("subjects", S.subjects!);
                    } )();
                    // Für debugging zwecke weggelassen
                    //request("/change_class_data?id=$id&class=$selectedClass&subjects=${S.subjects}");
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
