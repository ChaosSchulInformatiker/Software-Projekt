import 'package:flutter/material.dart';
import 'package:maristen_planer/utils.dart';

import '../requests.dart';

// Für Kayra

Widget _buildSchedule(List<dynamic> lessons) {
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
                DataCell(Text('')),
                DataCell(Text('')),
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

  return table;
}

/*Widget _buildSchedule(List<dynamic> lessons) {
  final column = Column(children: [
    Text('${dayOfSchedule()}:',
        style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold))
  ]);

  if (lessons.isEmpty) {
    column.children.add(Text('Kein Unterricht heute'));
    return column;
  }

  for (Json? lesson in lessons) {
    if (lesson == null)
      column.children.add(Text('-'));
    else
      column.children
          .add(Text('${lesson['subject']} bei ${lesson['teacher']}'));
  }

  return column;
}
*/
late Future<Json> schedule;

void initSchedule() {
  schedule = fetchSchedule();
}

Widget? _widget;

Widget scheduleWidget() =>
    _widget ??
    FutureBuilder<Json>(
        future: schedule,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            final List<dynamic> lessons = snapshot.data!['result'][0]['days']
                [scheduleTodayIndex()]['lessons'];

            return _widget = Center(child: _buildSchedule(lessons));
          } else if (snapshot.hasError) {
            return Text("Fehler: ${snapshot.error}",
                style:
                    TextStyle(color: Colors.red, fontWeight: FontWeight.bold));
          }

          return CircularProgressIndicator();
        });
