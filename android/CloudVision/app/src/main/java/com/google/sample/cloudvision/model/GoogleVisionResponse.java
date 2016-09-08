package com.google.sample.cloudvision.model;

//import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import java.util.List;
import java.util.ArrayList;

import com.google.sample.cloudvision.model.AnnotateImageResponse;

/******************************************************************************/
public class GoogleVisionResponse 
{
  private List<AnnotateImageResponse> responses;

  /********************************************/
  public List<AnnotateImageResponse> getResponses()
  {
    if(this.responses != null)
      return this.responses;
    else
      return new ArrayList<AnnotateImageResponse>();
  }
}
