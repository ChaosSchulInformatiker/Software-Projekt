import 'package:flutter/material.dart';
import 'package:maristen_planer/constants.dart';

import '../settings.dart';

class SideDrawer extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);
    final TextStyle textStyle = theme.textTheme.bodyText2!;
    final List<Widget> aboutBoxChildren = <Widget>[
      const SizedBox(height: 24),
      RichText(
          text: TextSpan(children: <TextSpan>[
            TextSpan(
                style: textStyle,
                text: "Die MaristenPlaner App ist eine App für Schüler & Lehrer"
                    ' mit dem Sie schnell und einfach Stundenpläne, Vertretungspläne'
                    ' und den Mensa Plan aufrufen können')
          ]))
    ];
    return Drawer(
      child: Column(
        children: <Widget>[
          DrawerHeader(
            child: Center(
                child: Text(
                  'MaristenPlaner Menü',
                  textAlign: TextAlign.center,
                  style: TextStyle(color: Colors.white, fontSize: 25),
                )),
            decoration: BoxDecoration(
              color: maristenBlue,
            ),
          ),
          ListTile(
            leading: Icon(Icons.settings),
            title: Text('Einstellungen'),
            onTap: () {
              Navigator.push(context, MaterialPageRoute(builder: (context) => SettingsScreen()));
              },
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