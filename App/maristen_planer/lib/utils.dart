int todayIndex() => DateTime.now().weekday;

String dayOfSchedule() {
  switch (todayIndex()) {
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