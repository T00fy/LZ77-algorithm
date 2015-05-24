import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

public class LZ77FileInputProgram{
 private String input = "";
 private int windowSize;
 private int parsingPosition;
 private LZ77Step firstStep;
 private LZ77Step currentStep;

 public void start(){
  //while (true){
   System.out.print("Enter name of file to parse: ");
   String str1 = Keyboard.readInput();
   System.out.println();
   try {
    FileInputStream localFileInputStream = new FileInputStream(str1);
    byte[] arrayOfByte = new byte[(int)new File(str1).length()];
    localFileInputStream.read(arrayOfByte);
    this.input = new String(arrayOfByte);
    localFileInputStream.close();
   }
   catch (IOException localIOException){
      System.out.println("Unable to open input file " + str1 + "!");
   }
  //}
  System.out.println("Input size: " + this.input.length());
  System.out.print("Enter window size [default = input length]: ");
  String innovation = Keyboard.readInput();
  if (innovation.length() == 0){
   this.windowSize = this.input.length();
     }
  else{
   this.windowSize = Integer.parseInt(innovation);
  }
  System.out.println();
  System.out.println("Window size: " + this.windowSize);
  lz77();
 }
 
 public void lz77() {
  long numberOfElementaryOp = 0L;
  int bitsNeeded = 0;
  int bitsToEncode = 0;
  this.firstStep = new LZ77Step(0, 0, 0, this.input.substring(0, 1), 1); 
  this.parsingPosition = 1;
  numberOfElementaryOp += 1L;
  this.currentStep = this.firstStep;
  String str1 = "(" + this.input.charAt(0) + ")";
  numberOfElementaryOp += 1L;
  
  while (this.parsingPosition < this.input.length()) {
   System.out.println("Current step: " + this.currentStep.stepNumber + ", parsing position: " + this.parsingPosition);
   int length = 0;
   numberOfElementaryOp += 1L;
   int startPosition = this.parsingPosition - this.windowSize;
   numberOfElementaryOp += 1L;
   numberOfElementaryOp += 1L;
   numberOfElementaryOp += 1L;
   
   if (startPosition < 0) {
    numberOfElementaryOp += 1L;
    startPosition = 0;
   }

   int n = this.parsingPosition - this.windowSize;
   numberOfElementaryOp += 1L;
   numberOfElementaryOp += 1L;
   numberOfElementaryOp += 1L;
   if (n < 0) {
    numberOfElementaryOp += 1L;
    n = 0;
   }
   
   numberOfElementaryOp += 4L;
   
   while ((n < this.parsingPosition) && (this.parsingPosition + length < this.input.length())){
    int i1 = 0;
    numberOfElementaryOp += 1L;
    numberOfElementaryOp += 8L;
    if (this.windowSize < this.input.length()) {
      numberOfElementaryOp += 2L;
    }
  
    while ((this.parsingPosition + i1 < this.input.length()) && (i1 < this.windowSize) && (this.input.charAt(n + i1) == this.input.charAt(this.parsingPosition + i1))){
     i1++;
     numberOfElementaryOp += 1L;
     numberOfElementaryOp += 8L;
    }
  
    numberOfElementaryOp += 1L;
    if (i1 > length){
     length = i1;
     numberOfElementaryOp += 1L;
     startPosition = n;
     numberOfElementaryOp += 1L;
    } 
    n++;
    numberOfElementaryOp += 1L;
    numberOfElementaryOp += 4L;
   }

   str1 = str1 + this.input.substring(this.parsingPosition, this.parsingPosition + length);
   String innovation;
   if (this.parsingPosition + length <= this.input.length() - 1) {
    innovation = this.input.substring(this.parsingPosition + length, this.parsingPosition + length + 1);
   }
   else{
    innovation = "no innovation - end of input!";
   }
   numberOfElementaryOp += 1L;

   str1 = str1 + "(" + innovation + ")";
   
   bitsToEncode = 1 + (int)Math.ceil(Math.log(Math.min(this.parsingPosition, this.windowSize)) / Math.log(2.0D)) + (int)Math.ceil(Math.log(Math.min(this.input.length() - this.parsingPosition, this.windowSize)) / Math.log(2.0D));

   this.currentStep.nextStep = new LZ77Step(this.currentStep.stepNumber + 1, startPosition, length, innovation, bitsToEncode);

   this.parsingPosition += length + 1;
   numberOfElementaryOp += 1L;
   numberOfElementaryOp += 1L;
   this.currentStep = this.currentStep.nextStep;
   bitsNeeded += this.currentStep.bitsToEncode;
   numberOfElementaryOp += 1L;
  }

  System.out.println();
  System.out.println("SUMMARY");
  System.out.println();
  System.out.println("Input: " + this.input);
  System.out.println();
  int length = 0;
  this.currentStep = this.firstStep;
  while (true){
   length++;
   if (this.currentStep.nextStep == null) break;
   this.currentStep = this.currentStep.nextStep;
  }
  bitsNeeded++;
  System.out.println("Parsed string:");
  System.out.println();
  System.out.println(str1);
     System.out.println();
  System.out.println("- window size: " + this.windowSize);
  System.out.println("- number of steps: " + length);
  System.out.println("- Elementary operations required: " + numberOfElementaryOp);
  System.out.println("- Total number of bits needed to encode input of " + this.input.length() + " bits: " + bitsNeeded);
  System.out.println("- Possible compression ratio: " + String.format("%.2f", new Object[] { Double.valueOf(bitsNeeded / (1.0D * this.input.length())) }) + " bits/bit");
   }
 }


