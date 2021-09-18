import 'package:flutter/material.dart';
import 'package:maristen_planer/constants.dart';
import 'package:maristen_planer/main.dart';
import 'package:maristen_planer/requests.dart';
import 'package:maristen_planer/widgets/subjectselection.dart';


class ClassSelection extends StatefulWidget {

  @override
  _ClassSelectionState createState() => _ClassSelectionState();
}

class _ClassSelectionState extends State<ClassSelection> {
  @override
  void initState() {
    super.initState();
    initSubjects();
  }


  String? klasse;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text('Klassen-Auswahl'),
        ),
        body: FutureBuilder<dynamic>(
          future: subjects,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final classes = (snapshot.data["result"]as List<dynamic>).map((e) {
                final value = e["name"] as String;
                return DropdownMenuItem<String>(
                value: value,
                child: Text(value, textAlign: TextAlign.center,),
              );
              }).toList();
              return Padding(
                padding: EdgeInsets.all(10),
                child: ListView(
                  children: <Widget>[
                    DropdownButton<String>(
                      value: klasse,
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
                          klasse = newValue!;
                        });
                      },
                      items: classes
                    ),
                    Container(
                        height: 50,
                        padding: EdgeInsets.fromLTRB(10, 10, 10, 0),
                        child: ElevatedButton(
                          style: ButtonStyle(
                              backgroundColor: MaterialStateProperty.all<Color>(
                                  maristenBlueLight)),
                          child: Text('Weiter'),
                          onPressed: () {
                            Navigator.of(context).push(MaterialPageRoute(
                              builder: (BuildContext context) =>
                                  SubjectSelection((snapshot.data["result"] as List<dynamic>).firstWhere((element) => element["name"] == klasse)["subjects"]),
                            ));
                          },
                        )
                    )
                  ],
                ),
              );
            }
            else if (snapshot.hasError) {
              return Text("Fehler: ${snapshot.error}",
                  style:
                  TextStyle(color: Colors.red, fontWeight: FontWeight.bold));
            }
            return Center(child: CircularProgressIndicator());
          },
        )
    );
  }
}




late Future<dynamic> subjects;
void initSubjects () {
  subjects = request('/classes');
}