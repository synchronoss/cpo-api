package org.synchronoss.cpo;

public class CpoNativeQuery {

  private String marker=null;
  private String nativeText=null;
  
  public CpoNativeQuery(){}
  
  public CpoNativeQuery(String marker, String text){
    this.marker=marker;
    this.nativeText = text;
  }
  
  public void setMarker(String marker){
    this.marker=marker;
  }
  
  public String getMarker(){
    return this.marker;
  }
  
  public void setNativeText(String text){
    this.nativeText = text;
  }
  
  public String getNativeText(){
    return this.nativeText;
  }
  
}
