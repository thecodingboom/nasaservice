package api;

import client.RestClientBuilder;
import client.RestPartnerServiceConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Response;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NASAApiClient {
    private final RoverService roverService;
    private static final String API_KEY = "O7RQLWc5v5MXGh3136YjSArlfffoe9GevWJ2MSaZ";

    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {{
        put("^\\w{4,}\\s\\d{1,2},\\s\\d{4}$", "MMMM dd, yyyy");
        put("^\\w{3}-\\d{1,2}-\\d{4}$", "MMM-dd-yyyy");
        put("^\\d{8}$", "yyyyMMdd");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
        put("^\\d{1,2}/\\d{1,2}/\\d{1,2}$", "MM/dd/yy");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
        put("^\\d{12}$", "yyyyMMddHHmm");
        put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
        put("^\\d{14}$", "yyyyMMddHHmmss");
        put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
    }};

    public NASAApiClient(){
        RestPartnerServiceConfig restPartnerServiceConfig =
                new RestPartnerServiceConfig("https://api.nasa.gov/", 30, 30);
        this.roverService = RestClientBuilder.createClient(restPartnerServiceConfig, RoverService.class);
    }

    public void requestPhotos(File textFile){
        try {
            List<String> allLines = Files.readAllLines(Paths.get(textFile.getAbsolutePath()));
            for (String line : allLines) {
                DateFormat fmt = new SimpleDateFormat(determineDateFormat(line));
                fmt.setLenient(false);

                Date d = fmt.parse(line);
                DateFormat destDf = new SimpleDateFormat("yyyy-MM-dd");
                String convertedDate = destDf.format(d);

                Response<Photos> response = roverService.create(convertedDate, API_KEY).execute();
                Photos roverPhotoResponses = response.body();

                boolean skipShowInBrowser = false;
                if (response.isSuccessful() && roverPhotoResponses != null && !CollectionUtils.isEmpty(roverPhotoResponses.getRoverPhotos())) {
                    System.out.println("DATE: " + convertedDate);
                    for (RoverPhotoResponse roverPhotoResponse : roverPhotoResponses.getRoverPhotos()) {
                        System.out.println("URL: " + roverPhotoResponse.getImgUrl());
                        if (!StringUtils.isEmpty(roverPhotoResponse.getImgUrl())) {
                            if (!skipShowInBrowser) {
                                int dialogButton = JOptionPane.YES_NO_OPTION;
                                int dialogResult = JOptionPane.showConfirmDialog(
                                        null,
                                        "Would you like to open " + roverPhotoResponse.getImgUrl() + " with your browser?",
                                        "Open URL in browser",
                                        dialogButton);
                                if (dialogResult == 0) {
                                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                        try {
                                            Desktop.getDesktop().browse(new URI(roverPhotoResponse.getImgUrl()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    dialogButton = JOptionPane.YES_NO_OPTION;
                                    dialogResult = JOptionPane.showConfirmDialog(
                                            null,
                                            "Would you like to skip showing all images on browser?",
                                            "Skip opening URL in browser",
                                            dialogButton);
                                    if (dialogResult == 0) {
                                        skipShowInBrowser = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public String determineDateFormat(String dateString) {
        for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                return DATE_FORMAT_REGEXPS.get(regexp);
            }
        }
        return null; // Unknown format.
    }
}
