package com.example.patrolinspection.db;

public class Type
{
    private String name;
    private int imageID;
    private int imagePressID;
    private String typeName;

    public Type(String name, int imageID, int imagePressID, String typeName)
    {
        this.name = name;
        this.imageID = imageID;
        this.imagePressID = imagePressID;
        this.typeName = typeName;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getImageID()
    {
        return imageID;
    }

    public void setImageID(int imageID)
    {
        this.imageID = imageID;
    }

    public int getImagePressID()
    {
        return imagePressID;
    }

    public void setImagePressID(int imagePressID)
    {
        this.imagePressID = imagePressID;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }
}