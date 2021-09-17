import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:maristen_planer/auth/authentification.dart';
import 'package:maristen_planer/constants.dart';
import 'package:maristen_planer/requests.dart';
import 'dart:async';
import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:path_provider/path_provider.dart';
import 'package:flutter_pdfview/flutter_pdfview.dart';

class LoginScreen extends StatefulWidget {
  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState  extends State<LoginScreen> {
  final TextEditingController fNameController = TextEditingController();
  final TextEditingController lNameController = TextEditingController();
  final TextEditingController eMailController = TextEditingController();
  bool editedEmail = false;

  String pathPDF = "";

  @override

  void initState() {
    super.initState();
    fromAsset('assets/DSGVO.pdf', 'DSGVO.pdf').then((f) {
      setState(() {
        pathPDF = f.path;
      });
    });
  }



  Future<File> fromAsset(String asset, String filename) async {
    // To open from assets, you can copy them to the app storage folder, and the access them "locally"
    Completer<File> completer = Completer();

    try {
      var dir = await getApplicationDocumentsDirectory();
      File file = File("${dir.path}/$filename");
      var data = await rootBundle.load(asset);
      var bytes = data.buffer.asUint8List();
      await file.writeAsBytes(bytes, flush: true);
      completer.complete(file);
    } catch (e) {
      throw Exception('Error parsing asset file!');
    }

    return completer.future;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('MaristenPlaner'),
        backgroundColor: maristenBlue,
      ),
      body: Padding(
        padding: EdgeInsets.all(10),
        child: ListView(
          children: <Widget>[
            Container(
              alignment: Alignment.center,
              padding: EdgeInsets.all(10),
              child: Text(
                'MaristenPlaner Login',
                style: TextStyle(
                  color: maristenBlueLight,
                  fontWeight: FontWeight.w500,
                  fontSize: 30
                ),
              ),
            ),
            Container(
              padding: EdgeInsets.all(10),
              child: TextField(
                controller: fNameController,
                decoration: InputDecoration(
                  border: OutlineInputBorder(),
                  labelText: 'Vorname',
                ),
                onChanged: (s) {
                  if (!editedEmail) {
                    eMailController.text = getEmailAccount(
                        s, lNameController.text);
                  } else if (eMailController.text == getEmailAccount(
                      s, lNameController.text)) {
                    editedEmail = false;
                  }
                },
              ),
            ),
            Container(
              padding: EdgeInsets.all(10),
              child: TextField(
                controller: lNameController,
                decoration: InputDecoration(
                  border: OutlineInputBorder(),
                  labelText: 'Nachname',
                ),
                onChanged: (s) {
                  if (!editedEmail) {
                    eMailController.text = getEmailAccount(
                        fNameController.text, s);
                  } else if (eMailController.text == getEmailAccount(
                      fNameController.text, s)) {
                    editedEmail = false;
                  }
                },
              ),
            ),
            Container(
              padding: EdgeInsets.all(10),
              child: TextField(
                controller: eMailController,
                decoration: InputDecoration(
                  border: OutlineInputBorder(),
                  labelText: 'Schul E-Mail',
                ),
                onChanged: (s) {
                  editedEmail = true;
                },
              ),
            ),
            Container(
              height: 50,
              padding: EdgeInsets.fromLTRB(10, 10, 10, 0),
              child: ElevatedButton(
                style: ButtonStyle(backgroundColor: MaterialStateProperty.all<Color>(maristenBlueLight)),
                child: Text('Login'),
                onPressed: () {
                  print(fNameController.text);
                  //getEmailAccount(fNameController.text, lNameController.text);
                  //print(eMail);
                  Navigator.of(context).push(MaterialPageRoute(
                      builder: (BuildContext context) => AuthentificationScreen(registerRequest(fNameController.text, lNameController.text, eMailController.text))
                  ));
                },
              )
            ),
            Container(
              padding: EdgeInsets.all(30),
                child: Align(
                    alignment: Alignment.center,
                    child: RichText(
                        text: new TextSpan(
                            children: [
                              new TextSpan(
                                text: 'Durch den Login akzeptieren sie unsere ',
                                style: new TextStyle(color: Colors.grey),

                              ),
                              new TextSpan(
                                  text: 'Nutzungs- & Datenschutzbedingungen.',
                                  style: new TextStyle(
                                      color: Colors.blue,
                                      decoration: TextDecoration.underline,
                                      decorationColor: Colors.blue,
                                  ),
                                  recognizer: new TapGestureRecognizer()
                                    ..onTap = () {
                                      if (pathPDF.isNotEmpty) {
                                        Navigator.push(
                                            context,
                                            MaterialPageRoute(
                                              builder: (context) => PDFScreen(path: pathPDF,),
                                            )
                                        );
                                      }
                                    }
                              ),
                            ]
                        )
                    )
                )
            )
          ],
        ),
      ),
    );
  }
}

