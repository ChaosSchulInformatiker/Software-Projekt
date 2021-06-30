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

//Building the SideDrawer

class SideDrawer extends StatelessWidget{
  @override
  Widget build(BuildContext context){
    final ThemeData theme = Theme.of(context);
    final TextStyle textStyle = theme.textTheme.bodyText2!;
    final List<Widget> aboutBoxChildren = <Widget>[
      const SizedBox(height: 24),
      RichText(
          text: TextSpan(
            children: <TextSpan>[
              TextSpan(
                style: textStyle,
                text: "Die MaristenPlaner App ist eine App für Schüler & Lehrer"
                  ' mit dem Sie schnell und einfach Stundenpläne, Vertretungspläne'
                  ' und den Mensa Plan aufrufen können'
              )
            ]
          )
      )
    ];
    return Drawer(
      child: Column(
        children: <Widget>[
          DrawerHeader(
            child: Center(
              child: Text(
                'MaristenPlaner Menu',
                textAlign: TextAlign.center,
                style: TextStyle(color: Colors.white, fontSize: 25),
              )
            ),
                decoration: BoxDecoration(
                  color: Colors.blue,
                ),
          ),
        ListTile(
          leading: Icon(Icons.settings),
          title: Text('Einstellungen'),
          onTap: () => {Navigator.of(context).pop()},
          ),
        ListTile(
          leading: Icon(Icons.account_circle),
          title: Text('Accounteinstellungen'),
          onTap: () => {Navigator.of(context).pop()},
    ),
          AboutListTile(
            icon: const Icon(Icons.info),
            applicationIcon: const FlutterLogo(),
            applicationName: 'MaristenPlaner',
            applicationVersion: 'Dev Build June 2021',
            applicationLegalese: '\u{a9} 2021 MaristenPlaner Team',
            aboutBoxChildren: aboutBoxChildren,
          ),
        ],
      ),
    );
  }
}

class _MyAppState extends State<MyApp> {

  //Indexing the menus for the BottomNavigationBar

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

  //Building the App itself

  @override
  Widget build(BuildContext context) => MaterialApp(
    title: 'Stundenplan',
    theme: ThemeData(
      primarySwatch: Colors.blue
    ),
    home: Scaffold(
      drawer: SideDrawer(),
      appBar: AppBar(
        title: Text('MaristenPlaner'),
      ),
      body: Column(
        children: [
          _widgetOptions[_selectedIndex],
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

      //Building the BottomNavigationBar

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
  final response = await http.get(Uri.parse(
      //'http://loens2.com/maristenplaner/schedule/000000'
    'https://loens2.com/maristenplaner/schedule/000000'
  ));

  if (response.statusCode == 200) {
    return jsonDecode(response.body);
  } else {
    throw Exception('Failed to load schedule');
  }


}

