//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.05.19 at 01:50:00 PM CEST 
//


package mycellar.core.datas.worksheet;

import javax.xml.bind.annotation.XmlRegistry;


@XmlRegistry
class WorkSheetFactory {


  /**
   * Create a new WorkSheetFactory that can be used to create new instances of schema derived classes for package: generated
   */
  public WorkSheetFactory() {
  }

  /**
   * Create an instance of {@link WorkSheetList }
   */
  public WorkSheetList createWorkSheetList() {
    return new WorkSheetList();
  }

  /**
   * Create an instance of {@link WorkSheetData }
   */
  public WorkSheetData createWorkSheet() {
    return new WorkSheetData();
  }

}
