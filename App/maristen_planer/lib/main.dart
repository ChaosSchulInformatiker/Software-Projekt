import 'package:flutter/material.dart';
import 'package:maristen_planer/auth/login.dart';
import 'package:maristen_planer/constants.dart';
import 'package:maristen_planer/properties.dart';
import 'package:maristen_planer/settings.dart';
import 'package:maristen_planer/utils.dart';
import 'package:maristen_planer/widgets/classselection.dart';
import 'package:maristen_planer/widgets/schedule.dart';
import 'package:maristen_planer/widgets/sidebar.dart';
import 'package:maristen_planer/widgets/mensaplan.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  settings.length; // Load settings
  id = await getAccountId();
  final themeMode = themeDataOfMode(await settings[0].settings[0].getValue());
  print(themeMode);
  runApp(MaterialApp(
    theme: darkTheme,
    darkTheme: darkTheme,
    themeMode: themeMode,
    home: id == -1 ? LoginScreen() : MyApp(),
  ));
}

int id = -1;

final MyApp app = MyApp();

class MyApp extends StatefulWidget {
  MyApp({Key? key}) : super(key: key);

  @override
  State createState() => _mainState;
}

final _mainState = _MyAppState();

class _MyAppState extends State<MyApp> {
  //Indexing the menus for the BottomNavigationBar
  int _selectedIndex = 0;

  int _scheduleDay = todayIndex();

  static const TextStyle optionStyle =
      TextStyle(fontSize: 30, fontWeight: FontWeight.bold);
  /*static final List<Widget> _widgetOptions = <Widget>[
    Row(children: <Widget>[
      GestureDetector(
        child: Icon(Icons.chevron_left),
        onTap: () => --_scheduleDay,
      ),
      Text(
        '${dayOfSchedule(_scheduleDay)}:',
        style: optionStyle,
      ),
      Icon(Icons.chevron_right),
    ]),
    Text('Index 1: Mensaplan', style: optionStyle),
    Text('Index 2: Vertretungsplan', style: optionStyle),
  ];*/

  Widget _body() {
    switch (_selectedIndex) {
      case 0:
        return scheduleWidget(this);
      case 1:
        return Mensaplan();
    }
    return Text('Not implemented yet', style: TextStyle(color: Colors.amber));
  }

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  void initState() {
    super.initState();
    initSchedule();
  }

  //Building the App itself
  @override
  Widget build(BuildContext context) => Scaffold(
    drawer: SideDrawer(),
    appBar: AppBar(
      title: Text('MaristenPlaner'),
    ),
    body: /*Column(
      children: [
        _widgetOptions[_selectedIndex],*/
        _body(),
      //],
    //),

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
      ],
      currentIndex: _selectedIndex,
      selectedItemColor: maristenBlueLight,
      onTap: _onItemTapped,
    ),
  );
}
