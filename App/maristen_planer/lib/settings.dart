import 'package:flutter/material.dart';
import 'package:maristen_planer/constants.dart';

final List<SettingGroup> settings = [
  SettingGroup(title: 'Allgemein', settings: [
    BoolSetting(name: 'Debug', value: true, icon: const Icon(Icons.bug_report)),
    BoolSetting(name: 'Debug2', value: true, icon: const Icon(Icons.bug_report)),
    EnumSetting(name: 'Enum', enumName: '_Hi', values: _Hi.values, value: _Hi.Hi, icon: const Icon(Icons.emoji_people)),
    EnumSetting(name: 'Enum2', enumName: '_Hi', values: _Hi.values, value: _Hi.Bye, icon: const Icon(Icons.emoji_people))
  ])
];
enum _Hi { Hi, Hello, Bye }

class SettingGroup {
  final String title;
  final List<Setting> settings;
  bool visible = true;

  SettingGroup({
    required this.title,
    required this.settings
  });
}

abstract class Setting<T> {
  final Icon icon;

  final String name;
  T value;

  ListTile settingRow(BuildContext context);

  Setting(this.name, this.value, this.icon);
}

class BoolSetting extends Setting<bool> {
  @override
  ListTile settingRow(BuildContext context) => ListTile(
    leading: icon,
    title: Text(name),
    trailing: Switch(value: value, onChanged: (newValue) {
      _settingsState.setState(() {
        value = newValue;
      });
    }),
  );

  BoolSetting({
    required String name,
    required bool value,
    required Icon icon,
  }) : super(name, value, icon);
}

class EnumSetting<T> extends Setting<T> {
  final String enumName;
  final int _enLength;
  final List<T> values;

  @override
  ListTile settingRow(BuildContext context) => ListTile(
    leading: icon,
    title: Text(name),
    trailing: Icon(Icons.navigate_next),
    onTap: () {
      Navigator.push(context, MaterialPageRoute(builder: (context) => _EnumSettingScreen(this, name, enumName, _enLength, values, value)));
    },
  );

  EnumSetting({
    required String name,
    required this.enumName,
    required this.values,
    required T value,
    required Icon icon
  }) : _enLength = enumName.length + 1, super(name, value, icon);
}

class _EnumSettingScreen<T> extends StatefulWidget {
  final EnumSetting<T> setting;
  final String title;
  final String enumName;
  final List<String> values;
  final String value;

  @override
  createState() => _EnumSettingScreenState(setting, title, values, value);

  _EnumSettingScreen(this.setting, this.title, this.enumName, int _enLength, List<T> values, T value)
  : this.values = values.map((e) => e.toString().substring(_enLength)).toList(),
        this.value = value.toString().substring(_enLength);
}

class _EnumSettingScreenState extends State<_EnumSettingScreen> {
  final EnumSetting setting;
  final String title;
  final List<String> values;
  String value;

  bool lockInBackground = true;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(title)),
      body: ListView.builder(
        padding: const EdgeInsets.all(10.0),
        itemCount: values.length * 2 - 1,
        itemBuilder: (context, i) {
          if (i.isOdd) return const Divider();
          final index = i ~/ 2;
          return ListTile(
            title: Text(values[index]),
            trailing: values[index] == value ? Icon(Icons.check) : null,
            onTap: () {
              setState(() {
                value = values[index];
                setting.value = setting.values[index];
              });
              //Navigator.pop(context);
            },
          );
        })
    );
  }

  _EnumSettingScreenState(this.setting, this.title, this.values, this.value);
}

/*Widget _settingsWidget(SettingGroup group, BuildContext context) => ListView.builder(
    padding: const EdgeInsets.all(10.0),
    itemCount: group.settings.length * 2 - 1,
    itemBuilder: (context, i) {
      if (i.isOdd) return const Divider();
      final index = i ~/ 2;
      return group.settings[index].settingRow(context);
    }
);*/

Widget settingsWidgets(BuildContext context) {
  final items = <ListTile>[];
  for (var group in settings) {
    items.add(ListTile(
      title: Text(group.title, style: TextStyle(color: maristenBlueLight, fontWeight: FontWeight.bold)),
      leading: Icon(group.visible ? Icons.expand_less : Icons.expand_more),
      onTap: () {
        _settingsState.setState(() {
          group.visible = !group.visible;
        });
      },
    ));
    if (group.visible) {
      for (var setting in group.settings) {
        items.add(setting.settingRow(context));
      }
    }
  }
  return ListView.builder(
      itemCount: items.length * 2 - 1,
      itemBuilder: (context, i) {
        if (i.isOdd) return const Divider();
        final index = i ~/ 2;
        return items[index];
      }
  );
}

class SettingsScreen extends StatefulWidget {
  @override
  _SettingsScreenState createState() => _settingsState = _SettingsScreenState();
}

late _SettingsScreenState _settingsState;

class _SettingsScreenState extends State<SettingsScreen> {
  bool lockInBackground = true;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Einstellungen'),),
      body: settingsWidgets(context)
    );
  }
}