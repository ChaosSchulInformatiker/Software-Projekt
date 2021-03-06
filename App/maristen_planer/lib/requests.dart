import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:maristen_planer/utils.dart';

import 'main.dart';

const apiRoot = 'https://www.loens2.com/maristenplaner';//'http://localhost:8000';'https://www.loens2.com/maristenplaner';

Future<Json> fetchSchedule(int dayIndex, String clazz, String subjects) async {
  print('Fetch schedule');
  print(dayIndex);
  final subjectsCSV = subjects;
  return request('/schedule?day=$dayIndex&class=$clazz&subjects=$subjectsCSV');
} // /schedule?day=$day&class=$clazz&subjects=$subjectsCSV

Future<Json> registerRequest(String fName, String lName, String eMail) async => request('/register?first_name=$fName&last_name=$lName&e_mail=$eMail');

Future<Json> loginRequest(String email, String code) async => request('/login?e_mail=$email&code=$code');

Future<Json> request(String subAddress) async {
  print(subAddress);
  final response = await http.get(Uri.parse('$apiRoot$subAddress'), headers: {
    'Authorization': '$id'
  });
  print(response.body);
  print(response.statusCode);
  if (response.statusCode == 200) {
    return jsonDecode(response.body);
  } else {
    throw Exception('Failed to load schedule');
  }
}