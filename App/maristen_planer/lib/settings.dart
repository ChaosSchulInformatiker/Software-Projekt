import 'package:flutter/material.dart';
import 'package:maristen_planer/languages.dart';
import 'package:maristen_planer/main.dart';
import 'package:maristen_planer/widgets/sidebar.dart';
import 'package:settings_ui/settings_ui.dart';
/*
final List<Setting> settings = [
  BoolSetting(name: 'Debug', value: true, icon: const Icon(Icons.terrain)),
  BoolSetting(name: 'Debug2', value: true, icon: const Icon(Icons.terrain))
];

abstract class Setting<T> {
  final String name;
  T value;

  Widget settingRow();

  Setting(this.name, this.value);
}

class BoolSetting extends Setting<bool> {
  final Icon icon;

  @override
  Widget settingRow() => ListTile(
    leading: icon,
    title: Text(name),
    trailing: Switch(value: value, onChanged: (newValue) {
      settingsState.setState(() {
        value = newValue;
      });
    }),
  );

  BoolSetting({
    required String name,
    required bool value,
    required this.icon
  }) : super(name, value);
}

Widget settingsWidget() => ListView.builder(
    padding: const EdgeInsets.all(16.0),
    itemCount: settings.length * 2 - 1,
    itemBuilder: (context, i) {
      if (i.isOdd) return const Divider();

      final index = i ~/ 2;

      return settings[index].settingRow();
    });


class _SettingsState extends State<MyApp> {
  @override
  Widget build(BuildContext context) => MaterialApp(
    title: 'MaristenPlaner Settings',
    theme: ThemeData(primarySwatch: Colors.blue),
    home: Scaffold(
      drawer: SideDrawer(),
      appBar: AppBar(title: Text('Einstellungen')),
      body: settingsWidget(),
    ),
  );
}

final settingsState = _SettingsState();

void openSettings() {
  // TODO
  print('Open Settings: TODO!');
}
*/

class SettingsScreen extends StatefulWidget {
  @override
  _SettingsScreenState createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  bool lockInBackground = true;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Einstellungen'),),
      body: SettingsList(
        sections: [
          SettingsSection(
            title: 'Allgemein',
            tiles: [
              SettingsTile(
                  title: 'Sprache',
                  subtitle: 'Deutsch',
                  leading: Icon(Icons.language),
                  onTap: () {
                    Navigator.of(context).push(MaterialPageRoute(
                      builder: (BuildContext context) => LanguagesScreen()
                    ));
                  },
              )
            ],
          )
        ],
      ),
    );
  }
}