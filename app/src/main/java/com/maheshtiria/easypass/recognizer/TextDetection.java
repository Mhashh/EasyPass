package com.maheshtiria.easypass.recognizer;

import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class TextDetection {
  public static TextRecognizer detector= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

}
