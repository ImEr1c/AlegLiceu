package com.imer1c.alegliceu.manager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class DataManager {

    private static final File dataCache = new File("men_data_cache");

    private static final Gson GSON = new Gson();

    private static final String highSchoolsLink = "https://static.admitere.edu.ro/%s/repartizare/B/data/highschool.json";
    private static final String specializationLink = "https://static.admitere.edu.ro/%s/repartizare/B/data/specialization.json";
    private static final String candidatesLink = "https://static.admitere.edu.ro/%s/repartizare/B/data/candidate.json";
    private static final String evaluareCandidatesLink = "https://static.evaluare.edu.ro/%s/rezultate/B/data/candidate.json";

    private final Map<Long, Liceu> licee = new HashMap<>();

    private final List<String> specializari = new ArrayList<>();
    private final Map<String, String> colors = new HashMap<>();

    public void reloadCachedData()
    {
        int year = getNewestYear(highSchoolsLink);

        cacheLicee(year);
        populateLiceeWithProfiles(year);
        populateLastIndexes(year);
        licee.values().forEach(Liceu::computeValues);

        try (FileWriter writer = new FileWriter(dataCache))
        {
            GSON.toJson(new SaveData(licee, specializari, colors), writer);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean load()
    {
        if (!dataCache.exists())
        {
            return false;
        }

        try (FileReader reader = new FileReader(dataCache))
        {
            SaveData data = GSON.fromJson(reader, SaveData.class);

            this.licee.putAll(data.licee);
            this.specializari.addAll(data.specializari);
            this.colors.putAll(data.colors);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return true;
    }

    private void populateLastIndexes(int year)
    {
        try
        {
            URL candidatesUrl = new URL(String.format(candidatesLink, year));
            URL evaluareUrl = new URL(String.format(evaluareCandidatesLink, year));

            Map<Long, Map<String, String>> liceuToMapProfilToCode = new HashMap<>();
            Map<String, Double> evCodeToMedie = new HashMap<>();

            try (InputStream stream = candidatesUrl.openStream())
            {
                JsonElementList list = GSON.fromJson(new InputStreamReader(stream), JsonElementList.class);

                for (JsonObject object : list)
                {
                    String code = object.get("n").getAsString();
                    String liceu = object.get("h").getAsString();
                    String profil = object.get("sp").getAsString();
                    String madmText = object.get("madm").getAsString();
                    double medie = Double.parseDouble(madmText);

                    evCodeToMedie.put(code, medie);

                    long liceuId = -1;
                    String profilLiteral = null;

                    for (Liceu value : this.licee.values())
                    {
                        if (liceu.contains(value.getName()))
                        {
                            liceuId = value.getId();

                            for (Profil p : value.getProfile().values())
                            {
                                if (p.isProfile(profil))
                                {
                                    profilLiteral = p.getSpecializare();
                                }
                            }

                            break;
                        }
                    }

                    if (liceu.toLowerCase(Locale.ROOT).contains("miguel") && profil.toLowerCase().contains("filologie"))
                    {
                        System.out.println("FOUND: {");
                        System.out.println(profil.toLowerCase(Locale.ROOT).contains("spaniol"));
                        System.out.println("REAL " + profil);
                        System.out.println(licee.get(liceuId).getName());
                        System.out.println(liceuId);
                        System.out.println(profilLiteral);
                        System.out.println("}\n");
                    }

                    if (profilLiteral == null)
                    {
                        continue;
                    }

                    Map<String, String> profilToCode = liceuToMapProfilToCode.computeIfAbsent(liceuId, l -> new HashMap<>());

                    if (!profilToCode.containsKey(profilLiteral) || medie < evCodeToMedie.get(profilToCode.get(profilLiteral)))
                    {
                        profilToCode.put(profilLiteral, code);
                    }

                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }

            try (InputStream stream = evaluareUrl.openStream())
            {
                JsonElementList list = GSON.fromJson(new InputStreamReader(stream), JsonElementList.class);

                Map<String, Integer> codeToIndex = new HashMap<>();
                for (JsonObject object : list)
                {
                    String name = object.get("name").getAsString();
                    int index = object.get("index").getAsInt();

                    codeToIndex.put(name, index);
                }

                int i = 0;

                for (Liceu lic : licee.values())
                {
                    Map<String, String> profilToCode = liceuToMapProfilToCode.get(lic.getId());

                    if (profilToCode == null)
                    {
                        continue;
                    }

                    for (Profil profil : lic.getProfile().values())
                    {
                        String lastCodeOnProfile = profilToCode.get(profil.getSpecializare());

                        if (lastCodeOnProfile == null)
                        {
                            continue;
                        }

                        Integer index = codeToIndex.get(lastCodeOnProfile);

                        if (index == null)
                        {
                            continue;
                        }

                        profil.setIndex(index);
                    }
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }

        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    // Returns entire list of specializari
    private void populateLiceeWithProfiles(int year)
    {
        try
        {
            URL url = new URL(String.format(specializationLink, year));

            try (InputStream inputStream = url.openStream())
            {
                JsonElementList list = GSON.fromJson(new InputStreamReader(inputStream), JsonElementList.class);

                for (JsonObject profileData : list)
                {
                    int cod = profileData.get("c").getAsInt();
                    String liceuName = profileData.get("l").getAsString();
                    long liceuId = profileData.get("lc").getAsLong();
                    String limbaPredare = profileData.get("lp").getAsString();
                    String limbaBilingv = profileData.get("lb").getAsString();
                    int locuri = profileData.get("nlt").getAsInt();
                    JsonElement medieElement = profileData.get("um");
                    double medie = medieElement.isJsonNull() ? -1 : Double.parseDouble(medieElement.getAsString());
                    String specializare = profileData.get("sp").getAsString();

                    String specializareCustomId = computeCustomSpecializationName(specializare, limbaPredare, limbaBilingv);
                    Liceu liceu = licee.get(liceuId);

                    if (liceu.contains(specializareCustomId))
                    {
                        String name = specializareCustomId + " 1";
                        int i = 1;

                        while (liceu.contains(name))
                        {
                            name = specializareCustomId + " " + (i++);
                        }

                        specializareCustomId = name;
                    }

                    Profil profil = new Profil(liceuName, liceuId, specializareCustomId, specializare, limbaPredare, limbaBilingv, locuri, medie, cod);

                    liceu.addProfile(profil);

                    if (specializari.contains(specializareCustomId))
                    {
                        continue;
                    }

                    specializari.add(specializareCustomId);
                    addColor(specializareCustomId);
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        this.specializari.sort(String::compareToIgnoreCase);
    }

    private void cacheLicee(int year)
    {
        licee.clear();

        try
        {
            String s = String.format(highSchoolsLink, year);
            URL url = new URL(s);

            try (InputStream inputStream = url.openStream())
            {
                JsonElementList list = GSON.fromJson(new InputStreamReader(inputStream), JsonElementList.class);

                for (JsonObject liceuData : list)
                {
                    long id = liceuData.get("lc").getAsLong();
                    String name = liceuData.get("l").getAsString();
                    String address = liceuData.get("a").getAsString();
                    int profileCount = liceuData.get("sp").getAsInt();
                    String phone = liceuData.get("p").getAsString();

                    licee.put(id, new Liceu(name, address, id, profileCount, phone));
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String computeCustomSpecializationName(String sp, String matern, String bilingv)
    {
        StringBuilder builder = new StringBuilder(sp);

        if (matern != null && !matern.equals("-") && !matern.contains("română"))
        {
            builder.append(" Matern ").append(matern);
        }

        if (bilingv != null && !bilingv.equals("-"))
        {
            builder.append(" Bilingv ").append(bilingv);
        }

        return builder.toString();
    }

    private void addColor(String text)
    {
        String s = text.toLowerCase();
        String color;

        if (s.contains("matematic"))
        {
            color = "#1225a1";
        }
        else if (s.contains("filologie"))
        {
            color = "#5e1037";
        }
        else if (s.contains("naturii"))
        {
            color = "#6b6c75";
        }
        else if (s.contains("sociale"))
        {
            color = "#750829";
        }
        else
        {
            color = "#aba800";
        }

        this.colors.put(text, color);
    }

    // Gets the newest year value where data is available
    private int getNewestYear(String linkToTest)
    {
        int year = LocalDateTime.now().getYear();

        try
        {
            URL url = new URL(String.format(linkToTest, year));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            if (con.getResponseCode() != 200)
            {
                return year - 1;
            }

            con.disconnect();
        }
        catch (Exception e)
        {
            return year - 1;
        }

        return year;
    }

    public Profil getProfil(ProfilSmallData data)
    {
        return licee.get(data.getLiceuId()).getProfile().get(data.getSp());
    }

    public String getColor(String profil)
    {
        return this.colors.get(profil);
    }

    public int getEstimatedIndex(double medieEstimata)
    {
        try
        {
            URL url = new URL(String.format(evaluareCandidatesLink, getNewestYear(evaluareCandidatesLink)));

            try (InputStream inputStream = url.openStream())
            {
                JsonElementList list = GSON.fromJson(new InputStreamReader(inputStream), JsonElementList.class);

                int index = 0;

                for (JsonObject liceuData : list)
                {
                    double medie = liceuData.get("mev").getAsDouble();
                    index = liceuData.get("index").getAsInt();

                    if (medie < medieEstimata)
                    {
                        index--;
                        break;
                    }
                }

                return index;
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void searchForCandidateData(CandidateInfo info, Runnable after)
    {
        new Thread(() -> {
            String codMen = info.getCodMen();

            try
            {
                int year = LocalDateTime.now().getYear() - 1;

                URL url = new URL(String.format(evaluareCandidatesLink, year));
                URL url2 = new URL(String.format(candidatesLink, year));

                try (InputStream stream = url.openStream())
                {
                    JsonElementList list = GSON.fromJson(new InputStreamReader(stream), JsonElementList.class);

                    JsonObject candidateData = null;

                    for (JsonObject object : list)
                    {
                        if (object.get("name").getAsString().equals(codMen))
                        {
                            candidateData = object;
                            break;
                        }
                    }

                    if (candidateData == null)
                    {
                        if (after != null)
                        {
                            after.run();
                        }

                        return;
                    }

                    int index = candidateData.get("index").getAsInt();
                    String school = candidateData.get("school").getAsString();
                    double romana = candidateData.get("ra").isJsonNull() ? candidateData.get("ri")
                            .getAsDouble() : candidateData.get("ra").getAsDouble();
                    double matematica = candidateData.get("ma").isJsonNull() ? candidateData.get("mi")
                            .getAsDouble() : candidateData.get("ma").getAsDouble();
                    double medie = candidateData.get("mev").getAsDouble();

                    info.setEvData(school, romana, matematica, medie, index);
                }
                catch (IOException e)
                {
                    if (after != null)
                    {
                        after.run();
                    }
                    return;
                }

                try (InputStream stream = url2.openStream())
                {
                    JsonElementList list = GSON.fromJson(new InputStreamReader(stream), JsonElementList.class);

                    JsonObject candidateData = null;

                    for (JsonObject object : list)
                    {
                        if (object.get("n").getAsString().equals(codMen))
                        {
                            candidateData = object;
                            break;
                        }
                    }

                    if (candidateData == null)
                    {
                        if (after != null)
                        {
                            after.run();
                        }
                        return;
                    }

                    String medieGeneralaString = candidateData.get("mabs").getAsString();
                    String liceu = candidateData.get("h").getAsString();
                    String specializare = candidateData.get("sp").getAsString();

                    info.setAdmitereData(Double.parseDouble(medieGeneralaString), liceu, specializare);
                }
                catch (IOException e)
                {
                    if (after != null)
                    {
                        after.run();
                    }
                    return;
                }

                if (after != null)
                {
                    after.run();
                }
            }
            catch (MalformedURLException e)
            {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public Map<Long, Liceu> getLicee()
    {
        return licee;
    }

    public List<String> getSpecializari()
    {
        return specializari;
    }

    public static class JsonElementList extends ArrayList<JsonObject> {}

    public static class SaveData {
        private final Map<Long, Liceu> licee;

        private final List<String> specializari;
        private final Map<String, String> colors;

        public SaveData(Map<Long, Liceu> licee, List<String> specializari, Map<String, String> colors)
        {
            this.licee = licee;
            this.specializari = specializari;
            this.colors = colors;
        }

        public Map<String, String> getColors()
        {
            return colors;
        }

        public Map<Long, Liceu> getLicee()
        {
            return licee;
        }

        public List<String> getSpecializari()
        {
            return specializari;
        }
    }


}
