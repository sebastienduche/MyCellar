package mycellar.core.datas.jaxb.tracks;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "track")
@XmlAccessorType(XmlAccessType.FIELD)
public class Track {
  private Integer number;
  private String label;
  private String duration;
  private String comment;

  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public String getComment() {
    return comment;
  }

  public Track setComment(String comment) {
    this.comment = comment;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !Objects.equals(getClass(), o.getClass())) {
      return false;
    }
    Track track = (Track) o;
    return Objects.equals(number, track.number) && Objects.equals(label, track.label) && Objects.equals(duration, track.duration) && Objects.equals(comment, track.comment);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, label, duration, comment);
  }

  @Override
  public String toString() {
    return "Track{" +
        "number=" + number +
        ", label='" + label + '\'' +
        ", duration='" + duration + '\'' +
        ", comment='" + comment + '\'' +
        '}';
  }
}
