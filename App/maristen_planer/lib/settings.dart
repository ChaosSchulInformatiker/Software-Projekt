import 'package:flutter/material.dart';
import 'package:maristen_planer/constants.dart';
import 'package:maristen_planer/properties.dart';

final List<SettingGroup> settings = [
  SettingGroup(title: 'Design', settings: [
    EnumSetting(name: 'Theme', enumName: 'ThemeSelection', values: ThemeSelection.values, value: ThemeSelection.System, icon: const Icon(Icons.light))
  ]),
  SettingGroup(title: 'Erweitert', settings: [
    VoidSetting(name: 'Daten zurücksetzen', action: resetData)
  ])
  //SettingGroup(title: 'Allgemein', settings: [
  //  BoolSetting(name: 'Debug', value: true, icon: const Icon(Icons.bug_report)),
  //  BoolSetting(name: 'Debug2', value: true, icon: const Icon(Icons.bug_report)),
  //  VoidSetting(name: 'Reset Data', action: resetData)
  //]),
  //SettingGroup(title: 'Special', settings: [
  //  EnumSetting(name: 'Enum', enumName: '_Hi', values: _Hi.values, value: _Hi.Hi, icon: const Icon(Icons.emoji_people)),
  //  EnumSetting(name: 'Enum2', enumName: '_Hi', values: _Hi.values, value: _Hi.Bye, icon: const Icon(Icons.emoji_people)),
  //  StringSetting(name: 'String', value: 'S', icon: const Icon(Icons.emoji_people))
  //])
];
enum _Hi { Hi, Hello, Bye }

enum ThemeSelection { System, Light, Dark }

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
  final Icon? icon;

  final String name;
  T _value;
  bool _loadedValue = false;
  
  Future<T> getValue();
  Future<void> setValue(T t);

  ListTile settingRow(BuildContext context);
  
  Setting(this.name, this._value, this.icon) {
    getValue().then((value) {
      _value = value;
      print("Setting '$name' = $value");
    });
  }
}

class BoolSetting extends Setting<bool> {
  @override
  ListTile settingRow(BuildContext context) => ListTile(
    leading: icon,
    title: Text(name),
    trailing: Switch(value: _value, onChanged: (newValue) {
      _settingsState.setState(() {
        setValue(newValue);
      });
    }),
  );

  BoolSetting({
    required String name,
    required bool value,
    Icon? icon,
  }) : super(name, value, icon);

  @override
  Future<bool> getValue() async {
    if (_loadedValue) return _value;
    _value = (await getBoolSetting(name)) ?? _value;
    _loadedValue = true;
    return _value;
  }

  @override
  Future<void> setValue(bool t) async {
    _value = t;
    await setBoolSetting(name, t);
  }
}

class VoidSetting extends Setting<void> {
  final Future<void> Function() action;

  @override
  Future<void> getValue() async {}

  @override
  Future<void> setValue(void t) async {
    action();
  }

  @override
  ListTile settingRow(BuildContext context) => ListTile(
    leading: icon,
    title: ElevatedButton(
      child: Text(name),
      onPressed: () => _settingsState.setState(() {
        setValue(null);
      })
    )
  );

  VoidSetting({
    required String name,
    Icon? icon,
    required this.action
  }) : super(name, null, icon);
}

class StringSetting extends Setting<String> {
  @override
  ListTile settingRow(BuildContext context) => ListTile(
    leading: icon,
    title: TextField(
      controller: TextEditingController(text: _value),
      decoration: InputDecoration(
        border: OutlineInputBorder(),
        labelText: name,
      ),
      onChanged: (t) {
        setValue(t);
      },
    ),
    //trailing: const Icon(Icons.close),
  );

  StringSetting({
    required String name,
    required String value,
    Icon? icon,
  }) : super(name, value, icon);

  @override
  Future<String> getValue() async {
    if (_loadedValue) return _value;
    _value = (await getStringSetting(name)) ?? _value;
    _loadedValue = true;
    return _value;
  }

  @override
  Future<void> setValue(String t) async {
    _value = t;
    await setStringSetting(name, t);
  }
}

class EnumSetting<T> extends Setting<T> {
  final String enumName;
  final int _enLength;
  final List<T> values;

  @override
  ListTile settingRow(BuildContext context) => ListTile(
    leading: icon,
    title: Row(
      children: [
        Text(name),
        Text(" - ", style: TextStyle(color: maristenBlueLight)),
        Text(_value.toString().substring(_enLength)) //, style: TextStyle(fontWeight: FontWeight.bold)
      ]
    ),//Text("$name: ${_value.toString().substring(_enLength)}"),
    trailing: Icon(Icons.navigate_next),
    onTap: () {
      Navigator.push(context, MaterialPageRoute(builder: (context) => _EnumSettingScreen(this, name, enumName, _enLength, values, _value)));
    },
  );

  EnumSetting({
    required String name,
    required this.enumName,
    required this.values,
    required T value,
    Icon? icon
  }) : _enLength = enumName.length + 1, super(name, value, icon);

  @override
  Future<T> getValue() async {
    if (_loadedValue) return _value;
    var s = await getStringSetting(name);
    if (s != null) {
      for (var v in values) {
        if (v.toString() == s) {
          _value = v;
          break;
        }
      }
    }
    _loadedValue = true;
    return _value;
  }

  @override
  Future<void> setValue(T t) async {
    _value = t;
    await setStringSetting(name, t.toString());
  }
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
              _settingsState.setState(() {
              setState(() {
                value = values[index];
                setting.setValue(setting.values[index]);
              });

              });
              //Navigator.pop(context);
            },
          );
        })
    );
  }

  _EnumSettingScreenState(this.setting, this.title, this.values, this.value);
}

Widget settingsWidgets(BuildContext context) {
  final items = <Widget>[];
  int gi = 0;
  for (var group in settings) {
    if (gi > 0) items.add(const Divider(thickness: 4.0, height: 30.0));
    items.add(ListTile(
      //dense: true,
      //minVerticalPadding: 0,
      //contentPadding: EdgeInsets.fromLTRB(16, -4, 16, -12),
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
        items.add(const Divider(thickness: 0.0));
        items.add(setting.settingRow(context));
      }
    }
    ++gi;
  }
  return ListView.builder(
      itemCount: items.length,// * 2 - 1,
      itemBuilder: (context, i) {
        //if (i.isOdd) return const Divider();
        //final index = i ~/ 2;
        return items[i];
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