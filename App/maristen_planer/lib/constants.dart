import 'package:flutter/material.dart';


final lightTheme = ThemeData(
  primarySwatch: maristenColorMaterial,
  backgroundColor: const Color(0xEEEEEE),
  brightness: Brightness.light,
);

final darkTheme = ThemeData(
    primarySwatch: maristenColorMaterial,
    backgroundColor: const Color(0x222222),
    brightness: Brightness.dark,
);

//final x = ThemeData.dark()

const int _maristenPrimaryColor = 0xFF0D68AA;//const Color.fromARGB(255, 13, 104, 170);
const int _maristenPrimaryColorLight = 0xFF118BE6;

const maristenBlue = Color(_maristenPrimaryColor);
const maristenBlueLight = Color(_maristenPrimaryColorLight);

const MaterialColor maristenColorMaterial = MaterialColor(
  _maristenPrimaryColorLight,
  <int, Color>{
    50: Color.fromARGB(255, 207, 233, 252),
    100: Color.fromARGB(255, 129, 196, 245),
    200: Color.fromARGB(255, 119, 191, 244),
    300: Color.fromARGB(255, 69, 168, 241),
    400: Color.fromARGB(255, 43, 154, 236),
    500: Color(_maristenPrimaryColorLight),
    600: Color(_maristenPrimaryColor),
    700: Color.fromARGB(255, 12, 91, 150),
    800: Color.fromARGB(255, 7, 86, 143),
    900: Color.fromARGB(255, 4, 80, 136),
  },
);