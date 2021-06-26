import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  MyApp({Key? key}) : super(key: key);

  @override
  State createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  late Future<Json> schedule;

  @override
  void initState() {
    super.initState();
    schedule = fetchSchedule();
  }

  @override
  Widget build(BuildContext context) => MaterialApp(
    title: 'Stundenplan',
    theme: ThemeData(
      primarySwatch: Colors.blue
    ),
    home: Scaffold(
      appBar: AppBar(
        title: Text('Dein Stundenplan'),
      ),
      body: Center(
        child: FutureBuilder<Json>(
          future: schedule,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              return Text(snapshot.data!.toString());
            } else if (snapshot.hasError) {
              return Text("Fehler: ${snapshot.error}", style: TextStyle(color: Colors.red));
            }

            return CircularProgressIndicator();
          },
        ),
      ),
    ),
  );
}

typedef Json = Map<String, dynamic>;

Future<Json> fetchSchedule() async {
  final response = await http.get(Uri.parse('https://www.loens2.com/schedule/000000'));

  if (response.statusCode == 200) {
    return jsonDecode(response.body);
  } else {
    throw Exception('Failed to load schedule');
  }
}