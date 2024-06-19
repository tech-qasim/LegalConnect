package com.example.blacksuits.DataClass;

public class LegalAgreementDataClass {
    private String title;
    private String content;

    // Constructors
    public LegalAgreementDataClass() {
        // Default constructor
    }

    public LegalAgreementDataClass(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getter and Setter methods
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return "TitleAndString{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
