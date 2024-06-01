package org.coffe.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Plant implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private LocalDate plantDate;
    private int fertilizePeriodDays;
    private int harvestPeriodDays;

    public Plant(String name, String plantDate, int fertilizePeriodDays, int harvestPeriodDays) {
        this.name = name;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.plantDate = LocalDate.parse(plantDate, formatter);
        this.fertilizePeriodDays = fertilizePeriodDays;
        this.harvestPeriodDays = harvestPeriodDays;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getPlantDate() {
        return plantDate;
    }

    public void setPlantDate(String plantDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.plantDate = LocalDate.parse(plantDate, formatter);
    }

    public int getFertilizePeriodDays() {
        return fertilizePeriodDays;
    }

    public void setFertilizePeriodDays(int fertilizePeriodDays) {
        this.fertilizePeriodDays = fertilizePeriodDays;
    }

    public int getHarvestPeriodDays() {
        return harvestPeriodDays;
    }

    public void setHarvestPeriodDays(int harvestPeriodDays) {
        this.harvestPeriodDays = harvestPeriodDays;
    }

    public LocalDate getNextFertilizeDate() {
        LocalDate today = LocalDate.now();
        long daysElapsed = plantDate.until(today).getDays();
        long periodsElapsed = daysElapsed / fertilizePeriodDays;
        LocalDate nextFertilizeDate = plantDate.plusDays((periodsElapsed + 1) * fertilizePeriodDays);

        // Si la siguiente fecha de fertilización es antes de hoy, ajustarla
        while (nextFertilizeDate.isBefore(today)) {
            nextFertilizeDate = nextFertilizeDate.plusDays(fertilizePeriodDays);
        }
        return nextFertilizeDate;
    }

    public LocalDate getNextHarvestDate() {
        LocalDate today = LocalDate.now();
        long daysElapsed = plantDate.until(today).getDays();
        long periodsElapsed = daysElapsed / harvestPeriodDays;
        LocalDate nextHarvestDate = plantDate.plusDays((periodsElapsed + 1) * harvestPeriodDays);

        // Si la siguiente fecha de cosecha es antes de hoy, ajustarla
        while (nextHarvestDate.isBefore(today)) {
            nextHarvestDate = nextHarvestDate.plusDays(harvestPeriodDays);
        }
        return nextHarvestDate;
    }
}
