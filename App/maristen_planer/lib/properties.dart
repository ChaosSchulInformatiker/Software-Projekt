import 'package:shared_preferences/shared_preferences.dart';

import 'main.dart';

Future<void> saveAccountId(int id_) async {
  id = id_;
  final prefs = await SharedPreferences.getInstance();
  await prefs.setInt('account_id', id_);
}

Future<int> getAccountId() async {
  final prefs = await SharedPreferences.getInstance();
  return prefs.getInt('account_id') ?? -1;
}

Future<void> setBoolSetting(String id, bool value) async {
  final prefs = await SharedPreferences.getInstance();
  await prefs.setBool('settings.$id', value);
}

Future<bool?> getBoolSetting(String id) async {
  final prefs = await SharedPreferences.getInstance();
  return prefs.getBool('settings.$id');
}

Future<void> setStringSetting(String id, String value) async {
  final prefs = await SharedPreferences.getInstance();
  await prefs.setString('settings.$id', value);
}

Future<String?> getStringSetting(String id) async {
  final prefs = await SharedPreferences.getInstance();
  return prefs.getString('settings.$id');
}

Future<void> resetData() async {
  final prefs = await SharedPreferences.getInstance();
  await prefs.clear();
}