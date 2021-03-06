

import 'package:flutter/material.dart';
import 'package:maristen_planer/constants.dart';
import 'package:maristen_planer/utils.dart';
import 'package:maristen_planer/widgets/classselection.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../requests.dart';

String? clazz;
String? subjects;

// Für Kayra

Widget _buildSchedule(State state, List<dynamic> lessons) {
  final rows = <DataRow>[];
  var i = 0;
  for (Json? lesson in lessons) {
    ++i;
    final li = i == 7 ? 'MP' : i > 7 ? '${i - 1}' : '$i';
    if (lesson == null)
      rows.add(
          DataRow(
              color: MaterialStateProperty.resolveWith<Color>((Set<MaterialState> states) {
                return Colors.white10;
              }),
              cells: <DataCell>[
                DataCell(Text(li)),
                DataCell(Text('-')),
                DataCell(Text('-')),
                DataCell(Text('-')),
              ]
          )
      );
    else {
      final bool substituted = lesson['substituted'];
      var teacher = lesson['teacher'];
      String room = lesson['room'];
      if (teacher == "null") teacher = "Entfällt";
      if (room.endsWith('"')) room = room.substring(0, room.length - 1);
      if (room == 'null') room = '-';
      final row = DataRow(
          color: substituted ? MaterialStateProperty.resolveWith<Color>((Set<MaterialState> states) {
            return teacher == "Entfällt" ? Colors.green.shade900 : Colors.yellow.shade900;
          }) : null,
          cells: <DataCell>[
            DataCell(Text(li)),
            DataCell(Text(lesson['subject'])),
            DataCell(Text(teacher)),
            DataCell(Text(room)),
          ]
      );
      rows.add(row);
    }
  }
  final table = DataTable(
    dataRowHeight: 40,
    columns: const <DataColumn>[
      DataColumn(
        label: Text(
          'Stunde',
          style: TextStyle(fontStyle: FontStyle.italic),
        ),
      ),
      DataColumn(
        label: Text(
          'Fach',
          style: TextStyle(fontStyle: FontStyle.italic),
        ),
      ),
      DataColumn(
        label: Text(
          'Lehrer',
          style: TextStyle(fontStyle: FontStyle.italic),
        ),
      ),
      DataColumn(
        label: Text(
          'Raum',
          style: TextStyle(fontStyle: FontStyle.italic),
        ),
      ),
    ],
    rows: rows
   );

  print(dayOfSchedule(_scheduleDay));
  return Column(
    children: <Widget>[
      Row(
        children: <Widget>[
          GestureDetector(
            child: const Icon(Icons.chevron_left),
            onTap: () {
              --_scheduleDay;
              if (_scheduleDay < 1) _scheduleDay = 4;
              _refresh(state);
            },
          ),
          Text(
            dayOfSchedule(_scheduleDay),
            style: const TextStyle(fontSize: 30, fontWeight: FontWeight.bold, color: maristenBlueLight),
          ),
          GestureDetector(
            child: const Icon(Icons.chevron_right),
            onTap: () => _incSD(state),
          )
        ],
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
      ),
      GestureDetector(
        child: table,
        onHorizontalDragEnd: (details) {
          if (details.primaryVelocity!.abs() < 0.1) return;
          if (details.primaryVelocity! > 0) _decSD(state);
          else _incSD(state);
        },
      ),
      //table
    ],
  );
}

late Future<Json> schedule;

void initSchedule(BuildContext context) {
  schedule = _fetchSchedule(context);
}

Widget? _widget;

int _scheduleDay = scheduleTodayIndex();
void _decSD(State state) {
  --_scheduleDay;
  if (_scheduleDay < 0 || _scheduleDay > 4) _scheduleDay = 4;
  _refresh(state);
}
void _incSD(State state) {
  ++_scheduleDay;
  if (_scheduleDay == 5) _scheduleDay = 0;
  else if (_scheduleDay > 5) _scheduleDay = 1;
  _refresh(state);
}

Widget scheduleWidget(State state) =>
    _widget ??
    FutureBuilder<Json>(
        future: schedule,
        builder: (context, snapshot) {
          print(snapshot);
          if (snapshot.hasData) {
            final List<dynamic> lessons = snapshot.data!['result'][0]['lessons']; //['days'][scheduleTodayIndex()]

            return _widget =
              RefreshIndicator(
                child: SingleChildScrollView(
                  scrollDirection: Axis.vertical,
                  child: SingleChildScrollView(scrollDirection: Axis.horizontal, child: Center(child: _buildSchedule(state, lessons)), physics: const AlwaysScrollableScrollPhysics()),
                  physics: const AlwaysScrollableScrollPhysics(),
                  clipBehavior: Clip.hardEdge,
                ),
                onRefresh: () async {
                  _refresh(state);
                },
              )
            ;
          } else if (snapshot.hasError) {
            return Text("Fehler: ${snapshot.error}",
                style:
                    TextStyle(color: Colors.red, fontWeight: FontWeight.bold));
          }

          return Center(child: CircularProgressIndicator());
        }
    );

void _refresh(State state) async {
  await (schedule = _fetchSchedule(state.context));
  state.setState(() {
    _widget = null;
  });
}

Future<Json> _fetchSchedule(BuildContext context) async {
    if (clazz == null || subjects == null) {
      final prefs = await SharedPreferences.getInstance();
      clazz = prefs.getString("class");
      subjects = prefs.getString("subjects");
    }
    return fetchSchedule(_scheduleDay, clazz!, subjects!);
}
