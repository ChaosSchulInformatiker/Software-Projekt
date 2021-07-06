import 'package:shared_preferences/shared_preferences.dart';

Future<void> saveAccountId(int id) async {
  final prefs = await SharedPreferences.getInstance();
  await prefs.setInt('account_id', id);
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

Future<void> clearCache() async {
  final prefs = await SharedPreferences.getInstance();
  await prefs.clear();
}