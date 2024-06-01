package org.coffe.controller;

import org.coffe.entities.Plant;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GUI {
    private JFrame frame;
    private JTextField nameField;
    private JTextField plantDateField;
    private JTextField fertilizePeriodField;
    private JTextField harvestPeriodField;
    private JButton addButton;
    private JButton removeButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton checkButton;
    private JList<String> plantList;
    private DefaultListModel<String> listModel;
    private ArrayList<Plant> plants;
    private JTextArea plantDetailsArea;
    private Timer timer;

    public GUI() {
        plants = new ArrayList<>();

        frame = new JFrame("Plant Reminder App");
        frame.setSize(400, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel nameLabel = new JLabel("Plant Name:");
        nameLabel.setBounds(10, 20, 100, 25);
        frame.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(170, 20, 165, 25);
        frame.add(nameField);

        JLabel plantDateLabel = new JLabel("Plant Date (yyyy-mm-dd):");
        plantDateLabel.setBounds(10, 50, 150, 25);
        frame.add(plantDateLabel);

        plantDateField = new JTextField();
        plantDateField.setBounds(170, 50, 165, 25);
        frame.add(plantDateField);

        JLabel fertilizePeriodLabel = new JLabel("Fertilize Period (days):");
        fertilizePeriodLabel.setBounds(10, 80, 150, 25);
        frame.add(fertilizePeriodLabel);

        fertilizePeriodField = new JTextField();
        fertilizePeriodField.setBounds(170, 80, 165, 25);
        frame.add(fertilizePeriodField);

        JLabel harvestPeriodLabel = new JLabel("Harvest Period (days):");
        harvestPeriodLabel.setBounds(10, 110, 150, 25);
        frame.add(harvestPeriodLabel);

        harvestPeriodField = new JTextField();
        harvestPeriodField.setBounds(170, 110, 165, 25);
        frame.add(harvestPeriodField);

        addButton = new JButton("Add Plant");
        addButton.setBounds(10, 140, 325, 25);
        frame.add(addButton);

        removeButton = new JButton("Remove Plant");
        removeButton.setBounds(10, 170, 325, 25);
        frame.add(removeButton);

        saveButton = new JButton("Save Plants");
        saveButton.setBounds(10, 200, 160, 25);
        frame.add(saveButton);

        loadButton = new JButton("Load Plants");
        loadButton.setBounds(175, 200, 160, 25);
        frame.add(loadButton);

        checkButton = new JButton("Check Reminders");
        checkButton.setBounds(10, 230, 325, 25);
        frame.add(checkButton);

        listModel = new DefaultListModel<>();
        plantList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(plantList);
        listScrollPane.setBounds(10, 260, 325, 150);
        frame.add(listScrollPane);

        plantDetailsArea = new JTextArea();
        plantDetailsArea.setBounds(10, 420, 325, 150);
        plantDetailsArea.setEditable(false);
        frame.add(plantDetailsArea);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String plantDate = plantDateField.getText();
                int fertilizePeriod = Integer.parseInt(fertilizePeriodField.getText());
                int harvestPeriod = Integer.parseInt(harvestPeriodField.getText());

                Plant plant = new Plant(name, plantDate, fertilizePeriod, harvestPeriod);
                plants.add(plant);
                listModel.addElement(plant.getName());

                JOptionPane.showMessageDialog(frame, "Plant Added: " + plant.getName());
            }
        });

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = plantList.getSelectedIndex();
                if (selectedIndex != -1) {
                    listModel.remove(selectedIndex);
                    plants.remove(selectedIndex);
                    plantDetailsArea.setText("");
                    JOptionPane.showMessageDialog(frame, "Plant Removed.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a plant to remove.");
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("plants.dat"))) {
                    oos.writeObject(plants);
                    JOptionPane.showMessageDialog(frame, "Plants saved successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error saving plants: " + ex.getMessage());
                }
            }
        });

        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("plants.dat"))) {
                    plants = (ArrayList<Plant>) ois.readObject();
                    listModel.clear();
                    for (Plant plant : plants) {
                        listModel.addElement(plant.getName());
                    }
                    JOptionPane.showMessageDialog(frame, "Plants loaded successfully.");
                    checkReminders();
                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(frame, "Error loading plants: " + ex.getMessage());
                }
            }
        });

        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkReminders();
            }
        });

        plantList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList<String> list = (JList<String>) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    Plant selectedPlant = plants.get(index);
                    plantDetailsArea.setText("Name: " + selectedPlant.getName() + "\n" +
                            "Plant Date: " + selectedPlant.getPlantDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n" +
                            "Next Fertilize Date: " + selectedPlant.getNextFertilizeDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n" +
                            "Next Harvest Date: " + selectedPlant.getNextHarvestDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
            }
        });

        startReminderCheck();

        frame.setVisible(true);
    }

    private void startReminderCheck() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                checkReminders();
            }
        }, 0, 24 * 60 * 60 * 1000); // Verificar una vez al día
    }

    private void checkReminders() {
        LocalDate today = LocalDate.now();
        for (Plant plant : plants) {
            if (plant.getNextFertilizeDate().equals(today)) {
                JOptionPane.showMessageDialog(frame, "Time to fertilize: " + plant.getName());
            }
            if (plant.getNextHarvestDate().equals(today)) {
                JOptionPane.showMessageDialog(frame, "Time to harvest: " + plant.getName());
            }
        }
    }

    public static void main(String[] args) {
        new GUI();
    }
}
