import 'dart:io';

import 'package:flutter/material.dart';
import 'package:maristen_planer/constants.dart';
import 'package:maristen_planer/utils.dart';

import '../requests.dart';

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
              cells: <DataCell>[
                DataCell(Text(li)),
                DataCell(Text('-')),
                DataCell(Text('-')),
                DataCell(Text('-')),
              ]
          )
      );
    else
      rows.
      add(
          DataRow(
              cells: <DataCell>[
                DataCell(Text(li)),
                DataCell(Text(lesson['subject'])),
                DataCell(Text(lesson['teacher'])),
                DataCell(Text(lesson['room'])),
              ]
          )
      );
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
              if (_scheduleDay < 1) _scheduleDay = 5;
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

void initSchedule() {
  schedule = fetchSchedule();
}

Widget? _widget;

int _scheduleDay = todayIndex();
void _decSD(State state) {
  --_scheduleDay;
  if (_scheduleDay < 1 || _scheduleDay > 5) _scheduleDay = 5;
  _refresh(state);
}
void _incSD(State state) {
  ++_scheduleDay;
  if (_scheduleDay == 6) _scheduleDay = 1;
  else if (_scheduleDay > 6) _scheduleDay = 2;
  _refresh(state);
}

Widget scheduleWidget(State state) =>
    _widget ??
    FutureBuilder<Json>(
        future: schedule,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            final List<dynamic> lessons = snapshot.data!['result'][0]['days']
                [scheduleTodayIndex()]['lessons'];

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
  await (schedule = fetchSchedule());
  state.setState(() {
    _widget = null;
  });
}