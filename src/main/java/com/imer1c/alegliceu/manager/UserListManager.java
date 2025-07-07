package com.imer1c.alegliceu.manager;

import com.google.gson.Gson;
import com.imer1c.alegliceu.manager.sort.Sorter;
import com.imer1c.alegliceu.util.RepartitionType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListManager {

    private static final Gson GSON = new Gson();
    private static final File save = new File("user_list");

    private final ObservableList<ProfilSmallData> userList = FXCollections.observableArrayList();
    private final Map<Long, LiceuSmallData> liceuUserData = new HashMap<>();
    private CandidateInfo candidateInfo;
    private String lastRunSimulation;

    public void load()
    {
        if (!save.exists())
        {
            return;
        }

        try (FileReader reader = new FileReader(save))
        {
            UserData userData = GSON.fromJson(reader, UserData.class);
            this.userList.addAll(userData.getUserList());
            this.liceuUserData.putAll(userData.getLiceuUserData());
            this.candidateInfo = userData.getCandidateInfo();
            this.lastRunSimulation = userData.getLastRunSimulation();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void save()
    {
        try (FileWriter writer = new FileWriter(save))
        {
            GSON.toJson(new UserData(userList, liceuUserData, candidateInfo, lastRunSimulation), writer);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setCodMen(String codMen)
    {
        this.candidateInfo = new CandidateInfo(codMen);
    }

    public void resetCandidateInfo()
    {
        this.candidateInfo = null;
    }

    public CandidateInfo getCandidateInfo()
    {
        return candidateInfo;
    }

    public ProfilSmallData get(int index)
    {
        return this.userList.get(index);
    }

    public LiceuSmallData getOrCreateLiceuData(long id)
    {
        return this.liceuUserData.computeIfAbsent(id, l -> new LiceuSmallData());
    }

    public void add(Profil profil)
    {
        this.userList.add(profil.createSmallData());
    }

    public void remove(Profil profil)
    {
        this.userList.remove(new ProfilSmallData(profil.getLiceuId(), profil.getSpecializare()));
    }

    public ObservableList<ProfilSmallData> getUserList()
    {
        return userList;
    }

    public boolean contains(Profil profil)
    {
        return userList.contains(new ProfilSmallData(profil.getLiceuId(), profil.getSpecializare()));
    }

    public void move(int from, int to)
    {
        ProfilSmallData fromData = userList.remove(from);
        userList.add(to, fromData);
    }

    public void runDifSimulation(DataManager dataManager, String id, int position, Runnable after)
    {
        new Thread(() -> {

            int index = position;

            List<Liceu> ordererdLiceeList = dataManager.getLicee()
                    .values()
                    .stream()
                    .sorted((o1, o2) -> Double.compare(o2.getMaxMedie(), o1.getMaxMedie()))
                    .toList();

            int i;
            boolean yellow = false;

            for (i = 0; i < ordererdLiceeList.size(); i++)
            {
                Liceu liceu = ordererdLiceeList.get(i);

                index -= liceu.getLocuri();

                if (index <= 0)
                {
                    if (index < 0)
                    {
                        yellow = true;
                    }

                    break;
                }
            }

            ordererdLiceeList.subList(i, ordererdLiceeList.size()).forEach(l -> {
                getOrCreateLiceuData(l.getId()).setRepartitionType(RepartitionType.EMPTY);
            });

            if (yellow)
            {
                getOrCreateLiceuData(ordererdLiceeList.get(i).getId()).setRepartitionType(RepartitionType.SOME_REMAIN);
            }

            ordererdLiceeList.subList(0, i).forEach(l -> {
                getOrCreateLiceuData(l.getId()).setRepartitionType(RepartitionType.FULL);
            });

            this.lastRunSimulation = id;

            after.run();
        }).start();
    }

    public void runComparationSimulation(DataManager dataManager, int index, Runnable after)
    {
        new Thread(() -> {
            for (Liceu liceu : dataManager.getLicee().values())
            {
                LiceuSmallData liceuData = getOrCreateLiceuData(liceu.getId());

                for (Profil profil : liceu.getProfile().values())
                {
                    int profilIndex = profil.getIndex();

                    if (profilIndex == -1)
                    {
                        liceuData.putProfilColor(profil.getSpecializare(), "gray");
                    }
                    else if (index < profilIndex)
                    {
                        liceuData.putProfilColor(profil.getSpecializare(), "green");
                    }
                    else if (index == profilIndex)
                    {
                        liceuData.putProfilColor(profil.getSpecializare(), "yellow");
                    }
                    else
                    {
                        liceuData.putProfilColor(profil.getSpecializare(), "red");
                    }
                }
            }

            after.run();
        }).start();
    }

    public boolean hasCandidateInfo()
    {
        return candidateInfo != null && candidateInfo.getMedie() > 0;
    }

    public void sort(Sorter sorter, int from, int to, DataManager dataManager)
    {
        Comparator<PrintSettings.Item> itemComparator = sorter.buildComparator();

        List<ProfilSmallData> list = userList
                .subList(Math.max(from, 0), to + 1)
                .stream()
                .map(d -> new PrintSettings.Item(dataManager.getProfil(d), getOrCreateLiceuData(d.getLiceuId())))
                .sorted(itemComparator)
                .map(i -> i.getProfil().createSmallData())
                .toList();

        for (int i = from; i <= to; i++)
        {
            this.userList.set(i, list.get(i - from));
        }
    }

    public static class UserData {
        private final List<ProfilSmallData> userList;
        private final Map<Long, LiceuSmallData> liceuUserData;
        private final CandidateInfo candidateInfo;
        private final String lastRunSimulation;

        public UserData(List<ProfilSmallData> userList, Map<Long, LiceuSmallData> liceuUserData, CandidateInfo candidateInfo, String lastRunSimulation)
        {
            this.userList = userList;
            this.liceuUserData = liceuUserData;
            this.candidateInfo = candidateInfo;
            this.lastRunSimulation = lastRunSimulation;
        }

        public String getLastRunSimulation()
        {
            return lastRunSimulation;
        }

        public Map<Long, LiceuSmallData> getLiceuUserData()
        {
            return liceuUserData;
        }

        public List<ProfilSmallData> getUserList()
        {
            return userList;
        }

        public CandidateInfo getCandidateInfo()
        {
            return candidateInfo;
        }
    }

}
