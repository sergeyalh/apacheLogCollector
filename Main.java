package com.serg.main;

import java.io.File;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Main {

  private static final String COUNTRY = "Country";
  private static final String OS = "OS";
  private static final String BROWSER = "Browser";
  private static final String LOGREGEX = "^([\\d.]+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+) \"([^\"]+)\" \"(.+?)\"";

  public static void main(String[] args) throws Exception {
    int totalCount = 0;

    Map<String, Integer> countryCount = new HashMap<>();
    Map<String, Integer> osCount = new HashMap<>();
    Map<String, Integer> browserCount = new HashMap<>();

    File file = new File("tab/src/main/java/com/serg/main/apache.log");

    Pattern logPattern = Pattern.compile(LOGREGEX);

    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        Matcher matcher = logPattern.matcher(line);

        if (matcher.find()) {
          String ip = matcher.group(1);
          String userAgent = matcher.group(9);
          LogsObj curLog = new LogsObj(ip, userAgent);

          ipAnalizer(countryCount, curLog);
          userAgentAnalizer(osCount, browserCount, curLog);

          totalCount++;
          System.out.println("----" + totalCount + "LogsProcessed-----------");
        }
      }
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }

    beautifySortifyAndPrintMap(totalCount, countryCount, COUNTRY);
    beautifySortifyAndPrintMap(totalCount, osCount, OS);
    beautifySortifyAndPrintMap(totalCount, browserCount, BROWSER);

  }

  private static void beautifySortifyAndPrintMap(int totalCount, Map<String, Integer> mapCount, String sTitle) {
    SortedMap<String, Double> MapPercentage = new TreeMap<>();
    for (Map.Entry<String, Integer> entry : mapCount.entrySet()) {
      double percentage = (double) entry.getValue() / totalCount * 100;
      String currKey = entry.getKey();
      if (currKey == null) {
        currKey = "ValueNotFound";
      }
      MapPercentage.put(currKey, percentage);
    }

    creatOutPutFile(sTitle, MapPercentage);
  }

  private static void creatOutPutFile(String sTitle, SortedMap<String, Double> MapPercentage) {
    Map<String, Double> sortedMap = MapPercentage.entrySet().stream()
        .sorted(Entry.comparingByValue(Comparator.reverseOrder()))// .limit(5)
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

    try {
      PrintWriter writer = new PrintWriter(sTitle + "Distribution.txt", "UTF-8");
      for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
        String sRow = String.format("%s: %.2f%%\n", entry.getKey(), entry.getValue());
        // String sRow = String.format("%s: %.3f%%\n", entry.getKey(),
        // entry.getValue());
        writer.println(sRow);
      }
      writer.close();
    } catch (Exception e) {
      System.out.println("IO issues");
    }
  }

  private static void userAgentAnalizer(Map<String, Integer> osCount, Map<String, Integer> browserCount,
      LogsObj curLog) {
    String browserName = curLog.getBrowser();
    browserCount.put(browserName, browserCount.getOrDefault(browserName, 0) + 1);
    String osName = curLog.getOS();
    osCount.put(osName, osCount.getOrDefault(osName, 0) + 1);
  }

  private static void ipAnalizer(Map<String, Integer> countryCount, LogsObj curLog) {
    String countryName = curLog.getCountry();
    countryCount.put(countryName, countryCount.getOrDefault(countryName, 0) + 1);
  }
}