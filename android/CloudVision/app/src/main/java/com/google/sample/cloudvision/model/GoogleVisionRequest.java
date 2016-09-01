package com.google.sample.cloudvision.model;

import java.util.ArrayList;

public class GoogleVisionRequest
{
  ArrayList<AnnotateImageRequest> requests;
  private transient AnnotateImageRequest requestCurrent;

  public GoogleVisionRequest(String image, String c)
  {
    this.requests = new ArrayList<AnnotateImageRequest>();
    
    requestCurrent = new AnnotateImageRequest(image, c);
    this.requests.add(requestCurrent);
  }

  public void addRequest(String image, String c)
  {
    requestCurrent = new AnnotateImageRequest(image, c);
    this.requests.add(requestCurrent);
  }

  public void addFeature(String type, int max)
  {
    Feature feat = new Feature(type, max);
    requestCurrent.features.add(feat);
  }

  /*******************************************************/
  private class AnnotateImageRequest
  {
    Image image;
    ArrayList<Feature> features;
    //String imageContext;

    public AnnotateImageRequest(String img, String imageContext)
    {
      this.image        = new Image(img);
      //this.imageContext = imageContext;
      this.features     = new ArrayList<Feature>();
    }
  }
  /*******************************************************/
  private class Image
  {
    String content;

    public Image(String img)
    {
      this.content = img;
    }
  }
  /*******************************************************/
  private class ImageContext
  {
  }
  /*******************************************************/
  private class Feature
  {
    String type;
    int maxResults;

    public Feature(String type, int max)
    {
      this.type       = type;
      this.maxResults = max;
    }
  }
}
