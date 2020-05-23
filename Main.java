package readability;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        String file = args[0];
        testCase(file);

    }

    public static void testCase(String filePath){

        String text = inputToString(filePath);
        String[] sentences = text.split("(?<=[.?!])\\s+(?=[a-zA-Z0-9])");
        double numSentences = sentences.length;

        String[] words = text.split("\\s+");
        double numWords = words.length;

        String chs = text.replaceAll("[\\n\\t ]", "");
        double numChars = chs.length();

        int numPolysyllable = getNumPolysyllable(words);

        int numSyllable = getNumSyllables(words);

        double L = numChars / numWords * 100;
        double S = numSentences / numWords * 100;

        double scoreARI = 4.71 * (numChars/numWords) + 0.5 * (numWords/numSentences) - 21.43;
        double scoreFK = 0.39 * (numWords/numSentences) + 11.8 * (numSyllable/numWords) - 15.59;
        double scoreSMOG = 1.043 * Math.sqrt(numPolysyllable * (30/numSentences)) + 3.1291;
        double scoreCL = 0.0588 * L - 0.296 * S - 15.8;

        int ageARI = getAge((int) Math.ceil(scoreARI));
        int ageFK = getAge((int) Math.ceil(scoreFK));
        int ageSMOG = getAge((int) Math.ceil(scoreSMOG));
        int ageCL = getAge((int) Math.ceil(scoreCL));

        double avg = (ageARI + ageCL + ageFK + ageSMOG)/4.0;


        //outputs
        System.out.println("Java Main " + filePath + "\nThe text is:" );
        System.out.println(text +"\n");
        System.out.println("Words: " + (int)numWords + "\nSentences: " +(int) numSentences + "\nCharacters: " + (int) numChars + "\nSyllables: " + numSyllable +
                "\nPolysyllables: " + numPolysyllable);
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        //to choose which to output
        Scanner sc = new Scanner(System.in);
        String scoreChoice = sc.next();
        System.out.println();

        if(scoreChoice.equals("ARI") || scoreChoice.equals("all"))
            System.out.println("Automated Readability Index: " + scoreARI + " (about " + ageARI + " year olds).");
        if(scoreChoice.equals("FK") || scoreChoice.equals("all"))
            System.out.println("Flesch–Kincaid readability tests: " + scoreFK + " (about " + ageFK + " year olds).");
        if(scoreChoice.equals("SMOG") || scoreChoice.equals("all"))
            System.out.println("Simple Measure of Gobbledygook: " + scoreSMOG + " (about " + ageSMOG + " year olds).");
        if(scoreChoice.equals("CL") || scoreChoice.equals("all"))
            System.out.println("Coleman–Liau index: " + scoreCL + " (about " + ageCL + " year olds).");
        System.out.println();
        System.out.println("This text should be understood in average by " + avg + " year olds.");


    }



    public static int getAge(int score){
        int ageSpan;
        switch(score) {
            case 1:
                ageSpan = 6;
                break;
            case 2:
                ageSpan = 7;
                break;
            case 3:
                ageSpan = 9;
                break;
            case 4:
                ageSpan = 10;
                break;
            case 5:
                ageSpan = 11;
                break;
            case 6:
                ageSpan = 12;
                break;
            case 7:
                ageSpan = 13;
                break;
            case 8:
                ageSpan = 14;
                break;
            case 9:
                ageSpan = 15;
                break;
            case 10:
                ageSpan = 16;
                break;
            case 11:
                ageSpan = 17;
                break;
            case 12:
                ageSpan = 18;
                break;
            case 13:
                ageSpan = 24;
                break;
            case 14:
                ageSpan = 24;
                break;
            default:
                ageSpan = 24;
        }
        return ageSpan;
    }



    private static String inputToString(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    public static int getNumPolysyllable(String[] words){
        int result = 0;
        for(String s : words){
            if(countSyllables(s) > 2)
                result++;
        }
        return result;

    }

    public static int getNumSyllables(String[] words){
        int result = 0;
        for(String s : words){
            result += countSyllables(s);
        }
        return result;
    }
/*
    public static int countSyllables(String word) {
        String input = word.toLowerCase();
        int i = input.length() - 1;
        int syllables = 0;
        // skip all the e's in the end
        while (i >= 0 && input.charAt(i) == 'e') {
            i--;
            syllables = 1;
        }

        boolean preVowel = false;
        while (i >= 0) {
            if (isVowel(input.charAt(i))) {
                if (!preVowel) {
                    syllables++;
                    preVowel = true;
                }
            } else {
                preVowel = false;
            }
            i--;
        }
        return syllables;
    }
*/

    public static int countSyllables(String word){
        String input = word.toLowerCase();
        int groups = 0;
        boolean isGroup = false;


        //count groups of vowels
        for (int i = 0; i < word.length(); i++) {

            //each group is only counted once
            if(isVowel(word.charAt(i)) && !isGroup){
                groups++;
                isGroup = true;
            } else if(!isVowel(word.charAt(i))){
                isGroup = false;
            }
        }
        char c = 'c';
        if(word.length() >= 2) {
            c = word.charAt(word.length() - 2);
        }

        if(groups > 1 && !isVowel(c) && word.charAt(word.length() - 1) == 'e')
            groups--;

        return groups;
    }

    public static boolean isVowel(char ch) {
        if (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u' || ch == 'y') {
            return true;
        }
        return false;
    }
}
