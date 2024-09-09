package com.truelogic.test.assessment;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import com.google.gson.*;

public class Solution {
    public static void main(String[] args) throws IOException {

        // Read genre from user input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the genre: ");
        String genre = scanner.nextLine();

        String result = bestInGenre(genre);

        if (result != null && !result.isEmpty()) {
            System.out.println("The best TV series in the " + genre + " genre is: " + result);
        } else {
            System.out.println("No TV series found for the genre: " + genre);
        }
    }

    public static String bestInGenre(String genre) {
        int page = 1;
        boolean hasMorePages = true;
        String bestSeries = "";
        double highRating = -1.0;
        String baseURL = "https://jsonmock.hackerrank.com/api/tvseries?genre=" + genre + "&page=";
        Gson gson = new Gson();

        while (hasMorePages) {
            try {
                String uri = baseURL + page;
                URL url = new URL(uri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStreamReader is = new InputStreamReader(connection.getInputStream());
                    BufferedReader in = new BufferedReader(is);
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JsonObject object = gson.fromJson(response.toString(), JsonObject.class);
                    JsonArray data = object.get("data").getAsJsonArray();

                    for (var element : data) {
                        JsonObject series = element.getAsJsonObject();
                        double rating = series.get("imdb_rating").getAsDouble();
                        String name = series.get("name").getAsString();

                        if (rating > highRating || (rating == highRating && name.compareToIgnoreCase(bestSeries) < 0)) {
                            highRating = rating;
                            bestSeries = name;
                        }
                    }

                    int totalPages = object.get("total_pages").getAsInt();
                    page++;
                    if (page > totalPages) {
                        hasMorePages = false;
                    }
                } else {
                    throw new RuntimeException("Request failed with error: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return bestSeries;
    }
}
