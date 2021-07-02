import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:maristen_planer/utils.dart';
import 'package:http/http.dart' as http;

// Für Kayra
Widget _buildSchedule(List<dynamic> lessons) {
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

late Future<Json> schedule;

void initSchedule() {
  schedule = _fetchSchedule();
}

Widget? _widget;

Widget scheduleWidget() =>
    _widget ??
    FutureBuilder<Json>(
        future: schedule,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            final List<dynamic> lessons = snapshot.data!['result'][0]['days']
                [/*todayIndex()*/ 0]['lessons'];

            return _widget = _buildSchedule(lessons);
          } else if (snapshot.hasError) {
            return Text("Fehler: ${snapshot.error}",
                style:
                    TextStyle(color: Colors.red, fontWeight: FontWeight.bold));
          }

          return CircularProgressIndicator();
        });

Future<Json> _fetchSchedule() async {
  final response = await http.get(Uri.parse(
      //'http://loens2.com/maristenplaner/schedule/000000'
      //'https://www.loens2.com/maristenplaner/schedule/000000'
      'http://localhost:8000/schedule/000000'
      //'http://192.168.178.61:8000/schedule/000000'
      //'http://84.164.234.82:25565/schedule/000000'
  ));

  if (response.statusCode == 200) {
    return jsonDecode(response.body);
  } else {
    throw Exception('Failed to load schedule');
  }
}
