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

  int _selectedIndex = 0;
  static const TextStyle optionStyle = TextStyle(
      fontSize: 30, fontWeight: FontWeight.bold);
  static const List<Widget> _widgetOptions = <Widget>[
    Text(
      'Index 0: Stundenplan',
      style: optionStyle,
    ),
    Text(
        'Index 1: Mensaplan',
        style: optionStyle
    ),
    Text(
        'Index 2: Vertretungsplan',
        style: optionStyle
    ),
  ];

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

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
        title: Text('MaristenPlaner'),
      ),
      body: Column(
        children: [
          _widgetOptions.elementAt(_selectedIndex),
         FutureBuilder<Json>(
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
      ],
      ),
      bottomNavigationBar: BottomNavigationBar(
        items: const <BottomNavigationBarItem>[

          BottomNavigationBarItem(
            icon: Icon(Icons.date_range),
            label: 'Stundenplan',
          ),

          BottomNavigationBarItem(
            icon: Icon(Icons.restaurant_menu),
            label: 'Mensaplan',
          ),

          BottomNavigationBarItem(
            icon: Icon(Icons.event_busy),
            label: 'Vertretungsplan',
          ),

        ],
        currentIndex: _selectedIndex,
        selectedItemColor: Colors.blue,
        onTap: _onItemTapped,
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