String getEmailAccount(String fName, String lName) {
  final buffer = StringBuffer();
  for (var c in fName.runes)
    appendTransformedChar(buffer, c);
  buffer.write('.');
  for (var c in lName.runes)
    appendTransformedChar(buffer, c);
  buffer.write('@maristenkolleg.de');
  return buffer.toString();
}

void appendTransformedChar(StringBuffer b, int c) {
  if (c >= 0x41 && c <= 0x5a) b.write(String.fromCharCode(c + 0x20));
  else if (c >= 0x61 && c <= 0x7a) b.write(String.fromCharCode(c));
  else if (c == 0xe4 || c == 0xc4) b.write('ae');
  else if (c == 0xf6 || c == 0xd6) b.write('oe');
  else if (c == 0xfc || c == 0xdc) b.write('ue');
  else if (c == 0xdf) b.write('ss');
}

class PDFScreen extends StatefulWidget {
  final String? path;
  PDFScreen({Key? key, this.path}) : super(key: key);
  _PDFScreenState createState() => _PDFScreenState();
}

class _PDFScreenState extends State<PDFScreen> with WidgetsBindingObserver {
  final Completer<PDFViewController> _controller =
  Completer<PDFViewController>();
  int? pages = 0;
  int? currentPage = 0;
  bool isReady = false;
  String errorMessage = '';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Document"),
        actions: <Widget>[
          IconButton(
            icon: Icon(Icons.share),
            onPressed: () {},
          )
        ],
      ),
      body: Stack(
        children: <Widget>[
          PDFView(
            filePath: widget.path,
            enableSwipe: true,
            swipeHorizontal: true,
            autoSpacing: false,
            pageFling: true,
            pageSnap: true,
            defaultPage: currentPage!,
            fitPolicy: FitPolicy.BOTH,
            preventLinkNavigation: false,
            onRender: (_pages) {
              setState(() {
                pages = _pages;
                isReady = true;
              });
            },
            onError: (error) {
              setState(() {
                errorMessage = error.toString();
              });
              print(error.toString());
            },
            onPageError: (page, error) {
              setState(() {
                errorMessage = '$page: ${error.toString()}';
              });
              print('$page: ${error.toString()}');
            },
            onViewCreated: (PDFViewController pdfViewController) {
              _controller.complete(pdfViewController);
            },
            onLinkHandler: (String? uri) {
              print('goto uri: $uri');
            },
            onPageChanged: (int? page, int? total) {
              print('page change: $page/$total');
              setState(() {
                currentPage = page;
              });
            },
          ),
          errorMessage.isEmpty
              ? !isReady
              ? Center(
            child: CircularProgressIndicator(),
          )
              : Container()
              : Center(
            child: Text(errorMessage),
          )
        ],
      ),
      floatingActionButton: FutureBuilder<PDFViewController>(
        future: _controller.future,
        builder: (context, AsyncSnapshot<PDFViewController> snapshot) {
          if (snapshot.hasData) {
            return FloatingActionButton.extended(
              label: Text("Go to ${pages! ~/ 2}"),
              onPressed: () async {
                await snapshot.data!.setPage(pages! ~/ 2);
              },
            );
          }

          return Container();
        },
      ),
    );
  }
}
