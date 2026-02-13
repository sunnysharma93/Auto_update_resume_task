package com.resume.Auto_resume_update.dto;

public class EditRequest {
    private String newExperience;
    private String modifiedSkill;
    private String newCertification;
    private String additionalText; // Optional, agar chahiye
    private String name;
    private String gmail;
    private String number;
    private String summary;
    private String education;
    private String projects;
    private String languages;

    // Getters and setters
    public String getNewExperience() { return newExperience; }
    public void setNewExperience(String newExperience) { this.newExperience = newExperience; }
    public String getModifiedSkill() { return modifiedSkill; }
    public void setModifiedSkill(String modifiedSkill) { this.modifiedSkill = modifiedSkill; }
    public String getNewCertification() { return newCertification; }
    public void setNewCertification(String newCertification) { this.newCertification = newCertification; }
    public String getAdditionalText() { return additionalText; }
    public void setAdditionalText(String additionalText) { this.additionalText = additionalText; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGmail() { return gmail; }
    public void setGmail(String gmail) { this.gmail = gmail; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    public String getProjects() { return projects; }
    public void setProjects(String projects) { this.projects = projects; }
    public String getLanguages() { return languages; }
    public void setLanguages(String languages) { this.languages = languages; }
}