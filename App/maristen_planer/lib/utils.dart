import 'package:flutter/material.dart';
import 'package:maristen_planer/settings.dart';

int todayIndex() => DateTime.now().weekday;


int scheduleTodayIndex() {
  int ti = todayIndex();
  if (ti >= DateTime.saturday) return 0;
  return ti - 1;
}

String dayOfScheduleToday() => dayOfSchedule(todayIndex());
String dayOfSchedule(int index) {
  ++index;
  switch (index) {
    case DateTime.monday:
    case DateTime.saturday:
    case DateTime.sunday:
      return 'Montag';
    case DateTime.tuesday: return 'Dienstag';
    case DateTime.wednesday: return 'Mittwoch';
    case DateTime.thursday: return 'Donnerstag';
    case DateTime.friday: return 'Freitag';
  }
  return 'Keintag';
}

typedef Json = Map<String, dynamic>;

ThemeMode themeDataOfMode(ThemeSelection mode) {
  switch (mode) {
    case ThemeSelection.System: return ThemeMode.system;
    case ThemeSelection.Light: return ThemeMode.light;
    case ThemeSelection.Dark: return ThemeMode.dark;
  }
}