// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility that Flutter provides. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'dart:convert';
import 'package:flutter_test/flutter_test.dart';
import 'package:http/http.dart' as http;

void main() {
  test('HTTP request', () async {
    final uri = Uri.parse('https://api.wynncraft.com/v2/player/visar77/stats');

    //final client = HttpClient()
    //    ..badCertificateCallback = (_, __, ___) { return true; };
    //final request = await client.getUrl(uri);
    //final response = await request.done;
    //print(response);

    //final json = jsonDecode(response);

    print(jsonDecode((await http.get(uri)).body));
  });
}
