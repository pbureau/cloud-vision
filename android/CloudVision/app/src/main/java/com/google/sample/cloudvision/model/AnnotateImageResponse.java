package com.google.sample.cloudvision.model;

import java.util.List;
import java.util.ArrayList;

import com.google.sample.cloudvision.model.EntityAnnotation;

/******************************************************************************/
public class AnnotateImageResponse
{
  private List<EntityAnnotation> logoAnnotations;
  private List<EntityAnnotation> landmarkAnnotations;
  private List<EntityAnnotation> labelAnnotations;
  private List<EntityAnnotation> textAnnotations;

  /********************************************/
  public List<EntityAnnotation> getLandmarks()
  {
    if(landmarkAnnotations != null)
      return this.landmarkAnnotations;
    else
      return new ArrayList<EntityAnnotation>();
  }
  /********************************************/
  public List<EntityAnnotation> getLogos()
  {
    if(logoAnnotations != null)
      return this.logoAnnotations;
    else
      return new ArrayList<EntityAnnotation>();
  }
  /********************************************/
  public List<EntityAnnotation> getLabels()
  {
    if(labelAnnotations != null)
      return this.labelAnnotations;
    else
      return new ArrayList<EntityAnnotation>();
  }
  /********************************************/
  public List<EntityAnnotation> getTexts()
  {
    if(textAnnotations != null)
      return this.textAnnotations;
    else
      return new ArrayList<EntityAnnotation>();
  }
}
