import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:maristen_planer/utils.dart';

const apiRoot = 'https://www.loens2.com/maristenplaner';

Future<Json> fetchSchedule() async {print('Fetch schedule');return request('/schedule/000000');}

Future<Json> registerRequest(String fName, String lName) async => request('/register?first_name=$fName&last_name=$lName');

Future<Json> loginRequest(String email, String code) async => request('/login?e_mail=$email&code=$code');

Future<Json> request(String subAddress) async {
  final response = await http.get(Uri.parse('$apiRoot$subAddress'));
  if (response.statusCode == 200) {
    return jsonDecode(response.body);
  } else {
    throw Exception('Failed to load schedule');
  }
}