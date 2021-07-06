import 'package:shared_preferences/shared_preferences.dart';

Future<void> saveAccountId(int id) async {
  final prefs = await SharedPreferences.getInstance();
  await prefs.setInt('account_id', id);
}

Future<int> getAccountId() async {
  final prefs = await SharedPreferences.getInstance();
  return prefs.getInt('account_id') ?? -1;
}