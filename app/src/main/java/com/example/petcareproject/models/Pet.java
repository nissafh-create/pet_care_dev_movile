package com.example.petcareproject.models;

public class Pet {

    private String petId;
    private String ownerId;
    private String name;
    private String species;
    private String breed;
    private int age;
    private String specialNeeds;

    public Pet() {
    }

    public Pet(String petId, String ownerId, String name, String species, String breed, int age, String specialNeeds) {
        this.petId = petId;
        this.ownerId = ownerId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.specialNeeds = specialNeeds;
    }

    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getSpecialNeeds() { return specialNeeds; }
    public void setSpecialNeeds(String specialNeeds) { this.specialNeeds = specialNeeds; }
}
